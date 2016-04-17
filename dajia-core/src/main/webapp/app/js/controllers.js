angular.module('starter.controllers', [ "ui.bootstrap", "countTo" ]).controller('ProdCtrl',
		function($scope, $http, $cookies, $ionicLoading, AuthService) {
			console.log('产品列表...');
			var loadProducts = function() {
				popLoading($ionicLoading);
				return $http.get('/products/').success(function(data, status, headers, config) {
					$scope.products = data;
					$scope.$broadcast('scroll.refreshComplete');
					$ionicLoading.hide();
				});
			}
			var checkOauthLogin = function() {
				if (!$cookies.get('dajia_user')) {
					$http.get('/user/loginuserinfo').success(function(data, status, headers, config) {
						var loginuser = data;
						if (null != loginuser['userId']) {
							AuthService.oauthLogin(loginuser);
						}
					}).error(function(data, status, headers, config) {
						console.log('request failed...');
					});
				}
			}
			checkOauthLogin();
			loadProducts();
			$scope.doRefresh = function() {
				loadProducts();
			};
		})

.controller(
		'ProdDetailCtrl',
		function($scope, $rootScope, $stateParams, $http, $cookies, $window, $timeout, $ionicSlideBoxDelegate,
				$ionicModal, $ionicLoading) {
			console.log('产品详情...')
			$scope.favBtnTxt = '收藏';
			var element = angular.element(document.querySelector('#fav_icon'));
			modalInit($rootScope, $ionicModal, 'login');

			$http.get('/user/checkfav/' + $stateParams.pid).success(function(data, status, headers, config) {
				var isFav = data;
				$scope.isFav = isFav;
				if ($scope.isFav) {
					$scope.favBtnTxt = '已收藏';
					element.addClass('assertive');
				}
			}).error(function(data, status, headers, config) {
				console.log('request failed...');
			});

			$scope.buyNow = function() {
				var loginUser = $cookies.get('dajia_user');
				if (loginUser == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					$window.location.href = '#/tab/prod/' + $stateParams.pid + '/order';
				}
			}

			$scope.add2Fav = function() {
				var loginUser = $cookies.get('dajia_user');
				if (loginUser == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					if ($scope.isFav) {
						$http.get('/user/favourite/remove/' + $stateParams.pid).success(
								function(data, status, headers, config) {
									popWarning('已取消收藏', $timeout, $ionicLoading);
									$scope.isFav = false;
									$scope.favBtnTxt = '收藏';
									element.removeClass('assertive');
								}).error(function(data, status, headers, config) {
							console.log('request failed...');
						});
					} else {
						$http.get('/user/favourite/add/' + $stateParams.pid).success(
								function(data, status, headers, config) {
									popWarning('收藏成功', $timeout, $ionicLoading);
									$scope.isFav = true;
									$scope.favBtnTxt = '已收藏';
									element.addClass('assertive');
								}).error(function(data, status, headers, config) {
							console.log('request failed...');
						});
					}
				}
			}

			$scope.share = function() {
				var loginUser = $cookies.get('dajia_user');
				if (loginUser == null) {
					$rootScope.$broadcast('event:auth-loginRequired');
				} else {
					popWarning('请点击右上角微信菜单-发送给朋友', $timeout, $ionicLoading);
					wx.onMenuShareAppMessage({
						title : '打价网',
						desc : $scope.product.name,
						link : window.location.href,
						imgUrl : 'http://51daja.com/app/img/logo.png',
						trigger : function() {
							console.log('click');
						},
						success : function() {
							popWarning('分享成功！', $timeout, $ionicLoading);
						},
						cancel : function() {
							console.log('cancel');
						}
					});
					wx.onMenuShareTimeline({
						title : '打价网 - ' + $scope.product.name,
						link : window.location.href,
						imgUrl : 'http://51daja.com/app/img/logo.png',
						trigger : function() {
							console.log('click');
						},
						success : function() {
							popWarning('分享成功！', $timeout, $ionicLoading);
						},
						cancel : function() {
							console.log('cancel');
						}
					});
				}
			}

			popLoading($ionicLoading);
			$http.get('/product/' + $stateParams.pid).success(
					function(data, status, headers, config) {
						var product = data;
						$scope.product = product;
						$ionicSlideBoxDelegate.update();
						$scope.orderNeeded = product.maxOrder - product.orderNum;
						$scope.nextPriceOff = product.priceOff / product.maxOrder;
						var amt = (product.originalPrice - product.currentPrice)
								/ (product.originalPrice - product.targetPrice) * 100;
						$scope.countTo = product.currentPrice;
						$scope.countFrom = product.originalPrice;
						$ionicLoading.hide();
						$timeout(function() {
							$scope.progressValue = amt;
						}, 1000);
					});

			$http.get('/wechat/signature').success(function(data, status, headers, config) {
				console.log(data);
				wx.config({
					appId : data['appId'],
					timestamp : data['timestamp'],
					nonceStr : data['nonceStr'],
					signature : data['signature'],
					jsApiList : [ 'checkJsApi', 'onMenuShareAppMessage', 'onMenuShareTimeline' ]
				});
				wx.checkJsApi({
					jsApiList : [ 'onMenuShareAppMessage', 'onMenuShareTimeline' ],
					success : function(res) {
						console.log(res);
					}
				});
				wx.ready(function() {

				});
			});
			$scope.progressValue = 0;
		})

