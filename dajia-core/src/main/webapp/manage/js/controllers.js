angular.module('DajiaMana.controllers', []).controller('ProductsCtrl', function($scope, $http, $route) {
	console.log('ProductsCtrl...');
	$http.get('/products/1').success(function(data, status, headers, config) {
		console.log(data);
		$scope.syncBtnTxt = '同步数据';
		$scope.pager = data;
		$scope.products = data.results;
		$scope.editProduct = function(pid) {
			window.location.href = '#/product/' + pid;
		};
		$scope.sync = function() {
			$scope.syncBtnTxt = '进行中...';
			$http.get('/sync/').success(function(data, status, headers, config) {
				console.log(data);
				$scope.syncBtnTxt = '同步数据';
			}).error(function(data, status, headers, config) {
				console.log('request failed...');
			});
		};
		$scope.bot = function(pid) {
			$http.get('/robotorder/' + pid).success(function(data, status, headers, config) {
				console.log(data);
				$route.reload();
			}).error(function(data, status, headers, config) {
				console.log('request failed...');
			});
		}
	}).error(function(data, status, headers, config) {
		console.log('request failed...');
	});
}).controller('OrdersCtrl', function($scope, $http) {
	console.log('OrdersCtrl...');
}).controller('ClientsCtrl', function($scope, $http) {
	console.log('ClientsCtrl...');
	$http.get('/users/1').success(function(data, status, headers, config) {
		console.log(data);
		$scope.pager = data;
		$scope.users = data.results;
	}).error(function(data, status, headers, config) {
		console.log('request failed...');
	});
}).controller(
		'ProductDetailCtrl',
		function($scope, $http, $routeParams, $route) {
			console.log('ProductDetailCtrl...');
			$http.get('/product/' + $routeParams.pid).success(
					function(data, status, headers, config) {
						$scope.newSold = null;
						$scope.newPrice = null;
						console.log(data);
						var product = data;
						if (null == product.startDate) {
							product.startDate = new Date();
						} else {
							product.startDate = new Date(product.startDate);
						}
						if (null == product.expiredDate) {
							product.expiredDate = new Date();
						} else {
							product.expiredDate = new Date(product.expiredDate);
						}
						$scope.product = product;

						$scope.go2Kdt = function(refId) {
							window.location.href = 'https://koudaitong.com/v2/showcase/goods/edit#id=' + refId;
						};
						$scope.addPrice = function() {
							if (null != $scope.newSold && $scope.newSold != 0 && null != $scope.newPrice
									&& $scope.newPrice != 0) {
								var priceObj = {
									sold : $scope.newSold,
									targetPrice : $scope.newPrice
								};
								if (null == $scope.product.prices) {
									$scope.product.prices = [];
								}
								$scope.product.prices.push(priceObj);
								$http.post('/product/' + $routeParams.pid, $scope.product).success(
										function(data, status, headers, config) {
											$route.reload();
										}).error(function(data, status, headers, config) {
									console.log('product update failed...');
									console.log(data.message);
								});
							}
						};
						$scope.removePrice = function(priceId) {
							for (var i = $scope.product.prices.length - 1; i >= 0; i--) {
								if ($scope.product.prices[i].priceId == priceId) {
									$scope.product.prices.splice(i, 1);
								}
							}
							$http.post('/product/' + $routeParams.pid, $scope.product).success(
									function(data, status, headers, config) {
										$route.reload();
									}).error(function(data, status, headers, config) {
								console.log('product update failed...');
								console.log(data.message);
							});
						};
						$scope.submit = function() {
							$http.post('/product/' + $routeParams.pid, $scope.product).success(
									function(data, status, headers, config) {
										window.location = '#';
									}).error(function(data, status, headers, config) {
								console.log('product update failed...');
								console.log(data.message);
							});
						}
					}).error(function(data, status, headers, config) {
				console.log('request failed...');
			});
		});
