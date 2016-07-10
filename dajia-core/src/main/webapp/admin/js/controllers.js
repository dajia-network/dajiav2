angular.module('dajiaAdmin.controllers', []).controller('ProductsCtrl', function($scope, $http, $route, $timeout) {
	console.log('ProductsCtrl...');
	$scope.syncBtnTxt = '同步数据';
	$scope.loadPage = function(pageNum) {
		$http.get('/admin/products/' + pageNum).success(function(data, status, headers, config) {
			$scope.pager = data;
			$scope.products = data.results;
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
		});
	}
	$scope.loadPage(1);
	$scope.sync = function() {
		$scope.alerts = [];
		$scope.syncBtnTxt = '进行中...';
		$http.get('/admin/sync/').success(function(data, status, headers, config) {
			console.log(data);
			$scope.syncBtnTxt = '同步数据';
			$scope.alerts.push({
				type : 'success',
				msg : '同步数据成功'
			});
			$timeout(function() {
				$route.reload();
			}, 1000);

		}).error(function(data, status, headers, config) {
			console.log('request failed...');
			$scope.alerts.push({
				type : 'danger',
				msg : '同步数据失败'
			});
		});
	};
	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};
	$scope.bot = function(pid) {
		$scope.alerts = [];
		$http.get('/admin/robotorder/' + pid).success(function(data, status, headers, config) {
			$scope.alerts.push({
				type : 'success',
				msg : '机器打价成功'
			});
			$timeout(function() {
				$route.reload();
			}, 1000);
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
			$scope.alerts.push({
				type : 'danger',
				msg : '机器打价失败'
			});
		});
	}
	$scope.addProduct = function() {
		window.location.href = '#/product/0';
	}
	$scope.editProduct = function(pid) {
		window.location.href = '#/product/' + pid;
	};
	$scope.delProduct = function(pid) {
		$http.get('/admin/product/remove/' + pid).success(function(data, status, headers, config) {
			window.location = '#';
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
		});
	};
}).controller('OrdersCtrl', function($scope, $http) {
	console.log('OrdersCtrl...');
	$scope.orderFilter = {
		type : 'real',
		status : -1
	};
	$scope.loadPage = function(pageNum) {
		$http.post('/admin/orders/' + pageNum, $scope.orderFilter).success(function(data, status, headers, config) {
			// console.log(data);
			$scope.pager = data;
			$scope.orders = data.results;
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
		});
	}
	$scope.loadPage(1);
	$scope.viewOrder = function(orderId) {
		window.location.href = '#/order/' + orderId;
	}
}).controller('ClientsCtrl', function($scope, $http) {
	console.log('ClientsCtrl...');
	$scope.loadPage = function(pageNum) {
		$http.get('/admin/users/' + pageNum).success(function(data, status, headers, config) {
			console.log(data);
			$scope.pager = data;
			$scope.users = data.results;
		}).error(function(data, status, headers, config) {
			console.log('request failed...');
		});
	}
	$scope.loadPage(1);
}).controller(
		'ProductDetailCtrl',
		function($scope, $http, $routeParams, $route, $window) {
			console.log('ProductDetailCtrl...');
			$http.get('/admin/product/' + $routeParams.pid).success(function(data, status, headers, config) {
				$scope.newSold = null;
				$scope.newPrice = null;
				var product = data;
				if (null == product.startDate) {
					product.startDate = new Date();
				} else {
					product.startDate = new Date(product.startDate);
				}
				product.startDate.setSeconds(0);
				product.startDate.setMilliseconds(0);
				if (null == product.expiredDate) {
					product.expiredDate = new Date();
				} else {
					product.expiredDate = new Date(product.expiredDate);
				}
				product.expiredDate.setSeconds(0);
				product.expiredDate.setMilliseconds(0);
				$scope.product = product;
			}).error(function(data, status, headers, config) {
				console.log('request failed...');
			});

			$scope.go2Kdt = function(refId) {
				window.location.href = 'https://koudaitong.com/v2/showcase/goods/edit#id=' + refId;
			};
			$scope.addPrice = function() {
				$scope.alerts = [];
				if (!$scope.product.stock || !$scope.product.originalPrice) {
					$scope.formIncomplete();
				} else {
					if (null != $scope.newSold && $scope.newSold != 0 && null != $scope.newPrice
							&& $scope.newPrice != 0) {
						var priceObj = {
							sold : $scope.newSold,
							targetPrice : $scope.newPrice
						};
						if (null == $scope.product.prices) {
							$scope.product.prices = [];
						}
						$scope.product.prices.push(priceObj);
						$http.post('/admin/product/' + $routeParams.pid, $scope.product).success(
								function(data, status, headers, config) {
									var pid = data.productId;
									window.location.href = '#/product/' + pid;
								}).error(function(data, status, headers, config) {
							console.log('product update failed...');
							console.log(data.message);
						});
					}
				}
			};
			$scope.removePrice = function(priceId) {
				if (!$scope.product.stock || !$scope.product.originalPrice) {
					$scope.formIncomplete();
				} else {
					for (var i = $scope.product.prices.length - 1; i >= 0; i--) {
						if ($scope.product.prices[i].priceId == priceId) {
							$scope.product.prices.splice(i, 1);
						}
					}
					$http.post('/admin/product/' + $routeParams.pid, $scope.product).success(
							function(data, status, headers, config) {
								$route.reload();
							}).error(function(data, status, headers, config) {
						console.log('product update failed...');
						console.log(data.message);
					});
				}
			}
			$scope.republish = function() {
				$http.get('/admin/product/' + $routeParams.pid + '/republish').success(
						function(data, status, headers, config) {
							console.log(data);
							var product = data;
							$window.location.reload();
						});
			}
			$scope.submit = function() {
				$scope.alerts = [];
				console.log($scope.product);
				if (!$scope.product.name || !$scope.product.postFee || !$scope.product.stock
						|| !$scope.product.originalPrice || !$scope.product.startDate || !$scope.product.expiredDate) {
					$scope.formIncomplete();
				} else {
					$http.post('/admin/product/' + $routeParams.pid, $scope.product).success(
							function(data, status, headers, config) {
								window.location = '#';
							}).error(function(data, status, headers, config) {
						console.log('product update failed...');
					});
				}
			}
			$scope.formIncomplete = function() {
				$scope.alerts.push({
					type : 'danger',
					msg : '缺少必填项'
				});
			}
			$scope.closeAlert = function(index) {
				$scope.alerts.splice(index, 1);
			}

			$scope.homeImgUploader = {};
			$scope.homeImgUpload = function() {
				$scope.homeImgUploader.flow.upload();
			}
		})