.controller('ProgCtrl', function($scope, $rootScope, $window, $http, $cookies, $ionicModal, $timeout, $ionicLoading) {
	console.log('进度列表...');
	$scope.loginUser = $cookies.get('dajia_user');
	modalInit($rootScope, $ionicModal, 'login');
	$scope.login = function() {
		if ($scope.loginUser == null) {
			$rootScope.$broadcast('event:auth-loginRequired');
		} else {
			$window.location.reload();
		}
	}
	var loadProgress = function() {
		popLoading($ionicLoading);
		$http.get('/user/progress').success(function(data, status, headers, config) {
			console.log(data);
			var orders = data;
			orders.forEach(function(o) {
				o.progressValue = o.product.priceOff / (o.product.originalPrice - o.product.targetPrice) * 100;
			});
			$scope.myOrders = orders;
			$scope.$broadcast('scroll.refreshComplete');
			$ionicLoading.hide();
		});
	}
	if ($scope.loginUser != null) {
		loadProgress();
	}
	$scope.doRefresh = function() {
		loadProgress();
	}
	$scope.goHome = function() {
		$window.location.href = "#/tab/prod";
	}
})

.controller('ProgDetailCtrl', function($scope, $stateParams, $http, $ionicModal, $timeout, $ionicLoading) {
	console.log('进度详情...')
	$scope.order = {};
	popLoading($ionicLoading);
	$http.get('/user/order/' + $stateParams.orderId).success(function(data, status, headers, config) {
		console.log(data);
		var order = data;
		order.progressValue = order.product.priceOff / (order.product.originalPrice - order.product.targetPrice) * 100;
		$scope.order = order;
		$ionicLoading.hide();
	});
	$scope.order.progressValue = 0;
})

.controller('MineCtrl', function($scope, $rootScope, $window, $cookies, $timeout, $ionicLoading, AuthService) {
	console.log('我的打价...');
	$scope.userName = $cookies.get('dajia_username');
	var loginUser = $cookies.get('dajia_user');
	$scope.myFav = function() {
		if (loginUser == null) {
			$rootScope.$broadcast('event:auth-loginRequired');
		} else {
			$window.location.href = '#/tab/mine/fav';
		}
	}
	$scope.myPass = function() {
		if (loginUser == null) {
			$rootScope.$broadcast('event:auth-loginRequired');
		} else {
			$window.location.href = '#/tab/mine/password';
		}
	}
	$scope.logout = function() {
		if (loginUser == null) {
			$window.location.reload();
		} else {
			AuthService.logout(loginUser);
		}
	};
	$scope.$on('event:auth-logout-complete', function() {
		popWarning('退出登录成功', $timeout, $ionicLoading);
		$timeout(function() {
			$window.location.reload();
		}, 500);
		// $scope.openModal('login');
	});
})

