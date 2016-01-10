angular.module('DajiaMana', [ 'ui.bootstrap', 'ngRoute', 'DajiaMana.controllers' ]).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/products', {
				templateUrl : './templates/products.html',
				controller : 'ProductsCtrl'
			}).when('/orders', {
				templateUrl : './templates/orders.html',
				controller : 'OrdersCtrl'
			}).when('/clients', {
				templateUrl : './templates/clients.html',
				controller : 'ClientsCtrl'
			}).when('/product/:pid', {
				templateUrl : './templates/productDetail.html',
				controller : 'ProductDetailCtrl'
			}).otherwise('/products')
		} ]);