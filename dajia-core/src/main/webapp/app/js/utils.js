var DajiaGlobal = DajiaGlobal || {};

DajiaGlobal.utils = {
	isNotString : function(str) {
		return (typeof str !== "string");
	},
	mobileReg : /^((13[0-9]|15[0-9]|18[0-9])+\d{8})$/,
	getURLParameter : function(name) {
		return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [ ,
				"" ])[1].replace(/\+/g, '%20'))
				|| null;
	},
	getCountdown : function(countdown, targetDate) {
		var hours, minutes, seconds;
		var now = new Date().getTime();
		var seconds = (targetDate - now) / 1000;
		hours = DajiaGlobal.utils.pad(parseInt(seconds / 3600));
		if (hours > 99) {
			hours = 99;
		}
		seconds = seconds % 3600;
		minutes = DajiaGlobal.utils.pad(parseInt(seconds / 60));
		seconds = DajiaGlobal.utils.pad(parseInt(seconds % 60));

		countdown.innerHTML = "<span>" + hours + "</span><span>" + minutes + "</span><span>" + seconds + "</span>";
	},
	pad : function(n) {
		return (n < 10 ? '0' : '') + n;
	}
};