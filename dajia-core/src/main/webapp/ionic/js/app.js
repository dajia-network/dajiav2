// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('starter', [ 'ionic', 'ngCookies', 'starter.controllers', 'starter.services' ])

.run(function($ionicPlatform) {
	$ionicPlatform.ready(function() {
		// Hide the accessory bar by default (remove this to show the accessory
		// bar above the keyboard
		// for form inputs)
		if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
			cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
			cordova.plugins.Keyboard.disableScroll(true);

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
	})

	.state('tab.prod-order', {
		url : '/prod/:pid/order',
		views : {
			'tab-prod' : {
				templateUrl : 'templates/order.html',
				controller : 'OrderCtrl'
			}
		}
	})

	.state('tab.prog', {
		url : '/prog',
		views : {
			'tab-prog' : {
				templateUrl : 'templates/tab-progress.html',
				controller : 'ProgCtrl'
			}
		}
	}).state('tab.prog-detail', {
		url : '/prog/:orderId',
		views : {
			'tab-prog' : {
				templateUrl : 'templates/prog-detail.html',
				controller : 'ProgDetailCtrl'
			}
		}
	})

	.state('tab.mine', {
		url : '/mine',
		views : {
			'tab-mine' : {
				templateUrl : 'templates/tab-mine.html',
				controller : 'MineCtrl'
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
	});

	// if none of the above states are matched, use this as the fallback
	$urlRouterProvider.otherwise('/tab/prod');

}).config(function($ionicConfigProvider, $httpProvider) {
	var appVersion = navigator.appVersion;
	console.log(appVersion);
	$httpProvider.defaults.withCredentials = true;
});
