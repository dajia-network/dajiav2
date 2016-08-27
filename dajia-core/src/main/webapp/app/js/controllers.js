angular.module('dajia.controllers', [ "ui.bootstrap", "countTo" ])

.controller('TabsCtrl', function($scope, $window) {
	$scope.select = function(tab) {
		if (tab == 'product') {
			$window.location.href = '#/tab/prod';
		} else if (tab == 'progress') {
			$window.location.href = '#/tab/prog';
		} else if (tab == 'cart') {
			$window.location.href = '#/tab/cart';
		} else if (tab == 'mine') {
			$window.location.href = '#/tab/mine';
		}
	}
})

.controller('ProdCtrl', function($scope, $http, $cookies, $ionicLoading, $window, AuthService, $timeout) {
	console.log('产品列表...');
	$scope.products = [];
	$scope.countDowns = [];
	$scope.clocks = [];
	$scope.page = {
		hasMore : false,
		pageNo : 1
	};
	var loadProducts = function() {
		return $http.get('/products/' + $scope.page.pageNo).success(function(data, status, headers, config) {
			$scope.page.hasMore = data.hasNext;
			$scope.page.pageNo = data.currentPage;
			$scope.products = $scope.products.concat(data.results);
			$scope.products.forEach(function(p) {
				var countDown = {
					key : "clock-" + p.product.productId,
					targetDate : p.expiredDate
				}
				$scope.countDowns.push(countDown);
			});
		});
	}
	initWechatJSAPI('home', $http);
	checkOauthLogin($cookies, $http, AuthService);
	popLoading($ionicLoading);
	loadProducts().then(function() {
		$ionicLoading.hide();
		renderCountDown();
	});

	$scope.doRefresh = function() {
		console.log('refresh...');
		$scope.products = [];
		$scope.page.pageNo = 1;
		clearCountDowns();
		loadProducts().then(function() {
			$scope.$broadcast('scroll.refreshComplete');
			renderCountDown();
		});
	}
	$scope.go2Product = function(productId) {
		clearCountDowns();
		$window.location.href = '#/tab/prod/' + productId;
	}
	$scope.loadMore = function() {
		console.log('load more...');
		clearCountDowns();
		$scope.page.pageNo += 1;
		loadProducts().then(function() {
			$scope.$broadcast('scroll.infiniteScrollComplete');
			renderCountDown();
		});
	}
	var renderCountDown = function() {
		angular.element(document).ready(function() {

			$timeout(function() {
				$scope.countDowns.forEach(function(cd) {
					var targetDate = new Date(cd.targetDate);
					var countdown = document.getElementById(cd.key);
					DajiaGlobal.utils.getCountdown(countdown, targetDate);
					var clock = setInterval(function() {
						DajiaGlobal.utils.getCountdown(countdown, targetDate);
					}, 1000);
					$scope.clocks.push(clock);
				})
			}, 500);
		});
	}
	var clearCountDowns = function() {
		$scope.clocks.forEach(function(c) {
			clearInterval(c);
		});
		$scope.countDowns = [];
		$scope.clocks = [];
	}
})

.controller(
		'ProdDetailCtrl',
		function($scope, $rootScope, $stateParams, $http, $cookies, $window, $timeout, $ionicSlideBoxDelegate,
				$ionicModal, $ionicLoading, AuthService) {
			console.log('产品详情...')
			$scope.favBtnTxt = '收藏';
			var element = angular.element(document.querySelector('#fav_icon'));
			modalInit($rootScope, $ionicModal, 'login');
			shareModalInit($scope, $ionicModal);

			$http.get('/user/checkfav/' + $stateParams.pid).success(function(data, status, headers, config) {
				var isFav = data;
				$scope.isFav = isFav;
				if ($scope.isFav) {
					$scope.favBtnTxt = '已收藏';
					element.addClass('assertive');
				}
			}).error(function(data, status, headers, config) {
				console.log('request failed...');
			});

			$scope.buyNow = function() {
				if ($cookies.get('dajia_user_id') == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					$window.location.href = '#/tab/prodorder/' + $stateParams.pid;
				}
			}

			$scope.add2Cart = function() {
				if ($cookies.get('dajia_user_id') == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					$http.get('/user/cart/add/' + $stateParams.pid).success(function(data, status, headers, config) {
						popWarning('已放入购物车', $timeout, $ionicLoading);
					}).error(function(data, status, headers, config) {
						console.log('request failed...');
					});
				}
			}

			$scope.add2Fav = function() {
				if ($cookies.get('dajia_user_id') == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					if ($scope.isFav) {
						$http.get('/user/favourite/remove/' + $stateParams.pid).success(
								function(data, status, headers, config) {
									popWarning('已取消收藏', $timeout, $ionicLoading);
									$scope.isFav = false;
									$scope.favBtnTxt = '收藏';
									element.removeClass('assertive');
								}).error(function(data, status, headers, config) {
							console.log('request failed...');
						});
					} else {
						$http.get('/user/favourite/add/' + $stateParams.pid).success(
								function(data, status, headers, config) {
									popWarning('收藏成功', $timeout, $ionicLoading);
									$scope.isFav = true;
									$scope.favBtnTxt = '已收藏';
									element.addClass('assertive');
								}).error(function(data, status, headers, config) {
							console.log('request failed...');
						});
					}
				}
			}
			$scope.share = function() {
				popWarning('请点击右上角微信菜单-发送给朋友。如果您已购买该产品，成功推荐朋友购买后将享受额外奖励折扣。', $timeout, $ionicLoading);
			}
			$scope.back = function() {
				$window.location.replace('#/tab/prod');
			}
			popLoading($ionicLoading);
			checkOauthLogin($cookies, $http, AuthService);
			$http.get('/product/' + $stateParams.pid).success(
					function(data, status, headers, config) {
						var product = data;
						$scope.product = product;
						$ionicSlideBoxDelegate.update();
						var amt = (product.originalPrice - product.currentPrice)
								/ (product.originalPrice - product.targetPrice) * 100;
						$scope.countTo = product.currentPrice;
						$scope.countFrom = product.originalPrice;
						initWechatJSAPI('product', $http, $cookies, $timeout, $ionicLoading, $scope.product);
						$ionicLoading.hide();
						$timeout(function() {
							$scope.progressValue = amt;
						}, 1000);

						var targetDate = new Date(product.expiredDate);

						// save share log
						var productId = DajiaGlobal.utils.getURLParameter('productId');
						var userId = $cookies.get('dajia_user_id');
						if (null != userId) {
							userId = Number(userId);
						}
						if (null != productId) {
							productId = Number(productId);
							var refUserId = DajiaGlobal.utils.getURLParameter('refUserId');
							var refOrderId = DajiaGlobal.utils.getURLParameter('refOrderId');
							if (null != refUserId) {
								refUserId = Number(refUserId);
							}
							if (null != refOrderId) {
								refOrderId = Number(refOrderId);
							}
							var shareLog = {
								visitUrl : window.location.href,
								productId : product.productId,
								productItemId : product.productItemId,
								refUserId : refUserId,
								userId : userId
							}
							$http.post('/user/sharelog', shareLog);
						}
						// save visit log
						var visitLog = {
							visitUrl : window.location.href,
							productId : product.productId,
							productItemId : product.productItemId,
							userId : userId
						}
						$http.post('/user/visitlog', visitLog);

						// add user share
						$http.get('/product/share/' + productId + '/' + refOrderId).success(
								function(data, status, headers, config) {
									$scope.openShareModal(data);
								});
						if (null != refUserId && null != refOrderId && null != userId && null != productId
								&& product.isPromoted == 'Y' && refUserId != userId) {
							var userShare = {
								productId : product.productId,
								productItemId : product.productItemId,
								userId : refUserId,
								visitUserId : userId,
								orderId : refOrderId
							}
							$http.post('/user/addUserShare', userShare);
						}
					});
			$scope.progressValue = 0;
		})

