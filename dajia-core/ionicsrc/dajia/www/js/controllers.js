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
		function($scope, $rootScope, $stateParams, $http, $cookieStore, $window, $timeout, $ionicSlideBoxDelegate,
				$ionicModal) {
			console.log('产品详情...')
			modalInit($scope, $ionicModal, 'login');
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
							var loginUser = $cookieStore.get('loginUser');
							if (loginUser == null) {
								$rootScope.$broadcast('event:auth-loginRequired');
							} else {
								$window.location.href = '#/tab/prod/' + $stateParams.pid + '/order';
							}
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
	modalInit($scope, $ionicModal, 'login');
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

.controller('OrderCtrl', function($scope, $rootScope, $stateParams, $http, $ionicModal) {
	console.log('订单页面...')
	modalInit($scope, $ionicModal, 'login');
	$http.get('/user/product/' + $stateParams.pid + '/order').success(function(data, status, headers, config) {
		// console.log(data);
		var product = data;
		var orderItems = [];
		orderItems.push(product);
		$scope.orderItems = orderItems;
		$scope.totalPrice = product.price;
	}).error(function(data, status, headers, config) {
		console.log('request failed...');
	});

})

.controller('SignInCtrl', function($scope, $ionicLoading, $timeout, AuthService, $ionicModal) {
	modalInit($scope, $ionicModal, 'signup');
	$scope.login = {
		'mobile' : null,
		'password' : null
	};
	$scope.submit = function() {
		if (!$scope.login.mobile || !$scope.login.password) {
			popWarning('请输入完整信息', $timeout, $ionicLoading);
			return;
		}
		AuthService.login($scope.login);
	};
	$scope.signup = function() {
		$scope.openModal('signup');
	}

	$scope.$on('event:auth-loginRequired', function(e, rejection) {
		$scope.openModal('login');
	});

	$scope.$on('event:auth-loginConfirmed', function() {
		$scope.closeModal('login');
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

.controller('SignUpCtrl', function($scope, $http, $ionicLoading, $timeout, AuthService) {
	$scope.login = {
		'mobile' : null,
		'password' : null
	};
	$scope.submit = function() {
		if (!$scope.signup.mobile || !$scope.signup.password) {
			popWarning('请输入完整信息', $timeout, $ionicLoading);
			return;
		}
	};
})

.controller('SignOutCtrl', function($scope, AuthService) {
	AuthService.logout();
});

var modalInit = function($scope, $ionicModal, modalType) {
	console.log($ionicModal);
	$ionicModal.fromTemplateUrl('templates/' + modalType + '.html', {
		scope : $scope,
		animation : 'slide-in-up'
	}).then(function(modal) {
		$scope['modal_' + modalType] = modal;
	});
	$scope.openModal = function(type) {
		$scope['modal_' + type].show();
	};
	$scope.closeModal = function(type) {
		$scope['modal_' + type].hide();
	};
	$scope.$on('$destroy', function() {
		$scope['modal_' + modalType].remove();
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