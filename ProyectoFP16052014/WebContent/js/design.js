(function ($) {
    // Detect touch support
    $.support.touch = 'ontouchend' in document;
    // Ignore browsers without touch support
    if (!$.support.touch) {
    return;
    }
    var mouseProto = $.ui.mouse.prototype,
        _mouseInit = mouseProto._mouseInit,
        touchHandled;

    function simulateMouseEvent (event, simulatedType) { //use this function to simulate mouse event
    // Ignore multi-touch events
        if (event.originalEvent.touches.length > 1) {
        return;
        }
    event.preventDefault(); //use this to prevent scrolling during ui use

    var touch = event.originalEvent.changedTouches[0],
        simulatedEvent = document.createEvent('MouseEvents');
    // Initialize the simulated mouse event using the touch event's coordinates
    simulatedEvent.initMouseEvent(
        simulatedType,    // type
        true,             // bubbles                    
        true,             // cancelable                 
        window,           // view                       
        1,                // detail                     
        touch.screenX,    // screenX                    
        touch.screenY,    // screenY                    
        touch.clientX,    // clientX                    
        touch.clientY,    // clientY                    
        false,            // ctrlKey                    
        false,            // altKey                     
        false,            // shiftKey                   
        false,            // metaKey                    
        0,                // button                     
        null              // relatedTarget              
        );

    // Dispatch the simulated event to the target element
    event.target.dispatchEvent(simulatedEvent);
    }
    mouseProto._touchStart = function (event) {
    var self = this;
    // Ignore the event if another widget is already being handled
    if (touchHandled || !self._mouseCapture(event.originalEvent.changedTouches[0])) {
        return;
        }
    // Set the flag to prevent other widgets from inheriting the touch event
    touchHandled = true;
    // Track movement to determine if interaction was a click
    self._touchMoved = false;
    // Simulate the mouseover event
    simulateMouseEvent(event, 'mouseover');
    // Simulate the mousemove event
    simulateMouseEvent(event, 'mousemove');
    // Simulate the mousedown event
    simulateMouseEvent(event, 'mousedown');
    };

    mouseProto._touchMove = function (event) {
    // Ignore event if not handled
    if (!touchHandled) {
        return;
        }
    // Interaction was not a click
    this._touchMoved = true;
    // Simulate the mousemove event
    simulateMouseEvent(event, 'mousemove');
    };
    mouseProto._touchEnd = function (event) {
    // Ignore event if not handled
    if (!touchHandled) {
        return;
    }
    // Simulate the mouseup event
    simulateMouseEvent(event, 'mouseup');
    // Simulate the mouseout event
    simulateMouseEvent(event, 'mouseout');
    // If the touch interaction did not move, it should trigger a click
    if (!this._touchMoved) {
      // Simulate the click event
      simulateMouseEvent(event, 'click');
    }
    // Unset the flag to allow other widgets to inherit the touch event
    touchHandled = false;
    };
    mouseProto._mouseInit = function () {
    var self = this;
    // Delegate the touch handlers to the widget's element
    self.element
        .on('touchstart', $.proxy(self, '_touchStart'))
        .on('touchmove', $.proxy(self, '_touchMove'))
        .on('touchend', $.proxy(self, '_touchEnd'));

    // Call the original $.ui.mouse init method
    _mouseInit.call(self);
    };
})(jQuery);
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
	arreglar();
	ajusteVertical();
	cambiarCabecera();
	
	$(window).resize(ajusteVertical);
	
	
	
	if (fullScreenApi.supportsFullScreen) {
	    document.getElementById("fullscreen").addEventListener('click', function() {
	        fullScreenApi.requestFullScreen(document.getElementsByTagName("html")[0]);
	    }, true);
	}
	var mostrado = false;
	/*
	$("#cambiarChat").click(function(){
			if (mostrado){
				chatOculto();
				mostrado = false;
			}else{
				chatVisible();
				mostrado = true;
			}
	});
	*/
	$("#cambiarChat").click(arreglar);
	$("#cambiarTablero").click(mostrarOcultarTablero);
	$("#cambiarChat2").click(mostrarOcultarChat);
	$("#cambiarControles").click(mostrarOcultarControles);
	$("#cambiarPartidas").click(mostrarOcultarPartidas);
	$("#tiempos").draggable({
		containment: $("#wrap")
		});
	$("#envolver").draggable({
		containment: $("#wrap"),
		handle: "#mover"
	    });
	$("#partidas").draggable({
		containment: $("#wrap"),
		handle: "#controlesPartida"
	    });
	$( "#wrapTablero" ).draggable({ containment: $("#wrap"),
		handle: "#moverTablero",
		});
	$( "#wrapmivideo" ).draggable({ containment: $("#wrap"),
		handle: "#arrastrarmivideo",
		});
	$( "#wrapsuvideo" ).draggable({ containment: $("#wrap"),
		handle: "#arrastrarsuvideo",
		});
	$("#wrapmivideo").resizable({
		 
	      handles: 'se'
	    });
	$("#wrapsuvideo").resizable({
		
	      handles: 'se'
	    });
	$( "#envolver").resizable({
	      minHeight: 250,
	      minWidth: 300,
	      handles: 'se'
	    });
	$("#partidas" ).resizable({
	      minHeight: 130,
	      minWidth: 400,
	      handles: 'se'
	    });
	
	$("#wrapTablero").resizable({
      aspectRatio: true,
      minHeight: 250,
      minWidth: 250,
      handles: 'se'
    });
	$("#envolver").resize(ajustarChat);
	$("#partidas").resize(ajustarPartidas);
	$("#click").click(animacion1);
	$("#wrapsuvideo").resize(ajustarvideo);
	$( "#wrapmivideo").resize(ajustarvideo);
});
function ajustarvideo(){
	//console.log($(this).width()+" - "+$(this).height());
	$(this).find("video").width($(this).width());
	$(this).find("video").height($(this).height());
}
function animacion1(){
	$("#animacion1").animate({left:'0px'},"slow");
	$("#animacion1").animate({left:'40px'},"slow");
	$("#animacion1").animate({left:'40px',top:'500px'},"slow");
	$("#animacion1").animate({left:'140px',top:'300px'},"slow");
	$("#animacion1").animate({rigth:'440px',width:'200px',height:'100px'},"slow");
	$("#animacion1").animate({top:'400px',width:'100px',height:'100px',background:'red'},"slow");
	//$("#animacion1").fade();
}
function ajustarPartidas(){
	$("#tablaPartidas").css("height",parseInt($(this).height()-65));
}
function ajustarChat(){
	var h = parseInt($(this).height());
	console.log(h);
	$("#chat").css("height",h-50);
	$("#chat div").css("height",h-100);
}
function ajustarPaneles(){
	var h = parseInt($("#envolver").height());
	console.log(h);
	$("#chat").css("height",h-50);
	$("#chat div").css("height",h-100);
}
function arreglar(){
	var container = $('#contenido');
	// initialize
	container.masonry({
	  columnWidth: 50,
	  itemSelector: '.item'
	});
	
}
function mostrarOcultarTablero(){
	$("#wrapTablero").toggle();
}
function mostrarOcultarControles(){
	$("#tiempos").toggle();
}
function mostrarOcultarChat(){
	$("#envolver").toggle();
}
function mostrarOcultarPartidas(){
	$("#partidas").toggle();
}
function ajusteVertical(){
	//$("#contenido").height(h-90);
	//Para cambiar la cabecera
	cambiarCabecera();
	centradoVertical(".alineadoVertical");
	var h = $(document).height();
	$("#wrap").css("height",h);
	//console.log("altura:"+h);
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