.controller(
		'OrderCtrl',
		function($scope, $rootScope, $stateParams, $http, $window, $ionicModal, $timeout, $ionicLoading) {
			console.log('订单页面...')
			var productReady = false;
			var locationReady = false;
			popLoading($ionicLoading);
			modalInit($rootScope, $ionicModal, 'login');
			userAgreeModalInit($scope, $ionicModal);
			$scope.userContact = {};
			$scope.userContacts = [];
			$scope.selectedUserContact = {};
			$scope.userAgree = {
				checked : true
			};
			$http.get('/user/loginuserinfo').success(
					function(data, status, headers, config) {
						var loginuser = data;
						$scope.loginuser = loginuser;
						if (null != $scope.loginuser.userContacts) {
							loginuser.userContacts.forEach(function(c) {
								var contact = {
									contactId : c.contactId,
									name : c.contactName,
									mobile : c.contactMobile,
									address : c.province.locationValue + ' ' + c.city.locationValue + ' '
											+ c.district.locationValue + ' ' + c.address1,
									summary : c.contactName + ' ' + c.contactMobile + ' ' + c.province.locationValue
											+ ' ' + c.city.locationValue + ' ' + c.district.locationValue + ' '
											+ c.address1
								};
								$scope.userContacts.push(contact);
							});
							// console.log($scope.userContacts);
						}
					}).error(function(data, status, headers, config) {
				console.log('request failed...');
			});
			$http.get('/locations').success(function(data, status, headers, config) {
				$scope.provinces = data;
				if ($scope.loginuser.userContact != null) {
					$scope.userContact = $scope.loginuser.userContact;
					fillLocationDropdowns($scope, $ionicLoading, locationReady);
				} else {
					if (productReady) {
						$ionicLoading.hide();
					}
					locationReady = true;
				}
			}).error(function(data, status, headers, config) {
				console.log('request failed...');
			});
			if ($window.location.hash.indexOf('prodorder') >= 0) {
				console.log('single product order');
				$scope.order = {
					'quantity' : 1,
					'unitPrice' : 0,
					'totalPrice' : 0,
					'payType' : 1
				};
				var quota = 2;
				$http.get('/product/' + $stateParams.pid).success(function(data, status, headers, config) {
					var product = data;
					$scope.orderItem = product;
					// $scope.totalPrice = product.price;
					quota = product.buyQuota;
					$scope.order.productId = product.productId;
					$scope.order.productItemId = product.productItemId;
					$scope.order.unitPrice = product.currentPrice;
					$scope.order.postFee = product.postFee;
					$scope.defaultPostFee = product.postFee;
					$scope.order.totalPrice = $scope.order.quantity * $scope.order.unitPrice + $scope.order.postFee;
					$scope.order.productDesc = product.shortName;
					if (locationReady) {
						$ionicLoading.hide();
					}
					productReady = true;
				});
			} else if ($window.location.hash.indexOf('cartorder') >= 0) {
				console.log('cart order');
				$scope.order = {
					'totalPrice' : 0,
					'payType' : 1
				};
				$http.get('/user/cartorder').success(function(data, status, headers, config) {
					$scope.cartItems = data;
					var postFee = 0;
					var totalPrice = 0;
					var productDesc = null;
					if (null != $scope.cartItems) {
						$scope.cartItems.forEach(function(c) {
							if (postFee < c.postFee) {
								postFee = c.postFee;
							}
							if (null == productDesc) {
								productDesc = c.shortName;
							}
							totalPrice += c.currentPrice * c.quantity;
						});
						if ($scope.cartItems.length > 1) {
							productDesc = productDesc + '等' + $scope.cartItems.length + '件产品';
						}
					}
					$scope.order.postFee = postFee;
					$scope.defaultPostFee = postFee;
					$scope.order.totalPrice = totalPrice + postFee;
					$scope.order.productDesc = productDesc;
					// console.log($scope.order);
					if (locationReady) {
						$ionicLoading.hide();
					}
					productReady = true;
				});
			}
			$scope.submit = function() {
				console.log($scope.order);
				if (!$scope.userAgree.checked) {
					popWarning('购买前请确认并同意打价网用户协议，谢谢！', $timeout, $ionicLoading);
					return;
				}
				if ($scope.userContact.contactId == null) {
					console.log('new userContact.');
				}
				var name = $scope.userContact.contactName;
				var mobile = $scope.userContact.contactMobile;
				var province = $scope.userContact.province;
				var city = $scope.userContact.city;
				var district = $scope.userContact.district;
				var address = $scope.userContact.address1;
				if (!name || !mobile || !province || !city || !district || !address) {
					popWarning('请输入完整信息', $timeout, $ionicLoading);
					return;
				}
				if (mobile.length != 11 || !DajiaGlobal.utils.mobileReg.test(mobile)) {
					popWarning('请输入正确的手机号码', $timeout, $ionicLoading);
					return;
				}
				$scope.order.userContact = $scope.userContact;
				$scope.order.cartItems = $scope.cartItems;

				var refUserId = DajiaGlobal.utils.getURLParameter('refUserId');
				var productId = DajiaGlobal.utils.getURLParameter('productId');
				var refOrderId = DajiaGlobal.utils.getURLParameter('refOrderId');
				if (null != refUserId && productId == $scope.order.productId) {
					console.log("refUserId:" + refUserId + " refOrderId:" + refOrderId);
					$scope.order.refUserId = refUserId;
					if (null != refOrderId) {
						$scope.order.refOrderId = refOrderId;
					}
				}

				console.log($scope.order);
				$http.post('/user/submitOrder', $scope.order).success(function(data, status, headers, config) {
					var charge = data;
					console.log(charge);
					if (null == charge || charge.length == 0) {
						popWarning('很抱歉，订单生成出错或商品已经售完，请刷新页面或重新进入应用再次尝试。', $timeout, $ionicLoading);
						$timeout(function() {
							$window.location.replace('#/tab/mine');
							$window.location.href = "#/tab/orders";
						}, 1000);
					} else {
						pingpp.createPayment(charge, function(result, error) {
							if (result == 'success') {
								// 只有微信公众账号 wx_pub 支付成功的结果会在这里返回，其他的 wap 支付结果都是在
								// extra
								// 中对应的URL 跳转。
								console.log('wechat pay success');
								popWarning('支付成功', $timeout, $ionicLoading);
								$timeout(function() {
									$window.location.replace('#/tab/prod');
									$window.location.href = "#/tab/prog";
								}, 1000);
							} else if (result == 'fail') {
								// charge 不正确或者微信公众账号支付失败时会在此处返回
								console.log('payment failed');
								popWarning('支付出错', $timeout, $ionicLoading);
								for (key in error) {
									alert(key + ': ' + error[key]);
								}
								$timeout(function() {
									$window.location.replace('#/tab/mine');
									$window.location.href = "#/tab/orders";
								}, 1000);
							} else if (result == 'cancel') {
								// 微信公众账号支付取消支付
								console.log('wechat pay cancelled');
								$timeout(function() {
									$window.location.replace('#/tab/mine');
									$window.location.href = "#/tab/orders";
								}, 1000);
							}
						});
					}
				}).error(function(data, status, headers, config) {
					console.log('request failed...');
				});
			}
			$scope.add = function() {
				if ($scope.order.quantity >= quota && quota != null) {
					popWarning('该产品每个账号限购' + quota + '件', $timeout, $ionicLoading);
					return;
				}
				if ($scope.order.quantity + 1 > $scope.orderItem.stock) {
					popWarning('该产品库存不足', $timeout, $ionicLoading);
					return;
				}
				$scope.order.quantity += 1;
				$scope.order.totalPrice = $scope.order.quantity * $scope.order.unitPrice + $scope.order.postFee;
			}
			$scope.remove = function() {
				if ($scope.order.quantity > 1) {
					$scope.order.quantity -= 1;
					$scope.order.totalPrice = $scope.order.quantity * $scope.order.unitPrice + $scope.order.postFee;
				}
			}
			$scope.add4Cart = function(cartId) {
				var cartItem = getCartItem(cartId);
				if (cartItem.quantity >= cartItem.buyQuota && cartItem.buyQuota != null) {
					popWarning('该产品每个账号限购' + cartItem.buyQuota + '件', $timeout, $ionicLoading);
					return;
				}
				if (cartItem.quantity + 1 > cartItem.stock) {
					popWarning('该产品库存不足', $timeout, $ionicLoading);
					return;
				}
				cartItem.quantity += 1;
				$scope.calcTotalPrice();
				$http.post('/user/cart/edit', cartItem).success(function(data, status, headers, config) {
					console.log('cartItem edit success...');
				}).error(function(data, status, headers, config) {
					popWarning('修改购物车信息出错', $timeout, $ionicLoading);
				});
			};
			$scope.remove4Cart = function(cartId) {
				var cartItem = getCartItem(cartId);
				if (cartItem.quantity > 1) {
					cartItem.quantity -= 1;
				}
				$scope.calcTotalPrice();
				$http.post('/user/cart/edit', cartItem).success(function(data, status, headers, config) {
					console.log('cartItem edit success...');
				}).error(function(data, status, headers, config) {
					popWarning('修改购物车信息出错', $timeout, $ionicLoading);
				});
			};
			$scope.del4Cart = function(cartId) {
				var cartItem = getCartItem(cartId);
				if ($scope.cartItems.length == 1) {
					popWarning('订单中只有一件商品', $timeout, $ionicLoading);
					return;
				}
				delCartItem(cartId);
				$scope.calcTotalPrice();
			};
			var getCartItem = function(cartId) {
				var cartItem;
				if (null != $scope.cartItems) {
					$scope.cartItems.forEach(function(c) {
						if (c.cartId == cartId) {
							cartItem = c;
							return;
						}
					});
				}
				return cartItem;
			}
			var delCartItem = function(cartId) {
				if (null != $scope.cartItems) {
					$scope.cartItems.forEach(function(c, idx) {
						if (c.cartId == cartId) {
							$scope.cartItems.splice(idx, 1);
							return;
						}
					});
				}
			}
			$scope.calcTotalPrice = function() {
				var postFee = $scope.order.postFee;
				var productDesc = null;
				if ($scope.defaultPostFee == 0) {
					return;
				}
				if ($scope.userContact.province.minPostFee > $scope.defaultPostFee) {
					$scope.order.postFee = $scope.userContact.province.minPostFee;
				} else {
					$scope.order.postFee = $scope.defaultPostFee;
				}
				if (null != $scope.cartItems) {
					var totalPrice = 0;
					$scope.cartItems.forEach(function(c) {
						totalPrice += c.currentPrice * c.quantity;
						if (null == productDesc) {
							productDesc = c.shortName;
						}
					});
					$scope.order.totalPrice = totalPrice + postFee;
					if ($scope.cartItems.length > 1) {
						productDesc = productDesc + '等' + $scope.cartItems.length + '件产品';
					}
					$scope.order.productDesc = productDesc;
				}
				$scope.order.totalPrice = $scope.order.totalPrice - postFee + $scope.order.postFee;
			}

			$scope.selectAlipay = function() {
				popWarning('由于微信技术屏蔽，选择支付宝购买可能需要打开独立浏览器。', $timeout, $ionicLoading);
			}
			$scope.changeUserContact = function(uc) {
				if (null != $scope.loginuser.userContacts && null != $scope.selectedUserContact.contactId) {
					$scope.loginuser.userContacts.forEach(function(c) {
						if (c.contactId == $scope.selectedUserContact.contactId) {
							$scope.userContact = c;
						}
					});
				}
				fillLocationDropdowns($scope, $ionicLoading);
			}
			var fillLocationDropdowns = function($scope, $ionicLoading) {
				$scope.provinces.forEach(function(p) {
					if (p.locationKey == $scope.userContact.province.locationKey) {
						$scope.userContact.province = p;
						p.children.forEach(function(c) {
							if (c.locationKey == $scope.userContact.city.locationKey) {
								$scope.userContact.city = c;
								c.children.forEach(function(d) {
									if (d.locationKey == $scope.userContact.district.locationKey) {
										$scope.userContact.district = d;
										$scope.calcTotalPrice();
										if (productReady) {
											$ionicLoading.hide();
										}
										locationReady = true;
										return;
									}
								});
								return;
							}
						});
						return;
					}
				});
			}
		})

