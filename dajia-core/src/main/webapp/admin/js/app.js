angular.module('DajiaAdmin', [ 'ui.bootstrap', 'ngRoute', 'DajiaAdmin.controllers' ]).config(
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
			}).when('/order/:orderId', {
				templateUrl : './templates/orderDetail.html',
				controller : 'OrderDetailCtrl'
			}).when('/login', {
				templateUrl : './templates/login.html',
				controller : 'SignInCtrl'
			}).otherwise('/products')
		} ]).service('authInterceptor', function($q) {
	var service = this;
	service.responseError = function(response) {
		if (response.status == 401) {
			window.location.href = '#/login';
		}
		return $q.reject(response);
	};
}).config([ '$httpProvider', function($httpProvider) {
	$httpProvider.interceptors.push('authInterceptor');
} ]);