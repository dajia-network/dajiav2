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
	getCountDown : function(countdown, targetDate) {
		var days, hours, minutes, seconds;
		// find the amount of "seconds" between now and target
		var currentDate = new Date().getTime();
		var secondsLeft = (targetDate - currentDate) / 1000;

		days = this.pad(parseInt(secondsLeft / 86400));
		secondsLeft = secondsLeft % 86400;

		hours = this.pad(parseInt(secondsLeft / 3600));
		secondsLeft = secondsLeft % 3600;

		minutes = this.pad(parseInt(secondsLeft / 60));
		seconds = this.pad(parseInt(secondsLeft % 60));

		// format countdown string + set tag value
		countdown.innerHTML = "<span>" + days + "</span><span>" + hours + "</span><span>" + minutes + "</span><span>"
				+ seconds + "</span>";
	},
	pad : function(n) {
		return (n < 10 ? '0' : '') + n;
	},
	startCountDown : function(tid, targetDate, intervals) {
		var countdown = document.getElementById(tid);
		// console.log(countdown);
		this.getCountDown(countdown, targetDate);
		var intv = setInterval(function() {
			DajiaGlobal.utils.getCountDown(countdown, targetDate);
		}, 1000);
		intervals.push(intv);
	}
};