.controller(
		'ProgCtrl',
		function($scope, $rootScope, $window, $http, $cookies, $ionicModal, $timeout, $ionicLoading) {
			console.log('进度列表...');
			$scope.loginUser = $cookies.get('dajia_user_id');
			modalInit($rootScope, $ionicModal, 'login');
			$scope.login = function() {
				if ($scope.loginUser == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					$window.location.reload();
				}
			}
			$scope.page = {
				hasMore : false,
				pageNo : 1
			};
			var loadProgress = function() {
				return $http.get('/user/progresses/' + $scope.page.pageNo).success(
						function(data, status, headers, config) {
							$scope.page.hasMore = data.hasNext;
							$scope.page.pageNo = data.currentPage;
							if (null == $scope.myOrders) {
								$scope.myOrders = [];
							}
							$scope.myOrders = $scope.myOrders.concat(data.results);
							console.log($scope.myOrders);
							$scope.myOrders.forEach(function(o) {
								if (null == o.progressValue) {
									o.progressValue = o.productVO.priceOff
											/ (o.productVO.originalPrice - o.productVO.targetPrice) * 100;
								}
							});
						});
			}
			if ($scope.loginUser != null) {
				popLoading($ionicLoading);
				loadProgress().then(function() {
					$ionicLoading.hide();
				});
			}
			$scope.doRefresh = function() {
				$scope.myOrders = null;
				$scope.page.pageNo = 1;
				loadProgress().then(function() {
					$scope.$broadcast('scroll.refreshComplete');
				});
			}
			$scope.loadMore = function() {
				console.log('load more...');
				$scope.page.pageNo += 1;
				loadProgress().then(function() {
					$scope.$broadcast('scroll.infiniteScrollComplete');
				});
			}
			$scope.goHome = function() {
				$window.location.href = '#/tab/prod';
			}
			$scope.progressDetail = function(trackingId, orderItemId, isPromoted) {
				if (null == orderItemId) {
					orderItemId = 0;
				}
				if (isPromoted == 'Y') {
					$window.location.href = '#/tab/prog4s/' + trackingId + '/' + orderItemId;
				} else {
					$window.location.href = '#/tab/prog/' + trackingId + '/' + orderItemId;
				}
			}
		})