.controller('MyFavCtrl', function($scope, $http, $ionicLoading) {
	console.log('我的收藏...');
	var loadFavs = function() {
		popLoading($ionicLoading);
		return $http.get('/user/favourites').success(function(data, status, headers, config) {
			$scope.products = data;
			$ionicLoading.hide();
		});
	}
	loadFavs();
	$scope.doRefresh = function() {
		loadFavs();
	};
})

.controller('MyPassCtrl', function($scope, $http, $timeout, $ionicLoading) {
	console.log('修改密码...');
	$scope.form = {};
	$scope.submit = function() {
		var oldPassword = $scope.form.oldPassword;
		var newPassword = $scope.form.newPassword;
		var newPasswordConfirm = $scope.form.newPasswordConfirm;
		if (!oldPassword || !newPassword || !newPasswordConfirm) {
			popWarning('请输入完整信息', $timeout, $ionicLoading);
			return;
		}
		if (newPassword.length < 6) {
			popWarning('请输入至少六位数的密码', $timeout, $ionicLoading);
			return;
		}
		if (newPassword != newPasswordConfirm) {
			popWarning('两次输入的新密码不一致', $timeout, $ionicLoading);
			return;
		}
		if (newPassword == oldPassword) {
			popWarning('新密码不能与老密码相同', $timeout, $ionicLoading);
			return;
		}
		$http.post('/user/changePassword', $scope.form).success(function(data, status, headers, config) {
			var msg = data.msg;
			popWarning(msg, $timeout, $ionicLoading);
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
		});

	};
})

.controller('OrderCtrl', function($scope, $rootScope, $stateParams, $http, $ionicModal, $timeout, $ionicLoading) {
	console.log('订单页面...')
	var productReady = false;
	var locationReady = false;
	popLoading($ionicLoading);
	modalInit($rootScope, $ionicModal, 'login');
	$scope.userContact = {};
	$scope.order = {
		'quantity' : 1,
		'unitPrice' : 0,
		'totalPrice' : 0,
		'payType' : 1
	};
	var quota = 5;
	$http.get('/product/' + $stateParams.pid).success(function(data, status, headers, config) {
		var product = data;
		$scope.orderItem = product;
		$scope.totalPrice = product.price;
		quota = product.buyQuota;
		$scope.order.productId = product.productId;
		$scope.order.unitPrice = product.currentPrice;
		$scope.order.totalPrice = $scope.order.quantity * $scope.order.unitPrice;
		if (locationReady) {
			$ionicLoading.hide();
		}
		productReady = true;
	});
	$http.get('/user/loginuserinfo').success(function(data, status, headers, config) {
		var loginuser = data;
		$scope.loginuser = loginuser;
	}).error(function(data, status, headers, config) {
		console.log('request failed...');
	});
	$http.get('/locations').success(function(data, status, headers, config) {
		$scope.provinces = data;
		if ($scope.loginuser.userContact != null) {
			$scope.userContact = $scope.loginuser.userContact;
			$scope.provinces.forEach(function(p) {
				if (p.locationKey == $scope.loginuser.userContact.province.locationKey) {
					$scope.userContact.province = p;
					p.children.forEach(function(c) {
						if (c.locationKey == $scope.loginuser.userContact.city.locationKey) {
							$scope.userContact.city = c;
							c.children.forEach(function(d) {
								if (d.locationKey == $scope.loginuser.userContact.district.locationKey) {
									$scope.userContact.district = d;
									if (productReady) {
										$ionicLoading.hide();
									}
									locationReady = true;
									return;
								}
							});
							return;
						}
					});
					return;
				}
			});
		} else {
			if (productReady) {
				$ionicLoading.hide();
			}
			locationReady = true;
		}
	}).error(function(data, status, headers, config) {
		console.log('request failed...');
	});
	$scope.submit = function() {
		if ($scope.userContact.contactId == null) {
			console.log('new userContact.');
		}
		var name = $scope.userContact.contactName;
		var mobile = $scope.userContact.contactMobile;
		var province = $scope.userContact.province;
		var city = $scope.userContact.city;
		var district = $scope.userContact.district;
		var address = $scope.userContact.address1;

		if (!name || !mobile || !province || !city || !district || !address) {
			popWarning('请输入完整信息', $timeout, $ionicLoading);
			return;
		}
		var mobileReg = /^(((13[0-9]{1})|159|153)+\d{8})$/;
		if (mobile.length != 11 || !mobileReg.test(mobile)) {
			popWarning('请数据正确的手机号码', $timeout, $ionicLoading);
			return;
		}

		$scope.order.userContact = $scope.userContact;

		$http.post('/user/submitOrder', $scope.order).success(function(data, status, headers, config) {
			var charge = data;
			console.log(charge);
			pingpp.createPayment(charge, function(result, error) {
				if (result == "success") {
					// 只有微信公众账号 wx_pub 支付成功的结果会在这里返回，其他的 wap 支付结果都是在 extra
					// 中对应的URL 跳转。
					console.log('wechat pay success');
				} else if (result == "fail") {
					// charge 不正确或者微信公众账号支付失败时会在此处返回
					console.log('payment failed');
					for (key in error) {
						alert(key + ': ' + error[key]);
					}
					// alert(error['msg']);
				} else if (result == "cancel") {
					// 微信公众账号支付取消支付
					console.log('wechat pay cancelled');
				}
			});
			// popWarning('订单已确认', $timeout, $ionicLoading);
			// payModalInit($scope, $ionicModal, function() {
			// console.log(order);
			// $scope.openPayModal(order);
			// });

		}).error(function(data, status, headers, config) {
			console.log('request failed...');
		});
	}
	$scope.add = function() {
		if ($scope.order.quantity >= quota && quota != null) {
			popWarning('该产品每个账号限购' + quota + '件', $timeout, $ionicLoading);
			return;
		}
		$scope.order.quantity += 1;
		$scope.order.totalPrice = $scope.order.quantity * $scope.order.unitPrice;
	}
	$scope.remove = function() {
		if ($scope.order.quantity > 1) {
			$scope.order.quantity -= 1;
			$scope.order.totalPrice = $scope.order.quantity * $scope.order.unitPrice;
		}
	}
	$scope.selectAlipay = function() {
		popWarning('由于微信技术屏蔽，选择支付宝购买可能需要打开独立浏览器。', $timeout, $ionicLoading);
	}
})

