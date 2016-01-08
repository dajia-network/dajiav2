angular.module('MyApp.controllers', []).controller('ProductsCtrl', function($scope, $http) {
	console.log('products...');
	$http.get('/products/').success(function(data, status, headers, config) {
		console.log(data);
		$scope.products = data;
	}).error(function(data, status, headers, config) {
		console.log('request failed...');
	});
});