.controller(
		'ProgDetailCtrl',
		function($scope, $rootScope, $cookies, $stateParams, $http, $window, $ionicModal, $timeout, $ionicLoading) {
			console.log('进度详情...')
			$scope.order = {};
			popLoading($ionicLoading);
			$http.get('/user/progress/' + $stateParams.trackingId + '/' + $stateParams.orderItemId).success(
					function(data, status, headers, config) {
						console.log(data);
						var order = data;
						order.progressValue = order.productVO.priceOff
								/ (order.productVO.originalPrice - order.productVO.targetPrice) * 100;
						$scope.order = order;
						// console.log(order);
						if (order.productVO.productStatus == 2 && order.productVO.stock) {
							initWechatJSAPI('progress', $http, $cookies, $timeout, $ionicLoading,
									$scope.order.productVO, $scope.order, $scope, $rootScope, 'N');
						}
						$ionicLoading.hide();
					});
			$scope.order.progressValue = 0;
			$scope.share = function() {
				popWarning('请点击右上角微信菜单-发送给朋友。有机会获取额外奖励折扣。', $timeout, $ionicLoading);
				shareProduct($scope, $rootScope, $http, $cookies, $timeout, $ionicLoading, $scope.order.productVO,
						$scope.order);
				// shareSuccess($scope, $http, $scope.order.orderId,
				// $scope.order.productVO.productId);
			}
			$scope.orderDetail = function(trackingId) {
				$window.location.href = '#/tab/order/' + trackingId;
			}
			$scope.back = function() {
				$window.location.replace('#/tab/prog');
			}
		})

.controller(
		'ProgDetailShareCtrl',
		function($scope, $rootScope, $cookies, $stateParams, $http, $window, $ionicModal, $timeout, $ionicLoading) {
			console.log('打群价进度详情...')
			$scope.order = {};
			popLoading($ionicLoading);
			$http.get('/user/progress/' + $stateParams.trackingId + '/' + $stateParams.orderItemId).success(
					function(data, status, headers, config) {
						console.log(data);
						var order = data;
						order.progressValue = order.productVO.priceOff
								/ (order.productVO.originalPrice - order.productVO.targetPrice) * 100;
						if (null != order.userShares) {
							var shareRefund = order.userShares.length;
							if (shareRefund > order.productVO.currentPrice) {
								shareRefund = order.productVO.currentPrice;
							}
							order.progressValue = shareRefund / order.productVO.currentPrice * 100;
							order.productVO.currentPrice = order.productVO.currentPrice - shareRefund;
						}
						$scope.order = order;
						// console.log(order);
						if (order.productVO.productStatus == 2 && order.productVO.stock) {
							initWechatJSAPI('progress', $http, $cookies, $timeout, $ionicLoading,
									$scope.order.productVO, $scope.order, $scope, $rootScope, 'Y');
						}
						$ionicLoading.hide();
					});
			$scope.order.progressValue = 0;
			$scope.share = function() {
				popWarning('请点击右上角微信菜单-分享到朋友圈或微信群，获取免单机会。', $timeout, $ionicLoading);
				shareProduct($scope, $rootScope, $http, $cookies, $timeout, $ionicLoading, $scope.order.productVO,
						$scope.order, 'Y');
			}
			$scope.orderDetail = function(trackingId) {
				$window.location.href = '#/tab/order/' + trackingId;
			}
			$scope.back = function() {
				$window.location.replace('#/tab/prog');
			}
		})

.controller('MineCtrl',
		function($scope, $rootScope, $http, $window, $cookies, $timeout, $ionicLoading, $ionicModal, AuthService) {
			console.log('我的打价...');
			modalInit($rootScope, $ionicModal, 'login');
			$scope.userName = $cookies.get('dajia_username');
			var loginUser = $cookies.get('dajia_user_id');
			if (loginUser != null) {
				$http.get('/user/loginuserinfo').success(function(data, status, headers, config) {
					$scope.headImgUrl = data.headImgUrl;
				});
			}
			$scope.myOrders = function() {
				if (loginUser == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					$window.location.href = '#/tab/orders';
				}
			}
			$scope.myFav = function() {
				if (loginUser == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					$window.location.href = '#/tab/mine/fav';
				}
			}
			$scope.myCart = function() {
				if (loginUser == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					$window.location.href = '#/tab/cart';
				}
			}
			$scope.contacts = function() {
				if (loginUser == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					$window.location.href = '#/tab/mine/contacts';
				}
			}
			$scope.bindMobile = function() {
				if (loginUser == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					$window.location.href = '#/tab/mine/bindmobile';
				}
			}
			$scope.myPass = function() {
				if (loginUser == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					$window.location.href = '#/tab/mine/password';
				}
			}
			$scope.showQcode = function() {
				$window.location.href = '#/tab/qcode';
			}
			$scope.logout = function() {
				if (loginUser == null) {
					$window.location.reload();
				} else {
					AuthService.logout(loginUser);
				}
			};
			$scope.$on('event:auth-logout-complete', function() {
				popWarning('退出登录成功', $timeout, $ionicLoading);
				$timeout(function() {
					$window.location.reload();
				}, 500);
				// $scope.openModal('login');
			});
		})

.controller('MyOrdersCtrl', function($scope, $http, $window, $stateParams, $timeout, $ionicLoading) {
	console.log('我的订单...');
	$scope.page = {
		hasMore : false,
		pageNo : 1
	};
	var loadOrders = function() {
		return $http.get('/user/myorders/' + $scope.page.pageNo).success(function(data, status, headers, config) {
			$scope.page.hasMore = data.hasNext;
			$scope.page.pageNo = data.currentPage;
			if (null == $scope.myOrders) {
				$scope.myOrders = [];
			}
			$scope.myOrders = $scope.myOrders.concat(data.results);
			console.log($scope.myOrders);
		});
	}
	popLoading($ionicLoading);
	loadOrders().then(function() {
		$ionicLoading.hide();
	});
	$scope.doRefresh = function() {
		$scope.myOrders = null;
		$scope.page.pageNo = 1;
		loadOrders().then(function() {
			$scope.$broadcast('scroll.refreshComplete');
		});
	};
	$scope.loadMore = function() {
		console.log('load more...');
		$scope.page.pageNo += 1;
		loadOrders().then(function() {
			$scope.$broadcast('scroll.infiniteScrollComplete');
		});
	}
	$scope.orderDetail = function(trackingId) {
		$window.location.href = '#/tab/order/' + trackingId;
	}
	$scope.delOrder = function(trackingId) {
		$http.get('/user/order/del/' + trackingId).success(function(data, status, headers, config) {
			popWarning('订单删除成功', $timeout, $ionicLoading);
			$timeout(function() {
				$window.location.reload();
			}, 500);
		});
	}
	$scope.goHome = function() {
		$window.location.href = '#/tab/prod';
	}
})

