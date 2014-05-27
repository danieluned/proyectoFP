
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
	<head>
		<meta charset="UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="description" content="Pantalla de logeo">
		<title><s:text name="inicio"/></title>
		<link rel="stylesheet" href="css/normalize.min.css">
        <link rel="stylesheet" href="css/black-tie/jquery-ui-1.10.4.custom.css" >
		<link rel="stylesheet" href="css/bootstrap.css"/>
		<link rel="stylesheet" href="css/juego2.css"/>
		<link rel="stylesheet" href="css/chat.css"/>
		<script src="js/modernizr-2.6.2-respond-1.1.0.min.js"></script>
		<script src="js/jquery-1.10.2.js"></script>
		<script src="js/jquery-ui-1.10.4.custom.js"></script>
		<script src="js/bootstrap.js"></script>
		<script src="js/bootbox.min.js"></script>
		<script src="js/arrange.js"></script>
		<script src="js/design.js"></script>
		
		<!--<script src="js/juego2.js"></script>-->
	</head>
	<body id="body" onload="init()">
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
					<a id="es" href="?request_locale=es">ES</a>
					<a id="en" href="?request_locale=en">EN</a>
					
				</div>
			</header>
			
			
			<div class="row alineadoVertical">
				<h4 class="h1 centrado"><s:text name="login"/></h4>
				<div class="col-xs-8 col-xs-offset-2
				            col-sm-3 col-sm-offset-4
				            col-md-3 col-md-offset-5
				            col-lg-2 col-lg-offset-5">
					<div class="row">
						<div id="videos">
					      <video id="you" class="flip" autoplay></video>
					    </div>
					    <div id="chatbox">
					      <button id="fullscreen">Enter Full Screen</button>
					      <button id="newRoom">Create A New Room</button>
					      <div id="messages">
					      </div>
					      <br>
					      <input id="chatinput" type="text"/>
					    </div>
					    
					</div>
					
				</div>
			</div>
			<div id="push">
			</div>
		</div>
		<footer id="footer">
			<div class="container">
				<div class="row center-block">
					<s:text name="footer"/>
				</div>
			</div>
		</footer>
		<script src="js/webrtc.io.js"></script>
		<script src="js/client.js"></script>
	</body>
</html>