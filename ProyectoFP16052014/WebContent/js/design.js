(function() {
    var
        fullScreenApi = {
            supportsFullScreen: false,
            isFullScreen: function() { return false; },
            requestFullScreen: function() {},
            cancelFullScreen: function() {},
            fullScreenEventName: '',
            prefix: ''
        },
        browserPrefixes = 'webkit moz o ms khtml'.split(' ');
 
    // check for native support
    if (typeof document.cancelFullScreen != 'undefined') {
        fullScreenApi.supportsFullScreen = true;
    } else {
        // check for fullscreen support by vendor prefix
        for (var i = 0, il = browserPrefixes.length; i < il; i++ ) {
            fullScreenApi.prefix = browserPrefixes[i];
 
            if (typeof document[fullScreenApi.prefix + 'CancelFullScreen' ] != 'undefined' ) {
                fullScreenApi.supportsFullScreen = true;
 
                break;
            }
        }
    }
 
    // update methods to do something useful
    if (fullScreenApi.supportsFullScreen) {
        fullScreenApi.fullScreenEventName = fullScreenApi.prefix + 'fullscreenchange';
 
        fullScreenApi.isFullScreen = function() {
            switch (this.prefix) {
                case '':
                    return document.fullScreen;
                case 'webkit':
                    return document.webkitIsFullScreen;
                default:
                    return document[this.prefix + 'FullScreen'];
            }
        }
        fullScreenApi.requestFullScreen = function(el) {
            return (this.prefix === '') ? el.requestFullScreen() : el[this.prefix + 'RequestFullScreen']();
        }
        fullScreenApi.cancelFullScreen = function(el) {
            return (this.prefix === '') ? document.cancelFullScreen() : document[this.prefix + 'CancelFullScreen']();
        }
    }
 
    // jQuery plugin
    if (typeof jQuery != 'undefined') {
        jQuery.fn.requestFullScreen = function() {
 
            return this.each(function() {
                if (fullScreenApi.supportsFullScreen) {
                    fullScreenApi.requestFullScreen(this);
                }
            });
        };
    }
 
    // export api
    window.fullScreenApi = fullScreenApi;
})();

$(function(){
	
	ajusteVertical();
	cambiarCabecera();
	$(window).resize(ajusteVertical);
	
	if (fullScreenApi.supportsFullScreen) {
	    document.getElementById("fullscreen").addEventListener('click', function() {
	        fullScreenApi.requestFullScreen(document.getElementsByTagName("html")[0]);
	    }, true);
	}
	
});
function ajusteVertical(){
	//Para cambiar la cabecera
	cambiarCabecera();
	centradoVertical(".alineadoVertical");
}
function cambiarCabecera(){
		var max = 580;
		var w = parseInt($(window).width());
		if (w<max){
			$("header h1").hide();
			$("header .im").show();
		}else{
			$("header h1").show();
			$("header .im").hide();
		}
		$("input").val(w);
}
function centradoVertical(div){
	var h = parseInt($(window).height());
	var h2 = parseInt($(div).height());
	var margen = ((h-h2)/2)-h2/6;
	if (margen <4){
		margen = 4;
	}
	$(div).css("margin-top",margen+"px");
}


//Api para fullscreen