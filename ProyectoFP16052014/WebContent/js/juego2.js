$(function(){
	
	/*** Atributos ***/
	var divd = $("#tablero");
	//Variables para recoger coordenadas
	var coordenadaInicial = null;
	var coordenadaFinal = null;
	/** id para obtener el nombre del usuario */
	
	var nombre;
	
	/** la conexion websocket */
	var ws = null;
	/** donde pondremos los mensajes generales */
	var idChat ="#p-00"; //Pestaña del chat general
	/** Enviar mensaje texto */
	var idBotonEnviar ="#enviar";
	var idInputMensaje = "#mensaje";
	var timePing = null;
	
	/*** Conf inicial ***/
	ajustar(divd);
	dibujarTablero(divd);
	pintarCasillas(divd);
	colocarFichas(divd);
	conectar();
	
	/** indicarle al chat que tiene pestaÃ±as */
	$("#chat").tabs();
	
	
	/*** Eventos ***/
	$(window).resize(ajuste);
	//Evento para recoger coordenadas 
	$("#tablero td").click(recogerCoordenada);
	
	/** Cuando presionamos el boton de conectar
		ejecutamos la conexion */
	
	
	
	$(idBotonEnviar).click(function(){
		var str = $("div[aria-expanded='true']").eq(0).attr("id").substr(2);
		if (str == "00")
			ws.send(deTextoAJson($(idInputMensaje).val()));
		else{
			ws.send(deTextoAJsonPara($(idInputMensaje).val(),str));
		}
		$(idInputMensaje).val("");
	});
	
	
	
	
	
	/** Usuario pulsa sobre un nombre-boton del chat
	para abrir una ventana **/
	$("button[title='wispear']").click(asignarEscuchadorBoton);
	
	$(idInputMensaje).keyup(function (e) {
		if (e.which == 13) {
			$('#enviar').trigger('click');
		}
	});
	

	
	$("#resetearPartida").click(function(){
		console.log("resetearPartida");
		ws.send(resetearPartidaJSON());
	});
		
	$("#crearPartida").click(function(){
		console.log("crearPartida");
		ws.send(crearPartidaJSON());
	});
	
	$("#rotar").click(rotarTablero);
		
		
	
	
	function decode_base64(s) {
	    var e={},i,k,v=[],r='',w=String.fromCharCode;
	    var n=[[65,91],[97,123],[48,58],[43,44],[47,48]];

	    for(z in n){for(i=n[z][0];i<n[z][1];i++){v.push(w(i));}}
	    for(i=0;i<64;i++){e[v[i]]=i;}

	    for(i=0;i<s.length;i+=72){
	    var b=0,c,x,l=0,o=s.substring(i,i+72);
	         for(x=0;x<o.length;x++){
	                c=e[o.charAt(x)];b=(b<<6)+c;l+=6;
	                while(l>=8){r+=w((b>>>(l-=8))%256);}
	         }
	    }
	    return r;
	    }
		
		/** Funciones Sonidos **/
	function soundNewGame(){
		$("#newgame")[0].play();
	}
	function soundMove(){
		$("#move")[0].play();
	}
	function soundTick(){
		$("#tick")[0].play();
	}
	/*** Funciones ***/
	function rotarTablero(){
		
		$(divd).find("tbody").each(function(e,i){
			
			$(this).find("tr").each(function(e,i){
				var array = $.makeArray($("td",this).detach());
				array.reverse();
				$(this).html(array);
			});
			var arr=$.makeArray($("tr",this).detach());
			arr.reverse();
			$(this).html(arr);
		});
	}
	function refrescarEscucharUnirse(){
		$(".unirse").click(function(){
			var blancas = $(this).attr("b");
			var negras = $(this).attr("n");
			var idPartida ="";
			if (blancas == ""){
				idPartida = negras;
			}else{
				idPartida = blancas;
			}
			ws.send(unirsePartidaJSON(idPartida));
			
		});
	}
	
	function conectar(){
		// Comprobamos que hay un nombre escrito
		
		//Nos conectamos al servidor
		var URL = 'ws://' + location.host  + '/ProyectoFP16052014/ajedrez';
		//var URL = 'ws://192.168.56.1:8080/websocket/simple?nombre='+nombre;
		//var URL = 'ws://localhost:8080/websocket/simple?nombre='+nombre;
		var timePing;
		if ('WebSocket' in window) {
		        ws = new WebSocket(URL);
		    } else if ('MozWebSocket' in window) {
		        ws = new MozWebSocket(URL);
		    } else {
		        alert('Tu navegador no soporta WebSockets');
		        return;
		    }
		    ws.onopen = function (event) {
		      
		        $(idChat).append("Conectado");
		       // toggleElementosWebsocket();
		        //ocultarConectarse();
		       
		        //enviarPings();
		    };
		    ws.onmessage = function (event) {
		       procesarMensaje(event.data);
		      
		     
		    };
		    ws.onclose = function (event) {
		       alert("No se ha podido conectar a Websockets.");
		       // $(idChat).append("Cerrado");
		       window.location.replace('http://' + location.host  + '/ProyectoFP16052014/principal');
		    };
		    ws.onerror = function (event) {
		       //$(idChat).append("Error");
		        alert("Error en Websockets.");
		        window.location.replace('http://' + location.host  + '/ProyectoFP16052014/principal');
		    };
		   
		   
	}
	
	function websocketDisponible(){
		
		var correcto = false;
		if ('WebSocket' in window) {
	        correcto = true;
	    } 
		if ('MozWebSocket' in window) {
	    	correcto = true;
	    }
	   return correcto;
	}
	function toggleElementosWebsocket(){
		//$("#tablero").toggle();
		//("#enviar").toggle();
		//$("#mensaje").toggle();
		$("#resetearPartida").toggle();
	}
	function ocultarConectarse(){
		$("#conectar").hide();
		$("#nombre").hide();
	}
	function asignarEscuchadorBoton(){
		crearPestania($(this).text());
	}
	function crearPestania(identificador){
		var buscar = "#p-"+identificador;
		if( $(buscar).length == 0){
			var li = "<li><a href='#p-"+identificador+"'><span>"+identificador+"</span></a><button b='p-"+identificador+"' onclick='borrarPestania(this);'>X</button></li>";
			$("#chat ul").append(li);
			var di = "<div id='p-"+identificador+"'></div>";
			$("#chat").append(di);
			$("#chat").tabs("refresh");
			
		}
	}
	function borrarPestania(boton){
		var id = $(boton).attr("b");
		$("div#"+id+" , a[href='#"+id+"']").remove();
		$("#chat").tabs("refresh");
		
	}
	/** Convertir mensaje del INput en JSON string */
	function deTextoAJson (mensaje){
		var str = '{ "tipo": "mensajeGeneral", "contenido": "'+mensaje+'" , "de": "'+nombre+'"}';
		return str;
	}
	function deTextoAJsonPara (mensaje,destino){
		var str = '{ "tipo": "mensajeUsuario", "contenido": "'+mensaje+'" , "de": "'+nombre+'" , "a":"'+destino+'"}';
		return str;
	}
	function movimientoJSON(inicio,fin){
		var str = '{ "tipo": "movimiento","inicio":"'+inicio+'", "fin":"'+fin+'"}';
		return str;
	}
	function resetearPartidaJSON (){
		var str = '{ "tipo": "resetearPartida"}';
		return str;
	}
	function crearPartidaJSON (){
		var str = '{ "tipo": "crearPartida"}';
		return str;
	}
	function enviarPing (){
		var str = '{ "tipo": "ping"}';
		return str;
	}
	function unirsePartidaJSON(idPartida){
		var str = '{ "tipo":"unirsePartida" , "idPartida":"'+idPartida+'"}';
		console.log(str);
		return str;
	}
	
	function procesarMensaje(mensaje){
		try{
			var json =  JSON.parse(mensaje);
		
		
		
		//console.log(json);
		switch (json.tipo) {
			case "mensajeGeneral": //Se recibe un mensaje para el chat general
				var claseBotonGeneral = "";
				 $(idChat).append("<p><button class='"+claseBotonGeneral+"' title='"+json.de+"'>"+json.de+"</button>: "+json.contenido+"</p>");
				 $(idChat).scrollTop($(idChat)[0].scrollHeight);
				 $("button[title='"+json.de+"']").click(asignarEscuchadorBoton);
				 $(idChat).scrollTop($(idChat).prop('scrollHeight'));
				 break;
			case "mensajeCorrectoChat": //Mensaje a un usuario en concreto.
				 $(idChat).append("<p><span style='color:blue;'>'"+json.contenido+"'</span></p>");
				 $(idChat).scrollTop($(idChat).prop('scrollHeight'));
				break;
			case "mensajeUsuario":
				crearPestania(json.de);
				$("#p-"+json.de).append("<p>"+json.de+": "+json.contenido+"</span></p>");
				$("#p-"+json.de).scrollTop($("#p-"+json.de).prop('scrollHeight'));
				break;
			case "mensajeReplica":
				crearPestania(json.a);
				 $("#p-"+json.a).append("<p>"+json.de+": "+json.contenido+"</span></p>");
				 $("#p-"+json.a).scrollTop($("#p-"+json.a).prop('scrollHeight'));
				break;
			case "movimiento":
				moverFicha(json.inicio,json.fin);
				soundMove();	
				break;
			case "resetearPartida":
					pintarCasillas(divd);
					colocarFichas(divd);
					soundNewGame();
				break;
			case "partidasDisponibles":
				var str="";
				for (var int = 0; int < json.partidas.length; int++) {
					str += "<tr><td>"+json.partidas[int].blancas+"</td><td>"+json.partidas[int].negras+"</td><td>"+json.partidas[int].estado+"</td><td>"+json.partidas[int].fase+"</td>";
					if (json.partidas[int].estado == "EsperandoOtroJugador"){
						str += "<td><button class='unirse' b='"+json.partidas[int].blancas+"' n='"+json.partidas[int].negras+"'>Unirse</button></td></tr>";
					}else{
						str += "<td></td></tr>";
					}
				}
				$("#listaPartidas").html(str);
				refrescarEscucharUnirse();
				break;
			case "tiempo":
				$("#tiempoBlancas").html(json.tiempoBlancas);
				$("#tiempoNegras").html(json.tiempoNegras);
				
			break;
			case "rotarTablero":
				rotarTablero();
				
			break;
			case "elegirFicha":
				//BoxJquery
				
				bootbox.dialog({
					message: "Elige la ficha que mas te interese:",
					title: "Tu peón ha coronado",
					buttons: {
						success2: {
							label: "<img src='img/piezas/bt.svg' />!",
							className: "btn-success",
							callback: function() {
								ws.send('{ "tipo": "movimiento","inicio":"a1", "fin":"a1" , "ficha":"torre"}');
								
							}
						},
						success: {
							label: "<img src='img/piezas/bc.svg' />!",
							className: "btn-success",
							callback: function() {
								ws.send('{ "tipo": "movimiento","inicio":"a1", "fin":"a1" , "ficha":"caballo"}');
							}
						},
						danger: {
							label: "<img src='img/piezas/ba.svg' />!",
							className: "btn-success",
							callback: function() {
								ws.send('{ "tipo": "movimiento","inicio":"a1", "fin":"a1" , "ficha":"alfil"}');
							}
						},
						main: {
							label: "<img src='img/piezas/bd.svg' />!",
							className: "btn-success",
							callback: function() {
								ws.send('{ "tipo": "movimiento","inicio":"a1", "fin":"a1" , "ficha":"dama"}');
							}
						}
					}
					});
				;
			break;
			case "crearFicha":
					var posicion = json.posicion;
					var color = json.color;
					var ficha = json.ficha;
					crearFicha(ficha,color,posicion);
				break;
			case "media":
				var target = document.getElementById("algo2");
				
					url = window.URL.createObjectURL(json.video);
					target.onload = function() {
	            		 window.URL.revokeObjectURL(url);
		            };
				
				
	            
	            target.src = url;
				break;
			default:
				
				break;
		}
		}catch(err){
			var target = document.getElementById("target");
			var sonido = document.getElementById("sonido");
			if (mensaje.size == 32812 || mensaje.size == 41004){
	        	 url = window.URL.createObjectURL(mensaje);
	        	sonido.onload= function() {
	        		window.URL.revokeObjectURL(url);
	            };
	        	sonido.src = url;
	        	sonido.play();
	        }else{
	        	 url = window.URL.createObjectURL(mensaje);
		        	target.onload = function() {
		        		 window.URL.revokeObjectURL(url);
		            };
		            target.src = url;
	        }
	        
	      
	       
			
			
           
		}
	}
	function ajuste(ev){
		ajustar(divd);
		dibujarTablero(divd);
	}
	
	function ajustar(div){
			//var w = parseInt($(window).width()*.9);
			//var h = parseInt($(window).height()*.9);
			var h2 = parseInt($(window).height()*.9);
			var w = parseInt($(div).parent().width()*.9);
			//var h = w;
			//console.log("H:"+h+" W:"+w);
			
			if (w>h2){
			//if(w>h){
				$(div).css("width",""+h2+"px");
				$(div).css("height",""+h2+"px");
			}else{
				$(div).css("width",""+w+"px");
				$(div).css("height",""+w+"px");
			}
	}
	
	function dibujarTablero(lugar){
		var ancho = (parseFloat($(lugar).css("width")) /8);
		//console.log(ancho);
		$(lugar).find("td").css("width",""+ancho+"");
		$(lugar).find("td").css("height",""+ancho+"");
	}
	
	function pintarCasillas(lugar){
		var con= 0;
		$(lugar).find("td").each(function(index){
			if (index==8 ||
				index==16 ||
				index==24 ||
				index==32 ||
				index==40 ||
				index==48 ||
				index==56 ){
			con++;
			}
			if (con%2==0){
				$(this).attr("class","b");
			}else{
				$(this).attr("class","n");
			}
			con++;
		});
	}
	function colocarFichas(lugar){
		var num = ["1","2","3","4","5","6","7","8"];
		var ar = ["a","b","c","d","e","f","g","h"];
		var pi = ["t","c","a","d","r","a","c","t"];
		for (var i=0; i<8;i++){
			for (var j=0;j<8;j++){
				if (i==1){
					$(lugar).find($("#"+ar[j]+num[i])).addClass("bp");
				}
				if (i==6){
					$(lugar).find($("#"+ar[j]+num[i])).addClass("np");
				}
				var color ;
				if (i==0){
					color ="b";
				}else{
					color ="n";
				}
				
				if (i == 0 || i == 7){
					$(lugar).find($("#"+ar[j]+num[i])).addClass(color+pi[j]);
				}
				
			}
		}
	}
	function pintarCasilla(tablero,coordenada,color){
		$(tablero).find("#"+coordenada).addClass(color);
	}
	function despintarCasilla(tablero,coordenada,color){
		$(tablero).find("#"+coordenada).removeClass(color);
	}
	function recogerCoordenadas(){
		coordenadaInicial= null;
		coordenadaFinal = null;
		
	}
	
	
	function recogerCoordenada (){	//Funcion evento
		
		if (coordenadaInicial == null){
			pintarCasilla(divd,$(this).attr("id"),"azul");
			coordenadaInicial = $(this).attr("id");
		}else{
			if (coordenadaInicial != null && coordenadaFinal == null){
				if(coordenadaInicial == $(this).attr("id")){
					coordenadaInicial = null;
					despintarCasilla(divd,$(this).attr("id"),"azul");
				}else{
					pintarCasilla(divd,$(this).attr("id"),"azul");
					coordenadaFinal = $(this).attr("id");
				}
				
			}
		}
		if (coordenadaInicial != null && coordenadaFinal != null){
			//Función para enviar coordenadas
			/*Tendria que enviar coordenadas
			 * al servidor y luego este me enviaria
			 * si es aceptable o no.
			 * de momento todo es aceptable
			 * y paso directamente a mover ficha
			 */
			$("#"+coordenadaInicial).toggleClass("azul");
			$("#"+coordenadaFinal).toggleClass("azul");
			enviarCoordenadas();
			coordenadaInicial = null;
			coordenadaFinal = null;
		}
	}
	function enviarCoordenadas(){
		ws.send(movimientoJSON(coordenadaInicial,coordenadaFinal));
		//moverFicha(coordenadaInicial,coordenadaFinal);
	}
	function moverFicha(inicio, fin){
		//alert(inicio+"-"+fin);
		var ficha = obtenerFicha(inicio);
		$("#"+inicio).toggleClass(ficha);
		$("#"+fin).toggleClass(obtenerFicha(fin));
		$("#"+fin).toggleClass(ficha);
		
	}
	function obtenerFicha(posicion){
		var str = $("#"+posicion).attr('class').split(" ");
		var enviar ="";
		for (var i= 0; i<str.length;i++){
			if (str[i].length == 2){
				enviar = str[i];
			}
		}
		return enviar;
	}
	function crearFicha(ficha,color,posicion){
		var str ="";
		if (color == "blancas"){
			str+="b";
		}else{
			str+="n";
		}
		if (ficha =="torre") str+="t";
		if (ficha =="caballo") str+="c";
		if (ficha=="alfil") str +="a";
		if (ficha == "dama") str +="d";
		$("#"+posicion).toggleClass(obtenerFicha(posicion));
		$("#"+posicion).toggleClass(str);
	}
});