.controller('SignInCtrl', function($scope, $rootScope, $window, $ionicLoading, $timeout, $ionicModal, AuthService) {
	modalInit($rootScope, $ionicModal, 'signup');
	$scope.login = {
		'mobile' : null,
		'password' : null
	};

	$scope.submit = function() {
		if (!$scope.login.mobile || !$scope.login.password) {
			popWarning('请输入完整信息', $timeout, $ionicLoading);
			return;
		}
		AuthService.login($scope.login);
	};

	$scope.signup = function() {
		$scope.openModal('signup');
	}

	$scope.wechatLogin = function() {
		$window.location.href = '/wechat/login';
	}

	$scope.$on('event:auth-loginRequired', function(e, rejection) {
		$scope.openModal('login');
	});

	$scope.$on('event:auth-loginConfirmed', function() {
		$scope.closeModal('login');
		popWarning('登陆成功', $timeout, $ionicLoading);
		$timeout(function() {
			$window.location.reload();
		}, 500);
	});

	$scope.$on('event:auth-login-failed', function(e, status) {
		var error = "登录失败";
		if (status == 401) {
			error = "用户名或密码错误";
		}
		popWarning(error, $timeout, $ionicLoading);
	});
})

.controller('SignUpCtrl', function($scope, $http, $q, $ionicLoading, $timeout, AuthService) {
	$scope.signup = {
		'mobile' : null,
		'password' : null,
		'signupCode' : null
	};
	$scope.smsBtnTxt = '发送手机验证码';
	$scope.smsBtnDisable = false;
	var smsBtn = angular.element(document.querySelector('#smsBtn'));

	var checkMobile = function(mobile) {
		var defer = $q.defer();
		$http.get('/signupCheck/' + mobile).success(function(data, status, headers, config) {
			if ("success" == data.result) {
				defer.resolve(true);
			} else {
				popWarning('该手机号已被注册', $timeout, $ionicLoading);
				defer.resolve(false);
			}
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
			defer.reject();
		});
		return defer.promise;
	}

	$scope.getSignupCode = function() {
		var mobile = $scope.signup.mobile;
		var mobileReg = /^(((13[0-9]{1})|159|153)+\d{8})$/;
		if (!mobile || mobile.length != 11 || !mobileReg.test(mobile)) {
			popWarning('请输入正确的手机号码', $timeout, $ionicLoading);
			return;
		}
		checkMobile(mobile).then(function(mobileValid) {
			if (mobileValid) {
				var counter = 60;
				var onTimeout = function() {
					counter--;
					if (counter == 0) {
						$scope.smsBtnTxt = '发送手机验证码';
						$scope.smsBtnDisable = false;
						return false;
					}
					$scope.smsBtnTxt = '发送手机验证码 (' + counter + ')';
					mytimeout = $timeout(onTimeout, 1000);
				}
				var mytimeout = $timeout(onTimeout, 1000);
				$scope.smsBtnDisable = true;

				$http.get('/signupSms/' + mobile).success(function(data, status, headers, config) {
					if ("success" == data.result) {
						popWarning('验证码已发送', $timeout, $ionicLoading);
					} else {
						popWarning('验证码发送失败', $timeout, $ionicLoading);
					}
				}).error(function(data, status, headers, config) {
					console.log('request failed...');
				});
			}
		});
	}

	$scope.submit = function() {
		var mobile = $scope.signup.mobile;
		var password = $scope.signup.password;
		var signupCode = $scope.signup.signupCode;
		if (!mobile || !password || !signupCode) {
			popWarning('请输入完整信息', $timeout, $ionicLoading);
			return;
		}
		var mobileReg = /^(((13[0-9]{1})|159|153)+\d{8})$/;
		if (mobile.length != 11 || !mobileReg.test(mobile)) {
			popWarning('请数据正确的手机号码', $timeout, $ionicLoading);
			return;
		}
		if (password.length < 6) {
			popWarning('请输入至少六位数的密码', $timeout, $ionicLoading);
			return;
		}
		checkMobile(mobile).then(function(mobileValid) {
			if (mobileValid) {
				AuthService.signup($scope.signup);
			}
		});
	};

	$scope.$on('event:auth-signup-failed', function(e, status) {
		var error = "注册失败，验证码错误";
		popWarning(error, $timeout, $ionicLoading);
	});

	$scope.$on('event:auth-signup-success', function() {
		$scope.closeModal('signup');
		popWarning('注册成功', $timeout, $ionicLoading);
	});
})

