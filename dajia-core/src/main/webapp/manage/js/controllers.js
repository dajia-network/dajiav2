angular.module('DajiaMana.controllers', []).controller('ProductsCtrl', function($scope, $http) {
	console.log('ProductsCtrl...');
	$http.get('/products/').success(function(data, status, headers, config) {
		console.log(data);
		$scope.products = data;
		$scope.editProduct = function(pid) {
			window.location.href = '#/product/' + pid;
		};
	}).error(function(data, status, headers, config) {
		console.log('request failed...');
	});
}).controller('OrdersCtrl', function($scope, $http) {
	console.log('OrdersCtrl...');
}).controller('ClientsCtrl', function($scope, $http) {
	console.log('ClientsCtrl...');
}).controller('ProductDetailCtrl', function($scope, $http, $routeParams) {
	console.log('ProductDetailCtrl...');
	$http.get('/product/' + $routeParams.pid).success(function(data, status, headers, config) {
		console.log(data);
		var product = data;
		$scope.product = product;
		$scope.go2Kdt = function(refId) {
			window.location.href = 'https://koudaitong.com/v2/showcase/goods/edit#id=' + refId;
		};
		$scope.submit = function() {
			$http.post('/product/' + $routeParams.pid, $scope.product).success(function(data, status, headers, config) {
				window.location = '#';
			}).error(function(data, status, headers, config) {
				console.log('product update failed...');
			});
		}
	}).error(function(data, status, headers, config) {
		console.log('request failed...');
	});
});
