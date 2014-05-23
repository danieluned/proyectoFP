package websocket;


import org.apache.commons.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

 




import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;

 /**
  * Tanto el servidor como el usuario van a generar
  * distintos mensajes y dependiendo de estos se ejecutara
  * una u otra accion.
  *  
  * JSON 
  *
  	Los tipos de mensajes a tratar son:
   
	  					
	   	
											
	    	1)	 usuario envia mensaje de texto a todos.  						
			    	sintaxis:  { "tipo":"mensajeGeneral", "contenido": "texto" , "de":"usuarioQueEnvia"}
			    	
			2)    usuario envia mensaje de texto a un usuario especifico.			
			    	sintaxis:  { "tipo":"mensajeUsuario", "contenido": "texto" , "de":"usarioQueEnvia" , "a":"usarioDestino"}
			
			2b)  mensaje replica    	
							   { "tipo":"mensajeReplica", "contenido":"texto", "de":"usarioQueEnvia" , "a":"usarioDestino"}
			
			3)    usuario pide crear una partida al servidor.									
			    	sintaxis: { "tipo":"crearPartida" }
			    	
			4)    evento Servidor: si partida creada envia lista partidas disponibles a todos los usuarios
			    		{"tipo":"partidasDisponibles" , "partidas": [{"creador":"nombreUsuario"},...,...,...,...]}
			    		
			5a)    evento Servidor: si partida no creada envia mensaje a usuario, tipo error en el chat
			 			{"tipo":"mensajeErrorChat" , "contenido":"texto"} 
			5b)    evento Servidor: si partida creada envia mensaje a usuario, tipo success en el chat
			 			{"tipo":"mensajeCorrectoChat" , "contenido":"texto"}   
			   
			6)  usuario pide unirse a partida							
						{"tipo":"unirsePartida" , "creador":"nombreUsuario" 
			
			7)	    Movimiento.								
						{ "tipo": "movimiento","inicio":"a2", "fin":"a4"}';
						
			    Enviar abandonar partida.						05
			    Enviar petición tablas.							06
	    
	    
	 
	    
	    	...para el chat:
			    Enviar lista de personas conectadas.			07
			    Enviar 
  
  
  * @author alumno
  *
  */
@WebServlet(urlPatterns = "/grupo")
public class AjedrezGrupoServlet extends WebSocketServlet implements Runnable{
	