.controller('SignOutCtrl', function($scope, AuthService) {
	AuthService.logout();
})

.controller('PayCtrl', function($scope, $http, $ionicLoading, $timeout, $document) {
	console.log('订单支付...');
	$scope.confirmPay = function(order) {
		console.log(order);
		var payBtn = angular.element(document.querySelector('#payBtn'));
	}
});

var modalInit = function($rootScope, $ionicModal, modalType) {
	// console.log($ionicModal);
	$ionicModal.fromTemplateUrl('templates/' + modalType + '.html', {
		scope : $rootScope,
		animation : 'slide-in-up'
	}).then(function(modal) {
		$rootScope['modal_' + modalType] = modal;
	});
	$rootScope.openModal = function(type) {
		$rootScope['modal_' + type].show();
	};
	$rootScope.closeModal = function(type) {
		$rootScope['modal_' + type].hide();
	};
	$rootScope.$on('$destroy', function() {
		$rootScope['modal_' + modalType].remove();
	});
}

var payModalInit = function($scope, $ionicModal, callback) {
	$ionicModal.fromTemplateUrl('templates/pay.html', {
		scope : $scope,
		animation : 'slide-in-up'
	}).then(function(modal) {
		$scope.payModal = modal;
		callback();
	});
	$scope.openPayModal = function(order) {
		$scope.order = order;
		$scope.payModal.show();
	};
	$scope.closePayModal = function() {
		$scope.payModal.hide();
	};
	$scope.$on('$destroy', function() {
		$scope.payModal.remove();
	});
}

var popWarning = function(msg, $timeout, $ionicLoading) {
	$ionicLoading.show({
		template : msg
	});
	$timeout(function() {
		$ionicLoading.hide();
	}, 1500);
}

var popLoading = function($ionicLoading) {
	$ionicLoading.show({
		template : '加载中...'
	});
}