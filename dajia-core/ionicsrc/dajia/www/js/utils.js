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
	}
};