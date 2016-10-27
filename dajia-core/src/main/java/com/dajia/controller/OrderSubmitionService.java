package com.dajia.controller;

import com.dajia.domain.User;
import com.dajia.domain.UserContact;
import com.dajia.domain.UserOrder;
import com.dajia.domain.UserOrderItem;
import com.dajia.repository.UserOrderItemRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRepo;
import com.dajia.service.*;
import com.dajia.util.CommonUtils;
import com.dajia.util.DajiaResult;
import com.dajia.vo.CartItemVO;
import com.dajia.vo.OrderVO;
import com.pingplusplus.exception.PingppException;
import com.pingplusplus.model.Charge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dajia.util.ResultConstants.COMMON_MSG_QUERY_OK;

/**
 * 订单创建是个比较复杂的业务 中间要经历好几个步骤
 * 放到一个统一的service来处理
 *
 * Created by huhaonan on 2016/10/23.
 */
@Service
public class OrderSubmitionService {

    @Autowired
    public UserRepo userRepo;

    @Autowired
    public UserOrderRepo orderRepo;

    @Autowired
    public OrderService orderService;

    @Autowired
    public CartService cartService;

    @Autowired
    public UserOrderItemRepo userOrderItemRepo;

    @Autowired
    public ProductService productService;

    @Autowired
    public UserCouponService userCouponService;

    @Autowired
    public ApiService apiService;

    @Autowired
    public UserContactService userContactService;

    final static Logger logger = LoggerFactory.getLogger(OrderSubmitionService.class);

    /**
     * 处理订单请求
     *
     * @param orderVO
     * @param user
     * @return
     */
    @Transactional
    public Charge handleOrderSubmitionRequest(OrderVO orderVO, User user) {

        if (null == orderVO) {
            return null;
        }

        /**
         * 验证是否超卖
         */
        if (!productService.validateStock(orderVO)) {
            return null;
        }

        /**
         * 创建一个待保存的订单
         */
        UserOrder order = createNewOrder(orderVO, user);

        /**
         * 购物车加入Order
         */
        addCartItemsToOrder(order, orderVO.cartItems);

        /**
         * 检查Order合法性 主要是检查
         *
         * 1. 订单总价totalPrice 不会小于 商品总价值 + 运费
         * 2. 商品的单价 不小于 当前数据库中该商品的单价
         */
        if (!orderService.orderValidate(order)) {
            logger.error("validate order failed, orderVo={}", orderVO);
            return null;
        }

        /************************************************************/
        /** 优惠券相关计算 **/
        /************************************************************/
        DajiaResult actualPayResult = calc_cut_off_for_order(order, orderVO.appliedCoupons, user);

        if(actualPayResult.isNotSucceed()) {
            logger.error("calc actual pay failed, orderVo={}, result={}", orderVO, actualPayResult);
            return null;
        }

        order.actualPay = order.totalPrice.add(((BigDecimal) actualPayResult.data).negate());

        /************************************************************/
        /** 开始事务 **/
        /************************************************************/
        try {
            orderRepo.save(order);
        } catch (Exception ex) {
            logger.error("order save failed, orderVO={}, order={}", orderVO, order, ex);
            throw new RuntimeException("保存订单信息失败,系统异常");
        }

        logger.info("order save succeed, orderVo={}, order={}", orderVO, order);

        DajiaResult consumeCouponResult = userCouponService.consumeUserCoupons(user.userId, order.orderId, orderVO.appliedCoupons);

        if (consumeCouponResult.isNotSucceed()) {
            logger.error("consume coupons failed, orderVo={}, result={}", orderVO, consumeCouponResult);
            throw new RuntimeException(consumeCouponResult.userMsg);
        }

        logger.info("consume user coupons succeed, orderVo={}", orderVO);

        if (null != orderVO.cartItems) {
            for (CartItemVO cartItem : orderVO.cartItems) {
                cartService.removeFromCart(user.userId, cartItem.productId);
            }
        }

        /*********************************************/
        /** 生成支付ID 开始一个新的事务 不影响本次事务 **/
        /*********************************************/
        return getCharge(user, order);
    }

