var starter = angular.module('starter.services', [ 'http-auth-interceptor' ]);

starter.factory('AuthService', function($rootScope, $http, $cookies, authService) {
	var service = {
		signup : function(signup) {
			$http.post('/signup', signup).success(function(data, status, headers, config) {
				$cookies.put('dajia_user', data['mobile'], {
					path : '/'
				});
				$cookies.put('dajia_username', data['userName'], {
					path : '/'
				});
				$rootScope.$broadcast('event:auth-signup-success', status);
				authService.loginConfirmed();
			}).error(function(data, status, headers, config) {
				$rootScope.$broadcast('event:auth-signup-failed', status);
			});
		},
		login : function(login) {
			$http.post('/login', login).success(function(data, status, headers, config) {
				console.log(data);
				// $http.defaults.headers.common.Authorization = data.authToken;
				$cookies.put('dajia_user', data['mobile'], {
					path : '/'
				});
				$cookies.put('dajia_username', data['userName'], {
					path : '/'
				});
				authService.loginConfirmed();
			}).error(function(data, status, headers, config) {
				$rootScope.$broadcast('event:auth-login-failed', status);
			});
		},
		logout : function(user) {
			$http.post('/user/logout', {}).success(function(data, status) {
				$rootScope.$broadcast('event:auth-logout-complete');
			});
		},
		loginCancelled : function() {
			authService.loginCancelled();
		}
	};
	return service;
});

starter.factory('Mocks', function() {
	// Might use a resource here that returns a JSON array

	// Some fake testing data
	var products = [ {
		pid : 1,
		name : '迪奥魅惑唇膏玩色狂想系列',
		desc : '一款全新风格，一个颠覆性时尚解码：全新Dior迪奥魅惑唇膏玩色狂想系列，采用晶炫酷黑包装，尽显谜漾深邃、晶透纯粹及闪耀光芒。',
		img : './img/dajia-sample-1.jpg',
		vendorImg : './img/dajia-company-1.jpg',
		oriPrice : 480.00,
		price : 247.00,
		priceOff : 232.00,
		targetPrice : 150.00,
		orderNum : 65,
		targetOrderNum : 100,
		long_desc : '...',
		spec : '...'
	}, {
		pid : 2,
		name : '倩碧润肤乳-啫喱配方',
		desc : '皮肤科医生研发无油保湿配方，与肌肤自然滋润成分如出一辙。',
		img : './img/dajia-sample-2.jpg',
		vendorImg : './img/dajia-company-2.jpg',
		oriPrice : 295,
		price : 205,
		priceOff : 90,
		targetPrice : 120,
		orderNum : 28,
		targetOrderNum : 100,
		long_desc : '...',
		spec : '...'
	}, {
		pid : 3,
		name : '香奈儿邂逅活力淡香水50ml',
		desc : '这款全新的活力淡香水给人以动力，犹如幸运之神赐予的强大能量，不是乍现的灵光，而是无尽活力的源泉，怡人的葡萄柚-血橙复合香调跟随脉搏一起跳动，激发无穷的蓬勃的活力。',
		img : './img/dajia-sample-3.jpg',
		vendorImg : './img/dajia-company-3.jpg',
		oriPrice : 550,
		price : 482,
		priceOff : 68,
		targetPrice : 240,
		orderNum : 19,
		targetOrderNum : 100,
		long_desc : '...',
		spec : '...'
	} ];

	var myOrders = [ {
		id : 1,
		pid : 1,
		number : 1,
		price : 281,
		orderDate : '2015/10/26',
		dueDate : '2015/11/05',
		status : '已付款',
		contactId : 1
	}, {
		id : 2,
		pid : 3,
		number : 1,
		price : 500,
		orderDate : '2015/10/29',
		dueDate : '2015/11/09',
		status : '已付款',
		contactId : 1
	} ];

	var contacts = [ {
		id : 1,
		name : '王大锤',
		cell : '13609999999',
		province : '上海',
		city : '上海市',
		district : '静安区',
		address1 : '南京西路1686号',
		address2 : '静安寺 大雄宝殿'
	} ];

	return {
		getProducts : function() {
			return products;
		},
		// remove: function(chat) {
		// chats.splice(chats.indexOf(chat), 1);
		// },
		getProduct : function(pid) {
			for (var i = 0; i < products.length; i++) {
				if (products[i].pid === parseInt(pid)) {
					return products[i];
				}
			}
			return null;
		},
		getMyOrders : function() {
			return myOrders;
		},
		getOrder : function(oid) {
			for (var i = 0; i < myOrders.length; i++) {
				if (myOrders[i].id === parseInt(oid)) {
					return myOrders[i];
				}
			}
			return null;
		},
		getContact : function(id) {
			for (var i = 0; i < contacts.length; i++) {
				if (contacts[i].id === parseInt(id)) {
					return contacts[i];
				}
			}
			return null;
		}
	};
});