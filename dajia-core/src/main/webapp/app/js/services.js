var dajia = angular.module('dajia.services', [ 'http-auth-interceptor' ]);

dajia.factory('AuthService', function($rootScope, $http, $cookies, authService) {
	var service = {
		signup : function(signup) {
			$http.post('/signup', signup).success(function(data, status, headers, config) {
				if (null == data || data.length == 0) {
					$rootScope.$broadcast('event:auth-signup-failed', status);
				} else {
					addCookies($cookies, data);
					$rootScope.$broadcast('event:auth-signup-success', status);
					authService.loginConfirmed();
				}
			}).error(function(data, status, headers, config) {
				$rootScope.$broadcast('event:auth-signup-failed', status);
			});
		},
		login : function(login) {
			$http.post('/smslogin', login).success(function(data, status, headers, config) {
				if (data == null || data.length == 0) {
					$rootScope.$broadcast('event:auth-login-failed', status);
				} else {
					addCookies($cookies, data);
					authService.loginConfirmed();
				}
			}).error(function(data, status, headers, config) {
				$rootScope.$broadcast('event:auth-login-failed', status);
			});
		},
		oauthLogin : function(data) {
			addCookies($cookies, data);
			authService.loginConfirmed();
		},
		logout : function(logout) {
			$http.post('/logout', logout).success(function(data, status) {
				removeCookies($cookies);
				$rootScope.$broadcast('event:auth-logout-complete');
			});
		},
		loginCancelled : function() {
			authService.loginCancelled();
		}
	};

	var addCookies = function($cookies, data) {
		$cookies.put('dajia_user_mobile', data['mobile'], {
			path : '/'
		});
		$cookies.put('dajia_user_oauth_id', data['oauthUserId'], {
			path : '/'
		});
		$cookies.put('dajia_user_id', data['userId'], {
			path : '/'
		});
		$cookies.put('dajia_username', data['userName'], {
			path : '/'
		});
	}
	var removeCookies = function($cookies) {
		$cookies.remove('dajia_user_mobile', {
			path : '/'
		});
		$cookies.remove('dajia_user_oauth_id', {
			path : '/'
		});
		$cookies.remove('dajia_user_id', {
			path : '/'
		});
		$cookies.remove('dajia_username', {
			path : '/'
		});
	}
	return service;
})
.factory('couponService', function($rootScope, $http, $cookies, authService) {
	var requestCoupon = function(couponId, _success_callback, _fail_callback) {
		$http.get('/user/coupons/request/' + couponId).success(function(data, status, headers, config) {
			_success_callback.call(data, status, headers, config);
		}).error(function(data, status, headers, config) {
			_fail_callback.call(data, status, headers, config);
		});
	};

	var canRequestCoupon = function(couponId) {
		$http.get("/user/coupons/canRequest/" + couponId).success(function(data) {
			console.log(data);
		}).error(function() {
			return false;
		});
	}
});