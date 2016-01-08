angular.module('MyApp', [ 'ngRoute', 'MyApp.controllers' ]).config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/products', {
		templateUrl : './templates/products.html',
		controller : 'ProductsCtrl'
	}).otherwise('/products')
} ]);
