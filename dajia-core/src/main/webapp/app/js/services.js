var starter = angular.module('starter.services', [ 'http-auth-interceptor' ]);

starter.factory('AuthService', function($rootScope, $http, $cookies, authService) {
	var service = {
		signup : function(signup) {
			$http.post('/signup', signup).success(function(data, status, headers, config) {
				if (null == data || data.length == 0) {
					$rootScope.$broadcast('event:auth-signup-failed', status);
				} else {
					$cookies.put('dajia_user', data['mobile'], {
						path : '/'
					});
					$cookies.put('dajia_username', data['userName'], {
						path : '/'
					});
					$rootScope.$broadcast('event:auth-signup-success', status);
					authService.loginConfirmed();
				}
			}).error(function(data, status, headers, config) {
				$rootScope.$broadcast('event:auth-signup-failed', status);
			});
		},
		login : function(login) {
			$http.post('/login', login).success(function(data, status, headers, config) {
				if (data == null || data.length == 0) {
					$rootScope.$broadcast('event:auth-login-failed', status);
				} else {
					$cookies.put('dajia_user', data['mobile'], {
						path : '/'
					});
					$cookies.put('dajia_usertype', 'normal', {
						path : '/'
					});
					$cookies.put('dajia_username', data['userName'], {
						path : '/'
					});
					authService.loginConfirmed();
				}
			}).error(function(data, status, headers, config) {
				$rootScope.$broadcast('event:auth-login-failed', status);
			});
		},
		oauthLogin : function(data) {
			var loginUser = data['user'];
			$cookies.put('dajia_user', loginUser['oauthUserId'], {
				path : '/'
			});
			$cookies.put('dajia_usertype', loginUser['oauthType'], {
				path : '/'
			});
			$cookies.put('dajia_username', data['userName'], {
				path : '/'
			});
			authService.loginConfirmed();
		},
		logout : function(logout) {
			$http.post('/logout', logout).success(function(data, status) {
				$cookies.remove('dajia_user', {
					path : '/'
				});
				$cookies.remove('dajia_username', {
					path : '/'
				});
				$rootScope.$broadcast('event:auth-logout-complete');
			});
		},
		loginCancelled : function() {
			authService.loginCancelled();
		}
	};
	return service;
});