	HashMap<String, WebSocketConnection> conexiones = new HashMap<String, WebSocketConnection>();
	/**
	 * Este método es llamado cuando un cliente se conecta al websocket
	 * 
	 */
	public void init() throws javax.servlet.ServletException {
		System.out.println("GRUPO: Iniciando");
	};
    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
    	final String connectionId = request.getSession().getId();
    	final String userName = (String)request.getSession().getAttribute("usuario");
    	System.out.println("GRUPO: conne: "+connectionId);
        return new WebSocketConnection(connectionId,userName);
    }
 
    private  class WebSocketConnection extends MessageInbound {
    	/**
    	 * Tendremos dos variables para identificar las conexiones que se crean
    	 * mas adelante tendremos que rellenar nombre con el usuario de la session
    	 */
    	String connectionId;
    	String nombre;
    	WebSocketConnection(String idc, String nombre){
    		this.connectionId = idc;
    		this.nombre = nombre;
    	}
        @Override
        protected void onOpen(WsOutbound outbound) {
        	
        	if (!conexiones.containsKey(this.connectionId)){
        		conexiones.put(this.connectionId, this);
        		System.out.println("GRUPO: Conexión abierta a "+this.nombre);
        	}
           
            for (WebSocketConnection value : conexiones.values()) {
                try {
					value.getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"mensajeCorrectoChat\" , \"contenido\":\"SISTEMA: ha entrado "+this.nombre+"\"}"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        
        @Override
        protected void onClose(int status) {
        	 System.out.println("GRUPO: Conexión cerrada a"+this.nombre);
        	 conexiones.remove(this.connectionId);
        	 for (WebSocketConnection value : conexiones.values()) {
                 try {
					value.getWsOutbound().writeTextMessage(CharBuffer.wrap("Se ha ido "+this.nombre));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
             }
        }
        @Override
        protected void onTextMessage(CharBuffer charBuffer) throws IOException {
            String datos = charBuffer.toString();
           
            JSONObject json;
            try {
				json = (JSONObject) new JSONParser().parse(datos);
				String tipo = (String)json.get("tipo");
	            System.out.println("GRUPO: mensaje->"+tipo);
	            switch (tipo) {
				case "mensajeGeneral": //Mensaje por el chat general a todos los usarios conectados
					//sintaxis:  { "tipo":"mensajeGeneral", "contenido": "texto" , "de":"usuarioQueEnvia"}
					String contenido = StringEscapeUtils.escapeHtml4((String) json.get("contenido"));
					String de = StringEscapeUtils.escapeHtml4((String) json.get("de"));
					String mensaje = "{\"tipo\":\"mensajeGeneral\",\"contenido\":\""+contenido+"\", \"de\":\""+de+"\"}";
					for (WebSocketConnection value : conexiones.values()) {
						 value.getWsOutbound().writeTextMessage(CharBuffer.wrap(mensaje));
			         }
					break;
				case "mensajeUsuario": //Mensaje a un usuario en concreto.
					Boolean enviado = false;
					//sintaxis:  { "tipo":"mensajeUsuario", "contenido": "texto" , "de":"usarioQueEnvia" , "a":"usarioDestino"}
					String contenido2 = StringEscapeUtils.escapeHtml4((String) json.get("contenido"));
					String de2 = StringEscapeUtils.escapeHtml4((String) json.get("de"));
					String a2 = StringEscapeUtils.escapeHtml4((String) json.get("a"));
					String mensaje2 = "{\"tipo\":\"mensajeUsuario\",\"contenido\":\""+contenido2+"\", \"de\":\""+de2+"\" , \"a\":\""+a2+"\" }";
					for (WebSocketConnection value : conexiones.values()) {
						System.out.println( value.nombre + " "+(String)json.get("a"));
						if( value.nombre.equals((String)json.get("a"))){
							value.getWsOutbound().writeTextMessage(CharBuffer.wrap(mensaje2));
							enviado = true;
						}
					}
					if (enviado){
						String men = "{ \"tipo\":\"mensajeReplica\", \"contenido\": \""+contenido2+"\" , \"de\":\""+de2+"\" , \"a\":\""+a2+"\"}";
						this.getWsOutbound().writeTextMessage(CharBuffer.wrap(men));
					}else{
						String men = "{ \"tipo\":\"mensajeReplica\", \"contenido\": \"Usuario no encontrado.\" , \"de\":\"SISTEMA\" , \"a\":\""+a2+"\"}";
						this.getWsOutbound().writeTextMessage(CharBuffer.wrap(men));
					}
					break;
				case "movimiento":
					//{ "tipo": "movimiento","inicio":"a2", "fin":"a4"}';
					//Tendria que definir a que partida se refiere y que nº de movimiento
					// La partida seria un array en la que este el jugador o 
					// especificar la partida. Ahora mismo sera una unica partida general a todos
					// Aparte se tendria que validar y decir si es un movimiento legal,
					// tambien me lo salto por ahora.
					for (WebSocketConnection value : conexiones.values()) {
						 value.getWsOutbound().writeTextMessage(CharBuffer.wrap(datos));
			         }
					break;
				case "resetearPartida":
					for (WebSocketConnection value : conexiones.values()) {
						 value.getWsOutbound().writeTextMessage(CharBuffer.wrap(datos));
			         }
					break;
				case "04":
					
					break;
				case "05":
					
					break;
				case "06":
					
					break;
				default:
					break;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("GRUPO: Fallo la conversión del JSON");
			}
            
           
        }
        
		@Override
		protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
			for (WebSocketConnection value : conexiones.values()) {
				 
				 value.getWsOutbound().writeBinaryMessage(arg0);
	         }
			
		}
    }
    public AjedrezGrupoServlet() {
		// TODO Auto-generated constructor stub
		 Thread tx =new Thread(this);
	        tx.start();
	}
	@Override
	public void run() {
		/*
		while (true){
			
			for (WebSocketConnection value : conexiones.values()) {
                try {
					value.getWsOutbound().writeTextMessage(CharBuffer.wrap("Hola capullo"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
			 try {
				 int sd	=  (int) (Math.random()*10)+4000;
	                Thread.sleep(sd);
	            } catch (InterruptedException ex) {
	                System.out.println(ex.getMessage());
	            }
		}
		*/
	}
}