// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('dajia', [ 'ionic', 'ngCookies', 'dajia.controllers', 'dajia.services' ])

.run(function($ionicPlatform) {
	$ionicPlatform.ready(function() {
		// Hide the accessory bar by default (remove this to show the accessory
		// bar above the keyboard
		// for form inputs)
		if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
			cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
			cordova.plugins.Keyboard.disableScroll(true);
			screen.lockOrientation('portrait');
		}
		if (window.StatusBar) {
			// org.apache.cordova.statusbar required
			StatusBar.styleLightContent();
		}
	});
})

.config(function($stateProvider, $urlRouterProvider) {

	// Ionic uses AngularUI Router which uses the concept of states
	// Learn more here: https://github.com/angular-ui/ui-router
	// Set up the various states which the app can be in.
	// Each state's controller can be found in controllers.js
	$stateProvider

	// setup an abstract state for the tabs directive
	.state('tab', {
		url : '/tab',
		abstract : true,
		templateUrl : 'templates/tabs.html'
	})

	// Each tab has its own nav history stack:

	.state('tab.prod', {
		url : '/prod',
		views : {
			'tab-prod' : {
				templateUrl : 'templates/tab-products.html',
				controller : 'ProdCtrl'
			}
		}
	}).state('tab.prod-detail', {
		url : '/prod/:pid',
		views : {
			'tab-prod' : {
				templateUrl : 'templates/prod-detail.html',
				controller : 'ProdDetailCtrl'
			}
		}
	}).state('tab.prod-detail-rec', {
		url : '/prod/:pid/:refuserid',
		views : {
			'tab-prod' : {
				templateUrl : 'templates/prod-detail.html',
				controller : 'ProdDetailCtrl'
			}
		}
	}).state('tab.prod-order', {
		url : '/prodorder/:pid',
		views : {
			'tab-prod' : {
				templateUrl : 'templates/order.html',
				controller : 'OrderCtrl'
			}
		}
	}).state('tab.prod-order-rec', {
		url : '/prodorder/:pid/:refuserid',
		views : {
			'tab-prod' : {
				templateUrl : 'templates/order.html',
				controller : 'OrderCtrl'
			}
		}
	}).state('tab.prog', {
		url : '/prog',
		views : {
			'tab-prog' : {
				templateUrl : 'templates/tab-progress.html',
				controller : 'ProgCtrl'
			}
		}
	}).state('tab.prog-detail', {
		url : '/prog/:trackingId',
		views : {
			'tab-prog' : {
				templateUrl : 'templates/prog-detail.html',
				controller : 'ProgDetailCtrl'
			}
		}
	}).state('tab.mine', {
		url : '/mine',
		views : {
			'tab-mine' : {
				templateUrl : 'templates/tab-mine.html',
				controller : 'MineCtrl'
			}
		}
	}).state('tab.mine-orders', {
		url : '/mine/orders',
		views : {
			'tab-mine' : {
				templateUrl : 'templates/order-list.html',
				controller : 'MyOrdersCtrl'
			}
		}
	}).state('tab.mine-order', {
		url : '/mine/order/:trackingId',
		views : {
			'tab-mine' : {
				templateUrl : 'templates/order-detail.html',
				controller : 'MyOrderDetailCtrl'
			}
		}
	}).state('tab.mine-fav', {
		url : '/mine/fav',
		views : {
			'tab-mine' : {
				templateUrl : 'templates/fav-list.html',
				controller : 'MyFavCtrl'
			}
		}
	}).state('tab.mine-pass', {
		url : '/mine/password',
		views : {
			'tab-mine' : {
				templateUrl : 'templates/password.html',
				controller : 'MyPassCtrl'
			}
		}
	}).state('tab.mine-mobile', {
		url : '/mine/bindmobile',
		views : {
			'tab-mine' : {
				templateUrl : 'templates/bindMobile.html',
				controller : 'BindMobileCtrl'
			}
		}
	}).state('tab.mine-contacts', {
		url : '/mine/contacts',
		views : {
			'tab-mine' : {
				templateUrl : 'templates/contact-list.html',
				controller : 'ListContactCtrl'
			}
		}
	}).state('tab.mine-contact', {
		url : '/mine/contact/:contactId',
		views : {
			'tab-mine' : {
				templateUrl : 'templates/contact-detail.html',
				controller : 'EditContactCtrl'
			}
		}
	}).state('tab.mine-qcode', {
		url : '/mine/qcode',
		views : {
			'tab-mine' : {
				templateUrl : 'templates/qcode.html',
				controller : 'QcodeCtrl'
			}
		}
	}).state('error', {
		url : '/error',
		templateUrl : 'templates/error.html',
		controller : 'ErrorCtrl'
	});

	// if none of the above states are matched, use this as the fallback
	$urlRouterProvider.otherwise('/tab/prod');

}).service('errorInterceptor', function($q) {
	var service = this;
	service.responseError = function(response) {
		console.log(response);
		if (response.status >= 404) {
			window.location.href = '#/error';
		}
		return $q.reject(response);
	};
}).config(function($ionicConfigProvider, $httpProvider) {
	var appVersion = navigator.appVersion;
	console.log(appVersion);
	$httpProvider.defaults.withCredentials = true;
	$ionicConfigProvider.backButton.text('返回').icon('ion-chevron-left');
	$ionicConfigProvider.tabs.position('bottom');
});