.controller(
		'OrderDetailCtrl',
		function($scope, $http, $routeParams, $route, $window) {
			console.log('OrderDetailCtrl...');
			$scope.order = {};
			$scope.logisticAgents = [ {
				code : 'tiantian',
				name : '天天快递'
			}, {
				code : 'shunfeng',
				name : '顺丰快递'
			} ];
			$http.get('/admin/order/' + $routeParams.orderId).success(
					function(data, status, headers, config) {
						$scope.order = data;
						// console.log($scope.order);
						$scope.checkLogisticUrl = "http://m.kuaidi100.com/index_all.html?type=" + data.logisticAgent
								+ "&postid=" + data.logisticTrackingId + "&callbackurl=" + $window.location.protocol
								+ "//" + $window.location.host + window.location.pathname;
					}).error(function(data, status, headers, config) {
				console.log('request failed...');
			});
			$scope.deliverOrder = function(orderId) {
				$scope.alerts = [];
				if (!$scope.order.logisticTrackingId || !$scope.order.logisticAgent) {
					$scope.missLogistic();
				} else {
					$http.get('/admin/order/' + $routeParams.orderId + '/deliver', {
						params : {
							lti : $scope.order.logisticTrackingId,
							la : $scope.order.logisticAgent
						}
					}).success(function(data, status, headers, config) {
						$scope.order = data;
						console.log($scope.order);
						$route.reload();
					}).error(function(data, status, headers, config) {
						console.log('request failed...');
					});
				}
			}
			$scope.finishOrder = function(orderId) {
				$http.get('/admin/order/' + $routeParams.orderId + '/finish').success(
						function(data, status, headers, config) {
							$scope.order = data;
							console.log($scope.order);
							$route.reload();
						}).error(function(data, status, headers, config) {
					console.log('request failed...');
				});
			}
			$scope.closeOrder = function(orderId) {
				$http.get('/admin/order/' + $routeParams.orderId + '/close').success(
						function(data, status, headers, config) {
							$scope.order = data;
							console.log($scope.order);
							$route.reload();
						}).error(function(data, status, headers, config) {
					console.log('request failed...');
				});
			}
			$scope.addComments = function(orderId) {
				$scope.alerts = [];
				if (!$scope.order.comments) {
					$scope.missComments();
				} else {
					$http.get('/admin/order/' + $routeParams.orderId + '/comments', {
						params : {
							comments : $scope.order.comments,
							adminComments : $scope.order.adminComments
						}
					}).success(function(data, status, headers, config) {
						$scope.order = data;
						console.log($scope.order);
						$route.reload();
					}).error(function(data, status, headers, config) {
						console.log('request failed...');
					});
				}
			}
			$scope.missLogistic = function() {
				$scope.alerts.push({
					type : 'danger',
					msg : '请填写快递相关信息'
				});
			}
			$scope.missComments = function() {
				$scope.alerts.push({
					type : 'danger',
					msg : '请填写备注信息'
				});
			}
			$scope.closeAlert = function(index) {
				$scope.alerts.splice(index, 1);
			}
		}).controller('SignInCtrl', function($scope, $rootScope, $http, $window, $timeout) {
	$scope.login = {
		'mobile' : null,
		'password' : null
	};
	$scope.submit = function() {
		$scope.alerts = [];
		if (!$scope.login.mobile || !$scope.login.password) {
			$scope.alerts.push({
				type : 'danger',
				msg : '请输入完整信息'
			});
			return;
		}

		$http.post('/login', $scope.login).success(function(data, status, headers, config) {
			if (data == null || data.length == 0 || data.isAdmin != 'Y') {
				$scope.loginFail();
			} else {
				$scope.alerts.push({
					type : 'success',
					msg : '登录成功'
				});
				window.location.href = "#";
			}
		}).error(function(data, status, headers, config) {
			$scope.loginFail();
		});
	};
	$scope.loginFail = function() {
		$scope.alerts.push({
			type : 'danger',
			msg : '登录失败。密码错误或没有管理员权限'
		});
	}
	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	}
});