.controller(
		'MyOrderDetailCtrl',
		function($scope, $http, $stateParams, $window, $timeout, $ionicLoading) {
			console.log('我的订单详情...');
			var loadOrderDetail = function() {
				popLoading($ionicLoading);
				return $http.get('/user/order/' + $stateParams.trackingId).success(
						function(data, status, headers, config) {
							console.log(data);
							$scope.order = data;
							$scope.checkLogisticUrl = "http://m.kuaidi100.com/index_all.html?type="
									+ data.logisticAgent + "&postid=" + data.logisticTrackingId + "&callbackurl="
									+ $window.location.protocol + "//" + $window.location.host
									+ window.location.pathname;
							$ionicLoading.hide();
						});
			}
			loadOrderDetail();

			$scope.submit = function() {
				console.log($scope.order);
				$http.post('/user/getCharge', $scope.order).success(function(data, status, headers, config) {
					var charge = data;
					console.log(charge);
					if (null == charge || charge.length == 0) {
						popWarning('订单已经过期或商品已经售完', $timeout, $ionicLoading);
					} else {
						pingpp.createPayment(charge, function(result, error) {
							if (result == 'success') {
								// 只有微信公众账号 wx_pub 支付成功的结果会在这里返回，其他的 wap 支付结果都是在
								// extra
								// 中对应的URL 跳转。
								console.log('wechat pay success');
								popWarning('支付成功', $timeout, $ionicLoading);
								$timeout(function() {
									$window.location.replace('#/tab/prod');
									$window.location.href = "#/tab/prog";
								}, 1000);
							} else if (result == 'fail') {
								// charge 不正确或者微信公众账号支付失败时会在此处返回
								console.log('payment failed');
								popWarning('支付出错', $timeout, $ionicLoading);
								for (key in error) {
									alert(key + ': ' + error[key]);
								}
							} else if (result == 'cancel') {
								// 微信公众账号支付取消支付
								console.log('wechat pay cancelled');
							}
						});
					}
				}).error(function(data, status, headers, config) {
					console.log('request failed...');
				});
			}

			$scope.selectAlipay = function() {
				popWarning('由于微信技术屏蔽，选择支付宝购买可能需要打开独立浏览器。', $timeout, $ionicLoading);
			}
		})

.controller('MyFavCtrl', function($scope, $http, $ionicLoading, $window) {
	console.log('我的收藏...');
	var loadFavs = function() {
		popLoading($ionicLoading);
		return $http.get('/user/favourites').success(function(data, status, headers, config) {
			$scope.products = data;
			$scope.$broadcast('scroll.refreshComplete');
			$ionicLoading.hide();
		});
	}
	loadFavs();
	$scope.doRefresh = function() {
		loadFavs();
	};
	$scope.goHome = function() {
		$window.location.href = '#/tab/prod';
	}
})

.controller('MyCartCtrl', function($scope, $http, $timeout, $ionicLoading, $window) {
	console.log('购物车...');
	$scope.cart = {
		totalPrice : 0
	};
	var loadMyCart = function() {
		popLoading($ionicLoading);
		return $http.get('/user/cart').success(function(data, status, headers, config) {
			// console.log(data);
			$scope.cartItems = data;
			calcTotalPrice();
			$scope.$broadcast('scroll.refreshComplete');
			$ionicLoading.hide();
		});
	}
	loadMyCart();
	$scope.doRefresh = function() {
		loadMyCart();
	};
	$scope.goHome = function() {
		$window.location.href = '#/tab/prod';
	}
	$scope.add = function(cartId) {
		var cartItem = getCartItem(cartId);
		if (cartItem.quantity >= cartItem.buyQuota && cartItem.buyQuota != null) {
			popWarning('该产品每个账号限购' + cartItem.buyQuota + '件', $timeout, $ionicLoading);
			return;
		}
		if (cartItem.quantity + 1 > cartItem.stock) {
			popWarning('该产品库存不足', $timeout, $ionicLoading);
			return;
		}
		cartItem.quantity += 1;
		calcTotalPrice();
		$http.post('/user/cart/edit', cartItem).success(function(data, status, headers, config) {
			console.log('cartItem edit success...');
		}).error(function(data, status, headers, config) {
			popWarning('修改购物车信息出错', $timeout, $ionicLoading);
		});
	};
	$scope.remove = function(cartId) {
		var cartItem = getCartItem(cartId);
		if (cartItem.quantity > 1) {
			cartItem.quantity -= 1;
		}
		calcTotalPrice();
		$http.post('/user/cart/edit', cartItem).success(function(data, status, headers, config) {
			console.log('cartItem edit success...');
		}).error(function(data, status, headers, config) {
			popWarning('修改购物车信息出错', $timeout, $ionicLoading);
		});
	};
	$scope.del = function(cartId) {
		var cartItem = getCartItem(cartId);
		$http.get('/user/cart/remove/' + cartItem.productId).success(function(data, status, headers, config) {
			delCartItem(cartId);
			calcTotalPrice();
		});
	};
	$scope.submit = function() {
		// var submitItems = [];
		// $scope.cartItems.forEach(function(c) {
		// if (c.productStatus == 2 && c.stock > 0) {
		// submitItems.push(c);
		// }
		// });
		// console.log(submitItems);
		if ($scope.cart.totalPrice > 0) {
			$window.location.href = '#/tab/cartorder';
		} else {
			popWarning('购物车中没有需要结算的商品', $timeout, $ionicLoading);
		}
	}

	var getCartItem = function(cartId) {
		var cartItem;
		if (null != $scope.cartItems) {
			$scope.cartItems.forEach(function(c) {
				if (c.cartId == cartId) {
					cartItem = c;
					return;
				}
			});
		}
		return cartItem;
	}
	var delCartItem = function(cartId) {
		if (null != $scope.cartItems) {
			$scope.cartItems.forEach(function(c, idx) {
				if (c.cartId == cartId) {
					$scope.cartItems.splice(idx, 1);
					return;
				}
			});
		}
	}
	var calcTotalPrice = function() {
		$scope.cart.totalPrice = 0;
		$scope.cartItems.forEach(function(c) {
			if (c.productStatus == 2 && c.stock > 0) {
				$scope.cart.totalPrice += c.currentPrice * c.quantity;
			}
		});
	}
})

.controller('MyPassCtrl', function($scope, $http, $timeout, $ionicLoading) {
	console.log('修改密码...');
	$scope.form = {};
	$scope.submit = function() {
		var oldPassword = $scope.form.oldPassword;
		var newPassword = $scope.form.newPassword;
		var newPasswordConfirm = $scope.form.newPasswordConfirm;
		if (!oldPassword || !newPassword || !newPasswordConfirm) {
			popWarning('请输入完整信息', $timeout, $ionicLoading);
			return;
		}
		if (newPassword.length < 6) {
			popWarning('请输入至少六位数的密码', $timeout, $ionicLoading);
			return;
		}
		if (newPassword != newPasswordConfirm) {
			popWarning('两次输入的新密码不一致', $timeout, $ionicLoading);
			return;
		}
		if (newPassword == oldPassword) {
			popWarning('新密码不能与老密码相同', $timeout, $ionicLoading);
			return;
		}
		$http.post('/user/changePassword', $scope.form).success(function(data, status, headers, config) {
			var msg = data.msg;
			popWarning(msg, $timeout, $ionicLoading);
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
		});

	};
})