    /**
     * 向平台支付 更新状态为已支付 这是一个单独的事务 不影响orderSubmition
     *
     * @param user
     * @param order
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private Charge getCharge(User user, UserOrder order) {
        Charge charge = null;
        try {
            charge = apiService.getPingppCharge(order, user, CommonUtils.getPayTypeStr(order.payType));
            order.pingxxCharge = charge.toString();
            order.paymentId = charge.getId();
            orderRepo.save(order);
        } catch (PingppException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("订单支付失败", e);
        }
        return charge;
    }


    /**
     * 计算减价逻辑
     *
     * @param order
     * @param couponPkList
     * @param user
     * @return
     */
    @Transactional(readOnly = true)
    public DajiaResult calc_cut_off_for_order(UserOrder order, List<Long> couponPkList, User user) {

        // 没有使用优惠券
        if (CollectionUtils.isEmpty(couponPkList)) {
            return DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, BigDecimal.ZERO);
        }

        // 计算实付价格
        DajiaResult totalOffResult = userCouponService.getTotalCutOffWithCoupons(couponPkList, user.userId);

        if(totalOffResult.isNotSucceed()) {
            return totalOffResult;
        }

        BigDecimal totalOff = ((BigDecimal) (totalOffResult.data)).negate();

        logger.info("total price cut off, order={}, couponPkList={}, totalOff={}", order, couponPkList, totalOff);
        return DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, order.totalPrice.add(totalOff));
    }

    /**
     * 创建待保存的OrderItems
     *
     * @param order
     * @param cartItems
     */
    public void addCartItemsToOrder(UserOrder order, List<CartItemVO> cartItems) {
        if (null == cartItems || cartItems.isEmpty()) {
            return;
        }
        List<UserOrderItem> orderItems = new ArrayList<UserOrderItem>();

        for (CartItemVO cartItem : cartItems) {
            UserOrderItem oi = new UserOrderItem();
            oi.userOrder = order;
            oi.trackingId = order.trackingId;
            oi.userId = order.userId;
            oi.productId = cartItem.productId;
            oi.productItemId = cartItem.productItemId;
            oi.productShared = CommonUtils.ProductShared.NO.toString();
            oi.unitPrice = cartItem.currentPrice;
            oi.quantity = cartItem.quantity;
            orderItems.add(oi);
        }
        order.orderItems = orderItems;
    }

    /**
     * 创建一个待保存的订单 调用来自 submitOrder
     *
     * @param orderVO
     * @param user
     * @return
     */
    public UserOrder createNewOrder(OrderVO orderVO, User user) {
        UserContact uc = orderVO.userContact;
        if (null != uc) {
            uc = userContactService.updateUserContact(uc, user);
        }
        UserOrder order = new UserOrder();
        order.unitPrice = orderVO.unitPrice;
        order.totalPrice = orderVO.totalPrice;
        order.postFee = orderVO.postFee;
        order.quantity = orderVO.quantity;
        order.payType = orderVO.payType;
        order.productId = orderVO.productId;
        order.productItemId = orderVO.productItemId;
        order.productDesc = orderVO.productDesc;
        order.productShared = CommonUtils.ProductShared.NO.toString();
        order.userComments = orderVO.userComments;

        if (null != orderVO.refUserId && orderVO.refUserId.longValue() != user.userId.longValue()) {
            order.refUserId = orderVO.refUserId;
            order.refOrderId = orderVO.refOrderId;
        }
        order.orderDate = new Date();

        /** 设置为pending **/
        order.orderStatus = CommonUtils.OrderStatus.PENDING_PAY.getKey();

        order.userId = user.userId;
        order.contactName = uc.contactName;
        order.contactMobile = uc.contactMobile;
        order.address = uc.province.locationValue + " " + uc.city.locationValue + " " + uc.district.locationValue + " "
                + uc.address1;
        order.trackingId = CommonUtils.genTrackingId(user.userId);
        return order;
    }

}
