angular.module('starter.controllers', [ "ui.bootstrap", "countTo" ]).controller('ProdCtrl', function($scope, $http) {
	console.log('产品列表...');
	$http.get('/products/').success(function(data, status, headers, config) {
		console.log(data);
		$scope.products = data;
	}).error(function(data, status, headers, config) {
		console.log('request failed...');
	});
})

.controller(
		'ProdDetailCtrl',
		function($scope, $stateParams, $state, $window, $timeout, $http, $ionicSlideBoxDelegate, $ionicModal) {
			console.log('产品详情...')
			loginModalInit($scope, $ionicModal);
			$http.get('/product/' + $stateParams.pid).success(
					function(data, status, headers, config) {
						// console.log(data);
						var product = data;
						$scope.product = product;
						$ionicSlideBoxDelegate.update();
						$scope.orderNeeded = product.maxOrder - product.orderNum;
						$scope.nextPriceOff = product.priceOff / product.maxOrder;
						var amt = (product.originalPrice - product.currentPrice)
								/ (product.originalPrice - product.targetPrice) * 100;
						$scope.countTo = product.currentPrice;
						$scope.countFrom = product.originalPrice;
						$scope.buyNow = function() {
							// $window.location.href = '#/tab/prod/' +
							// $stateParams.pid + '/order';
							$scope.openModal();
						}
						$timeout(function() {
							$scope.progressValue = amt;
						}, 1000);
					}).error(function(data, status, headers, config) {
				console.log('request failed...');
			});
			$scope.progressValue = 0;
		})

.controller('ProgCtrl', function($scope, $ionicModal, $timeout, Mocks) {
	console.log('进度列表...');
	loginModalInit($scope, $ionicModal);
	$timeout(function() {
		$scope.openModal();
	}, 500);
	var orders = Mocks.getMyOrders();
	orders.forEach(function(o) {
		o.product = Mocks.getProduct(o.pid);
		o.progressValue = o.product.priceOff / (o.product.oriPrice - o.product.targetPrice) * 100;
	});
	$scope.myOrders = orders;
})

.controller('ProgDetailCtrl', function($scope, $stateParams, Mocks) {
	console.log('进度详情...')
	var order = Mocks.getOrder($stateParams.orderId);
	order.product = Mocks.getProduct(order.pid);
	order.contactInfo = Mocks.getContact(order.contactId);
	order.progressValue = order.product.priceOff / (order.product.oriPrice - order.product.targetPrice) * 100;
	$scope.order = order;
})

.controller('MineCtrl', function($scope) {
	console.log('我的打价...');
	$scope.settings = {
		enableFriends : true
	};
})

.controller('OrderCtrl', function($scope, $stateParams, Mocks) {
	console.log('订单页面...')
	var product = Mocks.getProduct($stateParams.pid);
	var orderItems = [];
	orderItems.push(product);
	$scope.orderItems = orderItems;
	$scope.totalPrice = product.price;
})

.controller('SignInCtrl', function($scope, $http, $state, $ionicLoading, $timeout, AuthService) {
	$scope.login = {
		'userAccount' : null,
		'userPassword' : null
	};
	$scope.submit = function() {
		if (!$scope.login.userAccount || !$scope.login.userPassword) {
			popWarning('请输入完整信息', $timeout, $ionicLoading);
			return;
		}
		AuthService.login($scope.login);
	};

	$scope.$on('event:auth-loginRequired', function(e, rejection) {
		$scope.openModal();
	});

	$scope.$on('event:auth-loginConfirmed', function() {
		$scope.closeModal();
	});

	$scope.$on('event:auth-login-failed', function(e, status) {
		var error = "登录失败";
		if (status == 401) {
			error = "用户名或密码错误";
		}
		popWarning(error, $timeout, $ionicLoading);
	});

	$scope.$on('event:auth-logout-complete', function() {
		// $state.go("home");
		$scope.openModal();
	});
})

.controller('SignOutCtrl', function($scope, AuthenticationService) {
	AuthenticationService.logout();
});

var loginModalInit = function($scope, $ionicModal) {
	$ionicModal.fromTemplateUrl('templates/login.html', {
		scope : $scope,
		animation : 'slide-in-up'
	}).then(function(modal) {
		$scope.loginModal = modal;
	});
	$scope.openModal = function() {
		$scope.loginModal.show();
	};
	$scope.closeModal = function() {
		$scope.loginModal.hide();
	};
	s
	$scope.$on('$destroy', function() {
		$scope.loginModal.remove();
	});
	$scope.$on('modal.hidden', function() {
		// Execute action
	});
	$scope.$on('modal.removed', function() {
		// Execute action
	});
}

var popWarning = function(msg, $timeout, $ionicLoading) {
	$ionicLoading.show({
		template : msg
	});
	$timeout(function() {
		$ionicLoading.hide();
	}, 800);
}