.controller('ListContactCtrl', function($scope, $http, $timeout, $ionicLoading) {
	console.log('收货地址列表...');
	var loadContacts = function() {
		popLoading($ionicLoading);
		return $http.get('/user/contacts').success(function(data, status, headers, config) {
			$scope.contacts = data;
			$ionicLoading.hide();
		});
	}
	loadContacts();
})

.controller(
		'EditContactCtrl',
		function($scope, $http, $stateParams, $window, $timeout, $ionicLoading) {
			console.log('收货地址管理...');
			popLoading($ionicLoading);
			$http.get('/user/contact/' + $stateParams.contactId).success(function(data, status, headers, config) {
				console.log(data);
				$scope.userContact = data;
				var isNewContact = false;
				if (data == null || data.length == 0) {
					isNewContact = true;
					$scope.userContact = {};
				}
				$http.get('/locations').success(function(data, status, headers, config) {
					$scope.provinces = data;
					if (!isNewContact) {
						$scope.provinces.forEach(function(p) {
							if (p.locationKey == $scope.userContact.province.locationKey) {
								$scope.userContact.province = p;
								p.children.forEach(function(c) {
									if (c.locationKey == $scope.userContact.city.locationKey) {
										$scope.userContact.city = c;
										c.children.forEach(function(d) {
											if (d.locationKey == $scope.userContact.district.locationKey) {
												$scope.userContact.district = d;
												$ionicLoading.hide();
												return;
											}
										});
										return;
									}
								});
								return;
							}
						});
					} else {
						$ionicLoading.hide();
					}
				}).error(function(data, status, headers, config) {
					console.log('request failed...');
				});
			});

			$scope.remove = function() {
				if ($scope.userContact.contactId != null) {
					$http.get('/user/contact/remove/' + $stateParams.contactId).success(
							function(data, status, headers, config) {
								popWarning('收货信息删除成功', $timeout, $ionicLoading);
								$window.location.href = '#/tab/mine/contacts';
								$window.location.reload();
							});
				} else {
					$window.location.href = '#/tab/mine/contacts';
				}
			}

			$scope.markDefault = function() {
				$http.get('/user/contact/default/' + $stateParams.contactId).success(
						function(data, status, headers, config) {
							popWarning('成功设置为默认收货信息', $timeout, $ionicLoading);
							$window.location.href = '#/tab/mine/contacts';
							$window.location.reload();
						});
			}

			$scope.submit = function() {
				if ($scope.userContact.contactId == null) {
					console.log('new userContact.');
				}
				var name = $scope.userContact.contactName;
				var mobile = $scope.userContact.contactMobile;
				var province = $scope.userContact.province;
				var city = $scope.userContact.city;
				var district = $scope.userContact.district;
				var address = $scope.userContact.address1;

				if (!name || !mobile || !province || !city || !district || !address) {
					popWarning('请输入完整信息', $timeout, $ionicLoading);
					return;
				}

				if (mobile.length != 11 || !DajiaGlobal.utils.mobileReg.test(mobile)) {
					popWarning('请输入正确的手机号码', $timeout, $ionicLoading);
					return;
				}
				$http.post('/user/contact/' + $stateParams.contactId, $scope.userContact).success(
						function(data, status, headers, config) {
							popWarning('收货信息修改成功', $timeout, $ionicLoading);
							$window.location.href = '#/tab/mine/contacts';
							$window.location.reload();
						});
			}
		})

.controller('BindMobileCtrl', function($scope, $http, $q, $cookies, $timeout, $ionicLoading) {
	console.log('绑定手机...');
	var userId = $cookies.get('dajia_user_id');
	$scope.userMobile = $cookies.get('dajia_user_mobile');
	if (!DajiaGlobal.utils.isValidStr($scope.userMobile)) {
		$scope.userMobile = "未绑定";
	}
	$scope.user = {
		'userId' : userId,
		'mobile' : null,
		'bindingCode' : null
	};
	$scope.smsBtnTxt = '发送手机验证码';
	$scope.smsBtnDisable = false;
	var smsBtn = angular.element(document.querySelector('#smsBtn'));
	var checkMobile = function(mobile) {
		var defer = $q.defer();
		$http.get('/signupCheck/' + mobile).success(function(data, status, headers, config) {
			if ("success" == data.result) {
				defer.resolve(true);
			} else {
				popWarning('该手机号已被其他账号绑定', $timeout, $ionicLoading);
				defer.resolve(false);
			}
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
			defer.reject();
		});
		return defer.promise;
	}

	$scope.getBindingCode = function() {
		var mobile = $scope.user.mobile;

		if (!mobile || mobile.length != 11 || !DajiaGlobal.utils.mobileReg.test(mobile)) {
			popWarning('请输入正确的手机号码', $timeout, $ionicLoading);
			return;
		}
		checkMobile(mobile).then(function(mobileValid) {
			if (mobileValid) {
				sendSmsMessage($scope, $http, $timeout, $ionicLoading, '/bindingSms/', mobile);
			}
		});
	}

	$scope.submit = function() {
		if (!$scope.user.mobile || !$scope.user.bindingCode) {
			popWarning('请输入完整信息', $timeout, $ionicLoading);
			return;
		}
		$http.post('/bindMobile', $scope.user).success(function(data, status, headers, config) {
			if (null != data && data.result == 'success') {
				popWarning('已成功绑定新的手机号码', $timeout, $ionicLoading);
				$cookies.put('dajia_user_mobile', $scope.user.mobile, {
					path : '/'
				});
				$scope.userMobile = $scope.user.mobile;
				$scope.user.mobile = null;
				$scope.user.bindingCode = null;
			} else {
				popWarning('绑定失败', $timeout, $ionicLoading);
			}
		});
	};
})

.controller('SignInCtrl',
		function($scope, $rootScope, $window, $http, $q, $ionicLoading, $timeout, $ionicModal, AuthService) {
			$scope.login = {
				'mobile' : null,
				'signinCode' : null
			};

			$scope.smsBtnTxt = '发送手机验证码';
			$scope.smsBtnDisable = false;
			var smsBtn = angular.element(document.querySelector('#smsBtn'));

			var checkMobile = function(mobile) {
				var defer = $q.defer();
				$http.get('/signupCheck/' + mobile).success(function(data, status, headers, config) {
					if ("failed" == data.result) {
						defer.resolve(true);
					} else {
						popWarning('该手机号未被绑定，请先用微信登录后再绑定手机', $timeout, $ionicLoading);
						defer.resolve(false);
					}
				}).error(function(data, status, headers, config) {
					console.log('request failed...');
					defer.reject();
				});
				return defer.promise;
			}

			$scope.getSigninCode = function() {
				var mobile = $scope.login.mobile;

				if (!mobile || mobile.length != 11 || !DajiaGlobal.utils.mobileReg.test(mobile)) {
					popWarning('请输入正确的手机号码', $timeout, $ionicLoading);
					return;
				}
				checkMobile(mobile).then(function(mobileValid) {
					if (mobileValid) {
						sendSmsMessage($scope, $http, $timeout, $ionicLoading, '/signinSms/', mobile);
					}
				});
			}

			$scope.submit = function() {
				if (!$scope.login.mobile || !$scope.login.signinCode) {
					popWarning('请输入完整信息', $timeout, $ionicLoading);
					return;
				}
				AuthService.login($scope.login);
			};

			$scope.$on('event:auth-loginRequired', function(e, rejection) {
				$scope.openModal('login');
			});

			$scope.$on('event:auth-loginConfirmed', function() {
				$scope.closeModal('login');
				popWarning('登陆成功', $timeout, $ionicLoading);
				$timeout(function() {
					$window.location.reload();
				}, 500);
			});

			$scope.$on('event:auth-login-failed', function(e, status) {
				var error = '登录失败';
				if (status == 401) {
					error = '验证码错误';
				}
				popWarning(error, $timeout, $ionicLoading);
			});

			$scope.wechatLogin = function() {
				var productId = DajiaGlobal.utils.getURLParameter('productId');
				if (null != productId) {
					$window.location.href = '/wechat/login?productId=' + productId;
				} else {
					$window.location.href = '/wechat/login';
				}
			}

			// deprecated
			modalInit($rootScope, $ionicModal, 'signup');
			$scope.signup = function() {
				$scope.openModal('signup');
			}
		})

