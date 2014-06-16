<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
	<head>
		<meta charset="UTF-8"/>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
		<meta name="viewport" content="width=device-width, initial-scale=1"/>
		<meta name="description" content="Pantalla de juego"/>
		<title><s:text name="patallaJuego"/></title>
		<link rel="stylesheet" href="css/normalize.min.css"/>
        <link rel="stylesheet" href="css/black-tie/jquery-ui-1.10.4.custom.css"/>
		<link rel="stylesheet" href="css/bootstrap.css"/>
		<link rel="stylesheet" href="css/juego2.css"/>
		<link rel="stylesheet" href="css/chat.css"/>
		<script src="js/modernizr-2.6.2-respond-1.1.0.min.js"></script>
		<script src="js/jquery-1.10.2.js"></script>
		<script src="js/jquery-ui-1.10.4.custom.js"></script>
		<script src="js/bootstrap.js"></script>
		<script src="js/bootbox.min.js"></script>
		<script src="js/arrange.js"></script>
		<script src="js/juego2.js"></script>
		<script src="js/design.js"></script>
		
		
	</head>
	 <body id="body">
        <div class="container-fluid negroTransparente" id="wrap">
			<header class="row">
				<div class="col-xs-12 col-xs-offset-0
				            col-sm-12 col-sm-offset-0
				            col-md-12 col-md-offset-0
				            col-lg-12 col-lg-offset-0">
					<s:a action="principal">
						<img class="im" src="img/Logotipo.png"/>
						<h1 class="titulo"><s:text name="tituloJuego"/></h1>
					</s:a>
					<button class="btn-sm btn-info glyphicon glyphicon-fullscreen" id="fullscreen"></button>
					<a id="es" href="?request_locale=es"><s:text name="castellano"/></a>
					<a id="en" href="?request_locale=en"><s:text name="ingles"/></a>
					<s:a action="deslogearse" cssClass="btn-sm btn-danger glyphicon glyphicon-off" id="salir"></s:a>
					<button id="cambiarChat" class="btn-sm"><s:text name="layout"/></button>
					<!-- <button id="cambiarTablero" class="btn-xs">Tablero</button>
					<button id="cambiarChat2" class="btn-xs">Chat</button>
					<button id="cambiarPartidas" class="btn-xs">Partidas</button>
					<button id="cambiarControles" class="btn-xs">Controles</button>
					-->
					<!--<button id="click">Animacion</button>-->
				</div>
			</header>
			<div id="contenido">
			
				
						<div id="tiempos" class="item">
							<p><s:text name="tiempoBlancas"/><span id="tiempoBlancas"></span></p>
							<p><s:text name="tiempoNegras"/><span id="tiempoNegras"></span></p>
							<button id="rotar" class="btn-xs"><s:text name="rotarTablero"/></button>
							<button id="pedirTablas" class="btn-sm"><s:text name="ofrecerTablas"/></button>
							<button id="pedirAbandonar" class="btn-sm btn-danger"><s:text name="ofrecerAbandono"/></button>
						</div>
				<div id="partidas" class="item">
						<div id="controlesPartida"><s:text name="listaPartidas"/></div>
						<button id="crearPartida" class="btn-sm"><s:text name="nuevaPartida"/></button>
						
						<div id="tablaPartidas">
							<table >
								<thead>
									<tr>
										<th><s:text name="blancas"/></th>
										<th><s:text name="negras"/></th>
										<th><s:text name="estado"/></th>
										<th><s:text name="turno"/></th>
										<th><s:text name="accion"/></th>
									</tr>
								</thead>
								<tbody id="listaPartidas">
								</tbody>
							</table>
						</div>
					</div>
						<div id="wrapTablero" class="item">
							<div id="moverTablero"><s:text name="tablero"/></div>
							<div id="tablero">
								<table>
									<tr>
										<td id="a8"></td>
										<td id="b8"></td>
										<td id="c8"></td>
										<td id="d8"></td>
										<td id="e8"></td>
										<td id="f8"></td>
										<td id="g8"></td>
										<td id="h8"></td>
									</tr>
									<tr>
										<td id="a7"></td>
										<td id="b7"></td>
										<td id="c7"></td>
										<td id="d7"></td>
										<td id="e7"></td>
										<td id="f7"></td>
										<td id="g7"></td>
										<td id="h7"></td>
									</tr>
									<tr>
										<td id="a6"></td>
										<td id="b6"></td>
										<td id="c6"></td>
										<td id="d6"></td>
										<td id="e6"></td>
										<td id="f6"></td>
										<td id="g6"></td>
										<td id="h6"></td>
									</tr>
									<tr>
										<td id="a5"></td>
										<td id="b5"></td>
										<td id="c5"></td>
										<td id="d5"></td>
										<td id="e5"></td>
										<td id="f5"></td>
										<td id="g5"></td>
										<td id="h5"></td>
									</tr>
									<tr>
										<td id="a4"></td>
										<td id="b4"></td>
										<td id="c4"></td>
										<td id="d4"></td>
										<td id="e4"></td>
										<td id="f4"></td>
										<td id="g4"></td>
										<td id="h4"></td>
									</tr>
									<tr>
										<td id="a3"></td>
										<td id="b3"></td>
										<td id="c3"></td>
										<td id="d3"></td>
										<td id="e3"></td>
										<td id="f3"></td>
										<td id="g3"></td>
										<td id="h3"></td>
									</tr>
									<tr>
										<td id="a2"></td>
										<td id="b2"></td>
										<td id="c2"></td>
										<td id="d2"></td>
										<td id="e2"></td>
										<td id="f2"></td>
										<td id="g2"></td>
										<td id="h2"></td>
									</tr>
									<tr>
										<td id="a1"></td>
										<td id="b1"></td>
										<td id="c1"></td>
										<td id="d1"></td>
										<td id="e1"></td>
										<td id="f1"></td>
										<td id="g1"></td>
										<td id="h1"></td>
									</tr>
								</table>
							</div>
						</div>
						
				
						
					
				
				
					
					<div id="envolver" class="item">
						<div id="mover"><s:text name="chat"/></div>
						<div id="chat">
							 <ul>
								<li><a href="#p-00"><span><s:text name="general"/></span></a></li>
							</ul>
							<div id="p-00">
							</div>
							
				        </div>
				        <input type="text" id="mensaje"/><button id="enviar"><s:text name="enviarMensaje"/></button>
					</div>	
					
					
					
			
				
					<div class="item" id="wrapmivideo" >
						<div id="arrastrarmivideo" ><s:text name="miInfo"/></div>
						<video id="videoLocal" controls autoplay>
						</video>
						<button id="btnActivarMedia"><s:text name="camaraOn"/></button>
						<button id="crearOffer"><s:text name="peerOn"/></button>
						<button id="btnColgar"><s:text name="peerOff"/></button>
					</div>
				     <div class="item" id="wrapsuvideo" >
				     	<div id="arrastrarsuvideo"><s:text name="suInfo"/></div>
				     	<video id="videoRemoto" controls autoplay>
						</video>
				     </div>         
				
			</div>
			
		</div>
		<footer id="footer">
			<div class="container">
				<div class="row center-block">
					<s:text name="footer"/>
				</div>
			</div>
		</footer>
		<!-- Sonidos -->
			<audio id="newgame">
				<source src="sounds/NEWGAME.mp3" type="audio/mp3" />
			</audio>
			<audio id="move">
				<source src="sounds/MOVE.mp3" type="audio/mp3" />
			</audio>
			<audio id=tick>
				<source src="sounds/Tick.mp3" type="audio/mp3" />
			</audio>
			<div id="push">
			</div>
			<!-- -Animaciones 
			<div id="animacion1">
				<span>Victoria</span>
			</div>
			-->
	</body>
</html>