$(function(){
	/**
	 * Para los cursores
	 * http://jsfiddle.net/wNKcU/5/
	 */
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
	var inicial = true;
	var ho ;
	var width2;
	var height2;
	var estado="";
	var ladoBlancas=true;
	var estaEnBlancas = true;
	var seleccionadoObjecto = false;
	/*** Conf inicial ***/
	ajustar(divd);
	dibujarTablero(divd);
	pintarCasillas(divd);
	colocarFichas(divd);
	conectar();
	mostrarControlesIdle();
	//cerrarBtnconex();
	/** indicarle al chat que tiene pestañas */
	$("#chat").tabs();
	
	ajustarPaneles();
	/*** Eventos ***/
	$(window).resize(ajuste);
	//Evento para recoger coordenadas 
	//$("#tablero td").click(recogerCoordenada);
	$("#objetos li").click(activarFigura);
	
	$("#tablero td").click(accionTablero);
	$("#cancelarSeleccion").click(cancelarSeleccion);
	$("#borrarTodo").click(quitarTodasLasFichas);
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
	$("#pedirAbandonar").click(enviarAbandono);
	$("#pedirTablas").click(enviarTablas);
	$("#pedirTablas").prop( "disabled", true );
	$("#salirSala").click(salirSala);
	$("#comunicarse").click(llamarAusuarios);
	//
	
	//Vendor prefixes
	navigator.getUserMedia =  
	navigator.getUserMedia ||
	navigator.webkitGetUserMedia ||
	navigator.mozGetUserMedia ;
				
	window.RTCPeerConnection = 	
	window.RTCPeerConnection ||
	window.mozRTCPeerConnection ||
	window.webkitRTCPeerConnection ;

	window.URL =
	window.mozURL ||
	window.URL ||
	window.webkitURL ;

	window.RTCSessionDescription =
	window.RTCSessionDescription ||
	window.mozRTCSessionDescription ||
	window.webkitRTCSessionDescription ;

	        //Opciones iniciales
	var DtlsSrtpKeyAgreement = { DtlsSrtpKeyAgreement: true };
	var optional = { optional: [DtlsSrtpKeyAgreement]};
	var iceServers = {
		   iceServers: [{url:'stun:stun01.sipphone.com'},
		                {url:'stun:stun.ekiga.net'},
		                {url:'stun:stun.fwdnet.net'},
		                {url:'stun:stun.ideasip.com'},
		                {url:'stun:stun.iptel.org'},
		                {url:'stun:stun.rixtelecom.se'},
		                {url:'stun:stun.schlund.de'},
		                {url:'stun:stun.l.google.com:19302'},
		                {url:'stun:stun1.l.google.com:19302'},
		                {url:'stun:stun2.l.google.com:19302'},
		                {url:'stun:stun3.l.google.com:19302'},
		                {url:'stun:stun4.l.google.com:19302'},
		                {url:'stun:stunserver.org'},
		                {url:'stun:stun.softjoys.com'},
		                {url:'stun:stun.voiparound.com'},
		                {url:'stun:stun.voipbuster.com'},
		                {url:'stun:stun.voipstunt.com'},
		                {url:'stun:stun.voxgratia.org'},
		                {url:'stun:stun.xten.com'},
		                {
		                    url: 'turn:numb.viagenie.ca',
		                    credential: 'muazkh',
		                    username: 'webrtc@live.com'
		                },
		                {
		                    url: 'turn:192.158.29.39:3478?transport=udp',
		                    credential: 'JZEOEt2V3Qb0y27GRntt2u2PAYA=',
		                    username: '28224511:1379330808'
		                },
		                {
		                    url: 'turn:192.158.29.39:3478?transport=tcp',
		                    credential: 'JZEOEt2V3Qb0y27GRntt2u2PAYA=',
		                    username: '28224511:1379330808'
		                }]
		         };

	//WEBRTC variables y eventos  
	//Variables
	var conexiones = {};
	var videos = {};
	var peer;
	var puedePedirTablas= false;
	var streamLocal;
	var streamToAttach;
	var videoLocal = document.getElementById("videoLocal");
	var videoRemoto = document.getElementById("videoRemoto");
	//Eventos           
	$("#enviarAtodos").click(enviarPeticiones);
	
	$("#crearOffer").click(crearOffer);
	$("#crearAnswer").click(crearAnswer);

	$("#handling").click(handling);
	$("#btnActivarMedia").click(activarMedia);
	$("#btnColgar").click(closeMedia);
	//FIN WEBRTC var. y eventos
	
	//FUNCTIONES
		//Functiones generales
	var ping;
	function enviarPeticiones(){
		//ws.send("{\"tipo\":\"mediaOn\"}");
		for (var key in conexiones){
			console.log(key+"->");
			console.log(conexiones[key]);
		}
	}
	function hacerPing(){
		if (ping==null){
			ping = setInterval(function(){
				ws.send("ping");
			},10000);
		}
	}
	function pararPing(){
		clearInterval(ping);
	}
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
	var cant=0;
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
		if (estaEnBlancas){
			estaEnBlancas=false;
		}else{
			estaEnBlancas=true;
		}
	}
	function refrescarEscucharUnirse(){
		$(".unirse").click(function(){
			var sala = $(this).attr("sala");
			ws.send(unirsePartidaJSON(sala));
			//alert("enviado"+unirsePartidaJSON(sala));
		});
	}
	
	function conectar(){
		// Comprobamos que hay un nombre escrito
		
		//Nos conectamos al servidor
		var parth = location.pathname.split("/");
		if (parth.length >2){
			var URL = 'ws://' + location.host  + '/ProyectoFP16052014/grupo';
		}else{
			var URL = 'ws://' + location.host  + '/grupo';
		}
		
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
		       
		        hacerPing();
		    };
		    ws.onmessage = function (event) {
		       procesarMensaje(event.data);
		      
		     
		    };
		    ws.onclose = function (event) {
		       alert("No se ha podido conectar a Websockets.");
		       // $(idChat).append("Cerrado");
		       var parth = location.pathname.split("/");
				if (parth.length >2){
					var srtf = 'http://' + location.host  + '/ProyectoFP16052014/principal';
				}else{
					var srtf = 'http://' + location.host  + '/principal';
				}
		       window.location.replace(srtf);
		       pararPing();
		    };
		    ws.onerror = function (event) {
		       //$(idChat).append("Error");
		        alert("Error en Websockets.");
		       
			       // $(idChat).append("Cerrado");
			       var parth = location.pathname.split("/");
					if (parth.length >2){
						var srtf = 'http://' + location.host  + '/ProyectoFP16052014/principal';
					}else{
						var srtf = 'http://' + location.host  + '/principal';
					}
			       window.location.replace(srtf);
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
		//alert("creando");
		crearPestania($(this).text());
	}
	function crearPestania(identificador){
		var buscar = "#p-"+identificador;
		if( $(buscar).length == 0){
			var li = "<li><a href='#p-"+identificador+"'><span>"+identificador+"</span></a><button class='cerrarPestania' b='p-"+identificador+"'>X</button></li>";
			$("#chat ul").append(li);
			var di = "<div id='p-"+identificador+"'></div>";
			$("#chat").append(di);
			$("#chat").tabs("refresh");
			$(".cerrarPestania").click(cerrarPestania);
			ajusteVertical();
			ajustarPaneles();
		}
	}
	function cerrarPestania(){
		var id= $(this).attr("b");
		$(this).parent().remove();
		$("div#"+id).remove();
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
	function enviarAbandono(){
		var str = '{ "tipo":"pedirAbandono"}';
		ws.send(str);
	}
	function enviarTablas(){
		var str = '{ "tipo":"pedirTablas"}';
		ws.send(str);
		$("#pedirTablas").prop( "disabled", true );
	}
	function salirSala(){
		var str = '{ "tipo":"salirSala" }';
		ws.send(str);
	}
	function procesarMensaje(mensaje){
		try{
			var json =  JSON.parse(mensaje);
			console.log(json);
			if (json.tipo)
			switch (json.tipo) {
			case "answer":
				//WebRTC
				//Nos traen una respuesta
				aceptarHandling(json.descripcion,json.de);
				break;
			case "offer":
				//WebRTC
				//Nos piden una respuesta.
				crearAnswer(json.descripcion, json.de);
				break;
			case "llamame":
				var usuario = json.usuario;
				conexiones[usuario] = "hola"+usuario;
				for(var prop in conexiones) {
				     alert(prop+"->"+conexiones[prop]);
				}
				break;
			case "ui" :
					alert(json.modelo);
					switch (json.modelo){
					case "normal":
						mostrarControlesNormal();
						break;
					case "idle":
						mostrarControlesIdle();
						break;
					case "admin":
						mostrarControlesAdmin();
						break;
						
					}
				break;
				case "listaUsuarios":
					//Webrtc
					/*
					 * Cogere esta lista y creare un array de conexiones con cada uno de ellos.
					 * 
					 */
					
					//Fin webrtc
					var str4 = "<ul>";
					for (var i=0; i<json.usuarios.length ; i++){
						str4+= "<li>"+json.usuarios[i].nombre+"</li>";
						//Webrtc
						/*
						if (!conexiones[json.usuarios[i].nombre]){
							videos[json.usuarios[i].nombre] = $(document.createElement('video'));
							crearOffer(json.usuarios[i].nombre);
						}
						*/
						if (!conexiones[json.usuarios[i].nombre]){
							$("#listaVideos").append("<video id='video"+json.usuarios[i].nombre+"' controls autoplay></video>");
							console.log("No esta "+json.usuarios[i].nombre+" en la lista.. añadiendo conexion sinpeer y video");
							conexiones[json.usuarios[i].nombre] = "sinpeer";
							
							//mostrar videos
							
						}else{
							console.log("YA esta "+json.usuarios[i].nombre+" en la lista!!");
						}
						
					}
					/*
					for(var prop in conexiones) {
					     alert(prop+"->"+conexiones[prop]);
					}
					*/
					str4+="</ul>";
					$("#listaUsuarios").html(str4);
				break;
				case "listaSalas":
					console.log(json);
					console.log(mensaje);
					var str1= "";
					for(var i=0; i<json.salas.length;i++){
						str1+= "<tr><td><span>"+json.salas[i].creador+"</span></td><td><button class='unirse' sala='"+json.salas[i].creador+"'>Unirse</button></td></tr>";
					}
					$("#listaPartidas").html(str1);
					refrescarEscucharUnirse();
				break;
				
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
					$("#pedirTablas").prop( "disabled", false );
					break;
				case "fichasTablero":
					reproducirTablero(json);
					break;
				case "efectosTablero":
					reproducirEfectos(json);
					break;
				case "resetearPartida":
						pintarCasillas(divd);
						colocarFichas(divd);
						soundNewGame();
						estado="jugando";
						activarModoPartida();
						//alert("JuegasdeBlancas?->"+ladoBlancas+"\nTableroConBlancas?->"+estaEnBlancas);
						if (!ladoBlancas && estaEnBlancas){
							rotarTablero();
						}
						if (ladoBlancas && !estaEnBlancas){
							rotarTablero();
						}
						ladoBlancas=true;
								
						
					break;
				case "partidasDisponibles":
					var str="";
					for (var int = 0; int < json.partidas.length; int++) {
						//alert(json.partidas[int].estado);
						var strB="";
						var strN="";
						if (json.partidas[int].blancas !=""){
							strB += "<button title='"+json.partidas[int].blancas+"'>"+json.partidas[int].blancas+"</button>";
						}
						if (json.partidas[int].negras !=""){
							strN += "<button title='"+json.partidas[int].negras+"'>"+json.partidas[int].negras+"</button>";
						}
						if (json.partidas[int].estado == "EsperandoOtroJugador"){
							
							str += "<tr><td>"+strB+"</td><td>"+strN+"</td><td>Esperando/Waiting</td><td></td>";
						
							str += "<td><button class='unirse' b='"+json.partidas[int].blancas+"' n='"+json.partidas[int].negras+"'>Unirse</button></td></tr>";
						}
						if (json.partidas[int].estado == "Jugando"){
							str += "<tr><td>"+strB+"</td><td>"+strN+"</td><td>Jugando/Playing</td><td></td>";
							str += "<td></td></tr>";
						}
						
					}
					
					$("#listaPartidas").html(str);
					$("#listaPartidas button[title]").unbind().click(asignarEscuchadorBoton);
					refrescarEscucharUnirse();
					break;
				case "tiempo":
					
					$("#tiempoBlancas").html(tiempoAhumano(json.tiempoBlancas));
					$("#tiempoNegras").html(tiempoAhumano(json.tiempoNegras));
					
				break;
				case "rotarTablero":
						
					
					ladoBlancas=false;
					
						
					
						
					
						
					
				
					
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
				case "crearFicha2":
						var posicion = json.posicion;
						var color = json.color;
						var ficha = json.ficha;
						crearFicha2(ficha,color,posicion);
					break;
				case "crearFicha":
					var posicion = json.posicion;
					var color = json.color;
					var ficha = json.ficha;
					crearFicha(ficha,color,posicion);
				break;
				case "finPartida":
						
						if (estado =="jugando"){
							
							ejecutar(json.accion);
						}
					break;
				case "aceptarTablas":
					bootbox.dialog(
							
							{
								 message: "¿Quieres aceptar las tablas?",
								 title: "Tu adversario pide tablas.",
						buttons: {
							success2: {
								label: "Aceptar tablas",
								className: "btn-success",
								callback: function() {
									ws.send('{ "tipo": "aceptaTablas"}');
									console.log("tablas aceptadas");
								}
							},
							success: {
								label: "Rechazar tablas",
								className: "btn-success",
								callback: function() {
									console.log("tablas rechazadas");
								}
							}
						}
						});
					break;
				default:
					
					break;
			}
			/*
			if (json.type && json.type == "offer"){
				//alert("Entro json.offer");;
				console.log("Nos estan llamando");
				crearAnswer(json);
				console.log("Le hemos contestado");
			}
			if (json.type && json.type == "answer"){
				console.log("Nos estan respondiendo");
				handling(json);
				console.log("Fin de la respuesta");
			}
			*/
		}catch(err){
			console.log(err);
		}
	}
	function reproducirTablero(json){
		quitarTodasLasFichas();
		for (var i =0; i<json.fichas.length;i++){
			$("#"+json.fichas[i].casilla).addClass(json.fichas[i].pieza);
		}
	}
	function reproducirEfectos(json){
		quitarTodasLosEfectos();
		for (var i=0; i<json.efectos.length;i++){
			$("#"+json.efectos[i].casilla).addClass(json.efectos[i].efecto);
		}
	}
	function mostrarControlesIdle(){
		$("#partidas").css("visibility","visible");
		
		$("#wrapTablero").css("visibility","hidden");
		$("#tiempos").css("visibility","hidden");
		$("#envolver").css("visibility","hidden");
		$("#wrapListaUsuarios").css("visibility","hidden");
		$("#wrapmivideo").css("visibility","hidden");
		$("#wrapsuvideo").css("visibility","hidden");
	}
	function mostrarControlesAdmin(){
		$("#partidas").css("visibility","hidden");
		
		$("#wrapTablero").css("visibility","visible");
		$("#tiempos").css("visibility","visible");
		$("#envolver").css("visibility","visible");
		$("#wrapListaUsuarios").css("visibility","visible");
		$("#wrapmivideo").css("visibility","visible");
		$("#wrapsuvideo").css("visibility","hidden");
	}
	function mostrarControlesNormal(){
		$("#partidas").css("visibility","hidden");
		
		$("#wrapTablero").css("visibility","visible");
		$("#tiempos").css("visibility","hidden");
		$("#envolver").css("visibility","visible");
		$("#wrapListaUsuarios").css("visibility","visible");
		$("#wrapmivideo").css("visibility","hidden");
		$("#wrapsuvideo").css("visibility","visible");
	}
	function activarModoPartida(){
		$("#tiempos").css("visibility","visible");
		$("#partidas").css("visibility","hidden");
	}
	function ejecutar(accion){
		//alert(accion);
		switch(accion){
		case "Tablas":
			bootbox.alert("Has hecho tablas");
			break;
		case "ganas":
			bootbox.alert("Has ganado a tu oponente");
			break;
		case "pierdes":
			bootbox.alert("Has perdido");
			break;
			
		}
		
		mostrarControlesIdle();
		estado="idle";
	}
	function ajuste(ev){
		ajustar(divd);
		dibujarTablero(divd);
		
		var w = $(window).width();
		//console.log("con-> " + w);
		var wcontrolesPartida = $("controlesPartida").width();
		if (w<wcontrolesPartida){
			$("controlesPartida").css("width",w);
			console.log("controlesPartida");
		}
		var wmover= $("mover").width();
		if (w<wmover){
			$("mover").css("width",w);
			console.log("controlesPartida2");
		}
		var wtiempos= $("tiempos").width();
		if (w<wtiempos){
			$("tiempos").css("width",w);
			console.log("controlesPartida3");
		}
		var wmoverTablero = $("moverTablero").width();
		if (w<wmoverTablero){
			$("moverTablero").css("width",w);
			console.log("controlesPartida4");
		}
	}
	
	function tiempoAhumano(segundos){
		
		var minutos = parseInt(parseInt(segundos)/60);
		var segundos = parseInt(parseInt(segundos)%60);
		segundos+="";
		minutos+="";
		while(segundos.length<2){
			segundos ="0"+segundos;
		}
		while(minutos.length<2){
			minutos ="0"+minutos;
		}
		return minutos+":"+segundos;
	}
	function ajustar(div){
			
			var w = parseInt($(div).parent().width());
			var h2 = parseInt($(div).parent().height());
			ho = parseInt($(window).height()-100);
			$("#contenido").css("height",""+ho+"px");
		if (inicial){
			width2 = parseInt($(window).width() *0.9);
			height2 = parseInt($(window).height()*0.9)-100;
			
			w = width2;
			h2 = height2;
			//console.log("inicial "+ho +"-"+wo);
			if (height2<width2){
				$("#wrapTablero").css("width",""+height2+"px");
				$("#wrapTablero").css("height",""+height2+"px");
				
			}else{
				$("#wrapTablero").css("width",""+width2+"px");
				$("#wrapTablero").css("height",""+width2+"px");
				
			}
			inicial = false;
		}
			//var h = w;
		var width3 = parseInt($(window).width()*0.9);
		var height3 = parseInt($(window).height()*0.9)-100;
		var wmin;
		var hmin;
		var entro= false;
		if (width3 != width2 || height3 != height2){
			entro = true;
			if (width3 < width2 ){
				wmin = width3;
			}else{
				wmin = width2;
			}
			if (height3 < height2){
				hmin = height3;
			}else{
				hmin = height2;
			}
			w = wmin;
			h2 = hmin; 
			
			height2 = height3;
			width2 = width3;
		}
		
		//console.log("inicial:"+inicial+" Hi: "+h2+" Wi:"+w);
			if (w>h2){
			//if(w>h){
				$(div).css("width",""+h2+"px");
				$(div).css("height",""+h2+"px");
				if (entro){
					$("#wrapTablero").width(h2);
					$("#wrapTablero").height(h2);
				}
			}else{
				$(div).css("width",""+(w)+"px");
				$(div).css("height",""+(w)+"px");
				if (entro){
					$("#wrapTablero").width(w);
					$("#wrapTablero").height(w);
				}
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
	function cancelarSeleccion(){
		seleccionadoObjecto = false;
		ficha = null;
	}
	
	function activarFigura(){
		seleccionadoObjecto = true;
		ficha = $(this).attr("id");
		
	}
	var ficha =null;
	function quitarEfecto(ficha,casilla){
		ws.send("{\"tipo\":\"quitarEfecto\", \"efecto\" : \""+ficha+"\" , \"casilla\": \""+casilla+"\" }");
	}
	function quitarFicha(ficha,casilla){
		ws.send("{ \"tipo\": \"quitarFicha\" , \"ficha\":\""+ficha+"\" , \"casilla\": \""+casilla+"\" }");
	}
	function colocarEfecto(ficha,casilla){
		ws.send("{\"tipo\":\"colocarEfecto\", \"efecto\" : \""+ficha+"\" , \"casilla\": \""+casilla+"\" }");
	}
	function colocarFicha(ficha,casilla){
		ws.send("{ \"tipo\": \"colocarFicha\" , \"ficha\":\""+ficha+"\" , \"casilla\": \""+casilla+"\" }");
	}
	var mismoObjeto = false;
	function accionTablero(){
		if (seleccionadoObjecto){
			if (ficha.length==1){
				if($(this).hasClass(ficha)){
					quitarEfecto(ficha,$(this).attr("id"));
				}else{
					colocarEfecto(ficha,$(this).attr("id"));
				}
				
			}else{
				if (ficha.length==2){
					if($(this).hasClass(ficha)){
						quitarFicha(ficha,$(this).attr("id"));
					}else{
						colocarFicha(ficha,$(this).attr("id"));
					}
				}
			}
				
			
		}else{
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
				$("#"+coordenadaInicial).toggleClass("azul");
				$("#"+coordenadaFinal).toggleClass("azul");
				enviarCoordenadas();
				coordenadaInicial = null;
				coordenadaFinal = null;
			}
		}
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
	function quitarTodasLasFichas(){
		$("#tablero td").each(function(){
			
			$(this).removeClass("bp bt bc ba br bd np nt nc na nr nd");
		});
	}
	function quitarTodasLosEfectos(){
		$("#tablero td").each(function(){
			
			$(this).removeClass("r a v");
		});
	}
	function moverFicha(inicio, fin){
		
		
		//alert(inicio+"-"+fin);
		var ficha = obtenerFicha(inicio);
		$("#"+inicio).toggleClass(ficha);
		
		$("#"+fin).toggleClass(obtenerFicha(fin));
		$("#"+fin).toggleClass(ficha);
		
		$("#tablero td").removeClass("rojo");
		$("#"+inicio).addClass("rojo");
		$("#"+fin).addClass("rojo");
		
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
	function crearFicha2(ficha,color,posicion){
		var str ="";
		if (color == "b"){
			str+="b";
		}else{
			str+="n";
		}
		str+=ficha;
		$("#"+posicion).toggleClass(obtenerFicha(posicion));
		$("#"+posicion).toggleClass(str);
	}


	
	    
	       
	
	 /****WebRTC de ARRAYS*********/
	function llamarAusuarios(){
		if (!streamToAttach)
		activarMedia();
		for (var usuario in conexiones){
			crearOffer(usuario);
		}
	}
	function activarMedia(){
		navigator.getUserMedia({"audio":true,"video":true},
			function (stream){
				if (navigator.webkitGetUserMedia){
					videoLocal.src = webkitURL.createObjectURL(stream);
					streamToAttach = stream;
				}
				if (navigator.mozGetUserMedia){
					videoLocal.mozSrcObject = stream;
					videoLocal.play();
					streamToAttach = stream;
				}
			}
			,error
			);
		 	
		 	console.log("se activo la media");
		 	
		}
		
		function closeMedia(){
			
			streamToAttach.stop();
			//$("#btnActivarMedia").show();
			
		}
		function close(usuario){
			if (conexiones[usuario]!= "sinpeer"){
				conexiones[usuario].close();
				conexiones[usuario] = null;
			}
		}
		
		function crearOffer(usuario){
		close(usuario);
		
		conexiones[usuario] = new RTCPeerConnection(iceServers,optional);
		conexiones[usuario].onicecandidate = onicecandidate;
		
		conexiones[usuario].onaddstream = function (event){
			console.log("evento onaddstream disparado!");
			if (!event) return;
			if (navigator.webkitGetUserMedia){
				document.getElementById("video"+usuario).src = webkitURL.createObjectURL(event.stream);		
			}
			if (navigator.mozGetUserMedia){
				document.getElementById("video"+usuario).mozSrcObject  = event.stream;		
			}
			
			
		};
		if (streamToAttach !=null){
			conexiones[usuario].addStream (streamToAttach);
		}
		
		conexiones[usuario].createOffer(
			function(sessionDescription) {
				conexiones[usuario].setLocalDescription(sessionDescription);
				//$("#miDescripcion").html(JSON.stringify(sessionDescription));
				//Se ejecuta tan rapido que no da tiempo a crear el objeto
				setTimeout(function(){
					ws.send("{\"tipo\": \"offer\" , \"a\": \""+usuario+"\" , \"sd\" : "+JSON.stringify(sessionDescription)+" }"); 
				},1000);
				
			}
			, 
			error
			, 
			{ 'mandatory': { 'OfferToReceiveAudio': true, 'OfferToReceiveVideo': true }}
		);
			//$("#crearOffer").hide();
			
		}
		function onicecandidate(event) {
		/*
		if (!peer || !event || !event.candidate) return;
		var candidate = event.candidate;
		//$("#candidato").val(JSON.stringify(candidate));
		// POST-ICE-to-other-Peer(candidate.candidate, candidate.sdpMLineIndex);
		
		//ws.send(JSON.stringify(candidate));
		*/
		}
		function crearAnswer(offer,usuario){
		close(usuario);
		
		//console.log("Entrando en crearAnswer");
		//var offer = JSON.parse($("#suDescripcion").val());
		
		conexiones[usuario] = new RTCPeerConnection(iceServers,optional);
		conexiones[usuario].onicecandidate = onicecandidate;
		conexiones[usuario].onaddstream = function a(event){
			if (!event) return;
			if (navigator.webkitGetUserMedia){
				document.getElementById("video"+usuario).src = webkitURL.createObjectURL(event.stream);		
			}
			if (navigator.mozGetUserMedia){
				document.getElementById("video"+usuario).mozSrcObject  = event.stream;		
			}
			/*
			if (!(videos[usuario].readyState <= HTMLMediaElement.HAVE_CURRENT_DATA 
					|| videos[usuario].paused || videos[usuario].currentTime <= 0)) 
				{
					// remote stream started flowing!
				} 
			*/
		};
		if (streamToAttach !=null){
			conexiones[usuario].addStream (streamToAttach);
		}
		
		
		if (navigator.mozGetUserMedia){
			conexiones[usuario].setRemoteDescription(new RTCSessionDescription(offer), function() {
				conexiones[usuario].createAnswer(function(answer) {
					conexiones[usuario].setLocalDescription(new RTCSessionDescription(answer), function() {
						//$("#miDescripcion").html(JSON.stringify(answer));
						setTimeout(function(){
							ws.send("{\"tipo\": \"answer\" , \"a\":  \""+usuario+"\", \"sd\" : "+JSON.stringify(answer)+" }");
						},1000);
				  }, error);
				}, error);
			}, error);
		}else{
		
			insertarRemoteDes(offer,usuario);
			conexiones[usuario].createAnswer(function(answer) {
				conexiones[usuario].setLocalDescription(answer);
				//$("#miDescripcion").html(JSON.stringify(answer));
				setTimeout(function(){
					ws.send("{\"tipo\": \"answer\" , \"a\":  \""+usuario+"\", \"sd\" : "+JSON.stringify(answer)+" }");
				},1000);
				console.log("Answer:");
				console.log(answer);
				},
				error
				, { 'mandatory': { 'OfferToReceiveAudio': true, 'OfferToReceiveVideo': true } }
			);
		}
		
		console.log("crearAnswer finalizado");
		//console.log(peer);
		}
		function insertarRemoteDes(sessionDescription,usuario){
		console.log("insertandoRemote");
		//var sessionDescription = JSON.parse($("#suDescripcion").val());
		conexiones[usuario].setRemoteDescription(new RTCSessionDescription(sessionDescription));
		}
		function aceptarHandling(sessionDescription,usuario){
		conexiones[usuario].setRemoteDescription(new RTCSessionDescription(sessionDescription));
		}
		function handling(sessionDescription,usuario){
		//var sessionDescription = JSON.parse($("#resultado").val());
		conexiones[usuario].setRemoteDescription(new RTCSessionDescription(sessionDescription));
		}
		
		function error(e){
		console.log(e);
		}
		function addIc(candidate,usuario){
		//var candidate = JSON.parse($("#candidato2").val());
		conexiones[usuario].addIceCandidate(new RTCIceCandidate({
			sdpMLineIndex: candidate.sdpMLineIndex,
			candidate: candidate.candidate
		}));
		}
		
		
		function onaddstream(event) {
		if (!event) return;
		if (navigator.webkitGetUserMedia){
			videoRemoto.src = webkitURL.createObjectURL(event.stream);		
		}
		if (navigator.mozGetUserMedia){
			videoRemoto.mozSrcObject  = event.stream;		
		}
		waitUntilRemoteStreamStartsFlowing();
		}
		function waitUntilRemoteStreamStartsFlowing(){
		if (!(videoRemoto.readyState <= HTMLMediaElement.HAVE_CURRENT_DATA 
			|| videoRemoto.paused || videoRemoto.currentTime <= 0)) 
		{
			// remote stream started flowing!
		} 
		else setTimeout(waitUntilRemoteStreamStartsFlowing, 50);
		}
		
});