.controller('SignUpCtrl', function($scope, $http, $q, $ionicLoading, $timeout, AuthService) {
	$scope.signup = {
		'mobile' : null,
		'password' : null,
		'signupCode' : null
	};
	$scope.smsBtnTxt = '发送手机验证码';
	$scope.smsBtnDisable = false;
	var smsBtn = angular.element(document.querySelector('#smsBtn'));

	var checkMobile = function(mobile) {
		var defer = $q.defer();
		$http.get('/signupCheck/' + mobile).success(function(data, status, headers, config) {
			if ("success" == data.result) {
				defer.resolve(true);
			} else {
				popWarning('该手机号已被注册', $timeout, $ionicLoading);
				defer.resolve(false);
			}
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
			defer.reject();
		});
		return defer.promise;
	}

	$scope.getSignupCode = function() {
		var mobile = $scope.signup.mobile;

		if (!mobile || mobile.length != 11 || !DajiaGlobal.utils.mobileReg.test(mobile)) {
			popWarning('请输入正确的手机号码', $timeout, $ionicLoading);
			return;
		}
		checkMobile(mobile).then(function(mobileValid) {
			if (mobileValid) {
				sendSmsMessage($scope, $http, $timeout, $ionicLoading, '/signupSms/', mobile);
			}
		});
	}

	$scope.submit = function() {
		var mobile = $scope.signup.mobile;
		var password = $scope.signup.password;
		var signupCode = $scope.signup.signupCode;
		if (!mobile || !password || !signupCode) {
			popWarning('请输入完整信息', $timeout, $ionicLoading);
			return;
		}

		if (mobile.length != 11 || !DajiaGlobal.utils.mobileReg.test(mobile)) {
			popWarning('请输入正确的手机号码', $timeout, $ionicLoading);
			return;
		}
		if (password.length < 6) {
			popWarning('请输入至少六位数的密码', $timeout, $ionicLoading);
			return;
		}
		checkMobile(mobile).then(function(mobileValid) {
			if (mobileValid) {
				AuthService.signup($scope.signup);
			}
		});
	};

	$scope.$on('event:auth-signup-failed', function(e, status) {
		var error = '注册失败，验证码错误';
		popWarning(error, $timeout, $ionicLoading);
	});

	$scope.$on('event:auth-signup-success', function() {
		$scope.closeModal('signup');
		popWarning('注册成功', $timeout, $ionicLoading);
	});
})

.controller('QcodeCtrl', function($scope, $window) {
	console.log('QcodeCtrl');
	$scope.back = function() {
		$window.location.replace('#/tab/mine');
	}
})

.controller('SignOutCtrl', function($scope, AuthService) {
	AuthService.logout();
})

.controller('ErrorCtrl', function($scope, $window) {
	console.log('ErrorCtrl');
	$scope.goHome = function() {
		$window.location.replace('#');
	}
});

var modalInit = function($rootScope, $ionicModal, modalType) {
	// console.log($ionicModal);
	$ionicModal.fromTemplateUrl('templates/' + modalType + '.html', {
		scope : $rootScope,
		animation : 'slide-in-up'
	}).then(function(modal) {
		$rootScope['modal_' + modalType] = modal;
	});
	$rootScope.openModal = function(type) {
		$rootScope['modal_' + type].show();
	};
	$rootScope.closeModal = function(type) {
		$rootScope['modal_' + type].hide();
	};
	$rootScope.$on('$destroy', function() {
		$rootScope['modal_' + modalType].remove();
	});
}

var userAgreeModalInit = function($scope, $ionicModal) {
	$ionicModal.fromTemplateUrl('templates/user-agreement.html', {
		scope : $scope,
		animation : 'slide-in-up'
	}).then(function(modal) {
		$scope.userAgreeModal = modal;
	});
	$scope.openUserAgreeModal = function() {
		$scope.userAgreeModal.show();
	};
	$scope.closeUserAgreeModal = function() {
		$scope.userAgreeModal.hide();
	};
	$scope.$on('$destroy', function() {
		$scope.userAgreeModal.remove();
	});
}

var shareModalInit = function($scope, $ionicModal) {
	$ionicModal.fromTemplateUrl('templates/share-popup.html', {
		scope : $scope,
		animation : 'slide-in-up'
	}).then(function(modal) {
		$scope.shareModal = modal;
	});
	$scope.openShareModal = function(data) {
		console.log(data)
		$scope.shareInfo = data;
		$scope.shareModal.show();
	};
	$scope.closeShareModal = function() {
		$scope.shareModal.hide();
	};
	$scope.$on('$destroy', function() {
		$scope.shareModal.remove();
	});
}

var popWarning = function(msg, $timeout, $ionicLoading) {
	$ionicLoading.show({
		template : msg
	});
	$timeout(function() {
		$ionicLoading.hide();
	}, 2000);
}

var popLoading = function($ionicLoading) {
	$ionicLoading.show({
		template : '加载中...'
	});
}

var sendSmsMessage = function($scope, $http, $timeout, $ionicLoading, methodPath, mobile) {
	var counter = 60;
	var onTimeout = function() {
		counter--;
		if (counter == 0) {
			$scope.smsBtnTxt = '发送手机验证码';
			$scope.smsBtnDisable = false;
			return false;
		}
		$scope.smsBtnTxt = '发送手机验证码 (' + counter + ')';
		mytimeout = $timeout(onTimeout, 1000);
	}
	var mytimeout = $timeout(onTimeout, 1000);
	$scope.smsBtnDisable = true;

	$http.get(methodPath + mobile).success(function(data, status, headers, config) {
		if ("success" == data.result) {
			popWarning('验证码已发送', $timeout, $ionicLoading);
		} else {
			popWarning('验证码发送失败', $timeout, $ionicLoading);
		}
	}).error(function(data, status, headers, config) {
		console.log('request failed...');
	});
}

