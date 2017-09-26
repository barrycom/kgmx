$(window).on('scroll', function(){

          // Topbar
	var $this = $(this);
	var targetTop = $(this).scrollTop();
	
	if (targetTop >= 50){			
		$("#navi").css({
			"top": targetTop
		});
	}else{
		$("#navi").css({
			"position": "absolute",
			"top": "50px"
		});
	}
});