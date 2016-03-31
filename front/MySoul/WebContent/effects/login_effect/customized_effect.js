function loginSuccess(wleft, wtop) {
	
	var w = jq('$loginWin'), b = jq('$initBtn');
	//Minimize from the login Window
	jq('<div class="minimize" />').appendTo("body").css({
		left : wleft,
		top : wtop,
		width : w.width(),
		height : w.height()
	});
	//Minimize to the init Button
	p = b.offset();
	jq('.minimize').animate({
		left : p.left + b.width() / 2,
		top : p.top + b.height() / 2,
		width : 0,
		height : 0
	}, function() {
		jq(this).remove();
	});
}
function loginFailed() { //Shake the window
	var originalLeft = jq("$loginWin").position().left;
	var loginWin = jq("$loginWin");
	var previousBackgroundColor = loginWin.css('background-color');

	loginWin.animate({left : originalLeft - 25, backgroundColor : "red"}, 50)
		.animate({left : originalLeft}, 50)
		.animate({left : originalLeft + 25}, 50)
		.animate({left : originalLeft}, 50)
		.animate({backgroundColor : previousBackgroundColor}, 250, 
			function() {
				loginWin.css('background-color', '');
			} );
}