var initWechatJSAPI = function(screen, $http, $cookies, $timeout, $ionicLoading, product, order, $scope, $rootScope,
		isPromoted) {
	$http.get('/wechat/signature').success(function(data, status, headers, config) {
		// console.log(data);
		wx.config({
			appId : data['appId'],
			timestamp : data['timestamp'],
			nonceStr : data['nonceStr'],
			signature : data['signature'],
			jsApiList : [ 'checkJsApi', 'onMenuShareAppMessage', 'onMenuShareTimeline', 'onMenuShareQQ' ]
		});
		wx.checkJsApi({
			jsApiList : [ 'onMenuShareAppMessage', 'onMenuShareTimeline', 'onMenuShareQQ' ],
			success : function(res) {
				console.log(res);
			}
		});
		wx.ready(function() {
			if (screen == 'product') {
				simpleShare(product, $cookies, $timeout, $ionicLoading);
			} else if (screen == 'progress') {
				shareProduct($scope, $rootScope, $http, $cookies, $timeout, $ionicLoading, product, order, isPromoted);
			} else {
				shareHome();
			}
		});
	});
}

var checkOauthLogin = function($cookies, $http, AuthService) {
	if (!$cookies.get('dajia_user_id')) {
		$http.get('/user/loginuserinfo').success(function(data, status, headers, config) {
			var loginuser = data;
			if (null != loginuser['userId']) {
				AuthService.oauthLogin(loginuser);
			}
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
		});
	}
}

var shareHome = function() {
	console.log('shareHome');
	var shareLink = 'http://51daja.com/app';
	var logoImg = 'http://51daja.com/app/img/logo.png';
	wx.onMenuShareAppMessage({
		title : '打价网',
		desc : '一个可以还价的网站！~每晚8:00准时上新~',
		link : shareLink,
		imgUrl : logoImg,
		success : function() {
			console.log('success');
		},
		cancel : function() {
			console.log('cancel');
		}
	});
	wx.onMenuShareTimeline({
		title : '打价网:一个可以还价的网站！~每晚8:00准时上新~',
		link : shareLink,
		imgUrl : logoImg,
		success : function() {
			console.log('success');
		},
		cancel : function() {
			console.log('cancel');
		}
	});
	wx.onMenuShareQQ({
		title : '打价网',
		desc : '一个可以还价的网站！~每晚8:00准时上新~',
		link : shareLink,
		imgUrl : logoImg,
		success : function() {
			console.log('success');
		},
		cancel : function() {
			console.log('cancel');
		}
	});
}

var simpleShare = function(product, $cookies, $timeout) {
	console.log('simpleShare');
	// console.log(product);
	var userId = $cookies.get('dajia_user_id');
	var successMsg = '分享成功！';
	var shareLink = '#';
	if (null != userId) {
		shareLink = 'http://51daja.com/app/index.html?refUserId=' + userId + '&productId=' + product.productId
				+ '#/tab/prod/' + product.productId;
	} else {
		shareLink = 'http://51daja.com/app/index.html?productId=' + product.productId + '#/tab/prod/'
				+ product.productId;
	}
	wx.onMenuShareAppMessage({
		title : '一起来打价，越打越便宜！自己打出全网最低价！',
		desc : '「' + product.shortName + '」再打一次便宜' + product.nextOff + '元~ 红红火火恍恍惚惚~',
		link : shareLink,
		imgUrl : product.imgUrl4List,
		trigger : function() {
			console.log('click');
		},
		success : function() {
			popWarning(successMsg, $timeout, $ionicLoading);
		},
		cancel : function() {
			console.log('cancel');
		}
	});
	wx.onMenuShareTimeline({
		title : '一起来打价，越打越便宜！自己打出全网最低价！「' + product.shortName + '」再打一次便宜' + product.nextOff + '元~ 红红火火恍恍惚惚~',
		link : shareLink,
		imgUrl : product.imgUrl4List,
		success : function() {
			popWarning(successMsg, $timeout, $ionicLoading);
		},
		cancel : function() {
			console.log('cancel');
		}
	});
	wx.onMenuShareQQ({
		title : '一起来打价，越打越便宜！自己打出全网最低价！',
		desc : '「' + product.shortName + '」再打一次便宜' + product.nextOff + '元~ 红红火火恍恍惚惚~',
		link : shareLink,
		imgUrl : product.imgUrl4List,
		success : function() {
			popWarning(successMsg, $timeout, $ionicLoading);
		},
		cancel : function() {
			console.log('cancel');
		}
	});
	console.log(wx);
}

var shareProduct = function($scope, $rootScope, $http, $cookies, $timeout, $ionicLoading, product, order, isPromoted) {
	console.log('shareProduct');
	var userId = $cookies.get('dajia_user_id');
	var username = $cookies.get('dajia_username');
	if (userId == null) {
		$rootScope.$broadcast('event:auth-loginRequired');
	} else {
		var successMsg = '分享成功，底价已显示！朋友购买后将获得额外奖励折扣！';
		var shareTitle = '快来跟' + username + '一起打出全网最低价！';
		var shareTitle4Timeline = '快来跟' + username + '一起打出全网最低价！「' + product.shortName + '」再打一次便宜' + product.nextOff
				+ '元~ 红红火火恍恍惚惚~';
		var shareBody = '「' + product.shortName + '」再打一次便宜' + product.nextOff + '元~ 红红火火恍恍惚惚~';
		if (isPromoted == 'Y') {
			successMsg = '分享成功，每个好友点击将获1元额外优惠！';
			shareTitle = username + '正在苦战中！我来补一刀，有机会免单!';
			shareTitle4Timeline = username + '正在苦战中！我来补一刀，有机会免单!「' + product.shortName + '」补一刀便宜1元限时活动中~ 还等什么？';
			shareBody = '「' + product.shortName + '」补一刀便宜1元限时活动中~ 还等什么？';
		}
		var shareLink = "";
		if (null != order && null != product) {
			shareLink = 'http://51daja.com/app/index.html?refUserId=' + userId + '&productId=' + product.productId
					+ '&refOrderId=' + order.orderId + '#/tab/prod/' + product.productId;
		} else {
			shareLink = 'http://51daja.com/app/index.html#/tab/prod/' + product.productId;
		}
		wx.onMenuShareAppMessage({
			title : shareTitle,
			desc : shareBody,
			link : shareLink,
			imgUrl : product.imgUrl4List,
			trigger : function() {
				console.log('click');
			},
			success : function() {
				popWarning(successMsg, $timeout, $ionicLoading);
				shareSuccess($scope, $http, order.orderId, product.productId);
			},
			cancel : function() {
				console.log('cancel');
			}
		});
		wx.onMenuShareTimeline({
			title : shareTitle4Timeline,
			link : shareLink,
			imgUrl : product.imgUrl4List,
			success : function() {
				popWarning(successMsg, $timeout, $ionicLoading);
				shareSuccess($scope, $http, order.orderId, product.productId);
			},
			cancel : function() {
				console.log('cancel');
			}
		});
		wx.onMenuShareQQ({
			title : shareTitle,
			desc : shareBody,
			link : shareLink,
			imgUrl : product.imgUrl4List,
			success : function() {
				popWarning(successMsg, $timeout, $ionicLoading);
				shareSuccess($scope, $http, order.orderId, product.productId);
			},
			cancel : function() {
				console.log('cancel');
			}
		});
	}
}

var shareSuccess = function($scope, $http, orderId, productId) {
	$scope.order.productShared = 'Y';
	var share = {
		params : {
			orderId : orderId,
			productId : productId
		}
	};
	$http.get('/user/share', share);
}