$(function(){
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
	
              
	
                
	             //Variables
	var peer;
    var streamLocal;
	var streamToAttach;
	var videoLocal = document.getElementById("videoLocal");
	var videoRemoto = document.getElementById("videoRemoto");
				//Eventos           
	
    $("#crearOffer").click(crearOffer);
	$("#crearAnswer").click(crearAnswer);
	
    $("#handling").click(handling);
    $("#btnActivarMedia").click(activarMedia);
                //Functiones
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
	}
	function crearOffer(){
		peer = null;
		peer = new RTCPeerConnection(iceServers,optional);
		peer.onicecandidate = onicecandidate;
		peer.onaddstream = onaddstream;
		peer.addStream (streamToAttach);
		peer.createOffer(
			function(sessionDescription) {
				peer.setLocalDescription(sessionDescription);
				//$("#miDescripcion").html(JSON.stringify(sessionDescription));
				ws.send(JSON.stringify(sessionDescription));
			}
			, 
			error
			, 
			{ 'mandatory': { 'OfferToReceiveAudio': true, 'OfferToReceiveVideo': true }}
		);
		
	}
	function onicecandidate(event) {
		if (!peer || !event || !event.candidate) return;
		var candidate = event.candidate;
		//$("#candidato").val(JSON.stringify(candidate));
		// POST-ICE-to-other-Peer(candidate.candidate, candidate.sdpMLineIndex);
		ws.send(JSON.stringify(candidate));
	}
	function crearAnswer(offer){
		console.log("Entrando en crearAnswer");
		//var offer = JSON.parse($("#suDescripcion").val());
		peer = null;
		peer = new RTCPeerConnection(iceServers,optional);
		peer.onicecandidate = onicecandidate;
		peer.onaddstream = onaddstream;
		peer.addStream (streamToAttach);
		
		if (navigator.mozGetUserMedia){
			peer.setRemoteDescription(new RTCSessionDescription(offer), function() {
				peer.createAnswer(function(answer) {
					peer.setLocalDescription(new RTCSessionDescription(answer), function() {
						//$("#miDescripcion").html(JSON.stringify(answer));
						ws.send(JSON.stringify(answer));
				  }, error);
				}, error);
			}, error);
		}else{
			insertarRemoteDes();
			peer.createAnswer(function(answer) {
				peer.setLocalDescription(answer);
				//$("#miDescripcion").html(JSON.stringify(answer));
				ws.send(JSON.stringify(answer));
				console.log("Answer:");
				console.log(answer);
				},
				error
				, { 'mandatory': { 'OfferToReceiveAudio': true, 'OfferToReceiveVideo': true } }
			);
		}
		
		console.log("crearAnswer finalizado");
		console.log(peer);
	}
	function insertarRemoteDes(sessionDescription){
		console.log("insertandoRemote");
		//var sessionDescription = JSON.parse($("#suDescripcion").val());
		peer.setRemoteDescription(new RTCSessionDescription(sessionDescription));
	}
	function handling(sessionDescription){
		//var sessionDescription = JSON.parse($("#resultado").val());
		peer.setRemoteDescription(new RTCSessionDescription(sessionDescription));
	}
	
	function error(e){
		console.log(e);
	}
	function addIc(candidate){
		//var candidate = JSON.parse($("#candidato2").val());
		peer.addIceCandidate(new RTCIceCandidate({
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