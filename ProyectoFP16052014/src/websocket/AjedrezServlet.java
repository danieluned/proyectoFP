/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ajedrez.Partida;

@WebServlet(urlPatterns={"/ajedrez"})
public class AjedrezServlet extends WebSocketServlet implements Runnable{

  private static final long serialVersionUID = 1L;
  Thread tx = new Thread();
  public static HashMap<String, Partida> partidas = new HashMap<String,Partida>();

  public HashMap<String, WebSocketConnection> conexiones = new HashMap<String,WebSocketConnection>();

  public StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request){
	  String connectionId = request.getSession().getId();
	  String userName = (String)request.getSession().getAttribute("usuario");
	  System.out.println("conne: " + connectionId);
	  return new WebSocketConnection(connectionId, userName);
  }
  

  public void init() throws ServletException
  {
    System.out.println("Servlet Ajedrez iniciado");
    tx.start();
  }

  public void destroy()
  {
    super.destroy();
    System.out.println("Servlet Ajedrez destruido");
    tx.stop();
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
  {
    super.doGet(req, resp);
    System.out.println("Servlet Ajedrez mensaje get");
  }

  public String partidasJSON() {
 
    String partidasJSON = "{\"tipo\":\"partidasDisponibles\",\"partidas\":[";
    int cont = 0;
    for (Partida partida : AjedrezServlet.partidas.values()) {
      
    	if (cont == 0)
        partidasJSON = partidasJSON + "{\"blancas\":\"" + partida.getJugadorBlancas() + "\",\"negras\":\"" + partida.getJugadorNegras() + "\",\"estado\":\"" + partida.getEstadoActual() + "\" ,\"fase\":\"" + partida.getFase() + "\"}";
      else {
        partidasJSON = partidasJSON + ",{\"blancas\":\"" + partida.getJugadorBlancas() + "\",\"negras\":\"" + partida.getJugadorNegras() + "\",\"estado\":\"" + partida.getEstadoActual() + "\" ,\"fase\":\"" + partida.getFase() + "\"}";
      }
      ++cont;
    }

    partidasJSON = partidasJSON + "]}";
    return partidasJSON; 
   }
  public void cerrarPartidas(){
	  for (Partida partida : AjedrezServlet.partidas.values()) {
		if (!partida.getEstadoActual().equals("jugando") 
			||  !partida.getEstadoActual().equals("EsperandoOtroJugador") 
			){
			partidas.remove(partida);
		}
	  }
  }
  public void actualizarPartidas() throws IOException {
    try {
    	cerrarPartidas();
    	String partidasJSON = partidasJSON();
     
      
      String conex = "Conexiones: ";

      for (WebSocketConnection value : this.conexiones.values()) {
        conex = conex + " - " + value.nombre;
        value.getWsOutbound().writeTextMessage(CharBuffer.wrap(partidasJSON));
      }
      conex = conex + "\n";
      System.out.println(conex);
    } catch (Exception e) {
      System.out.println("Error en actualizar Partidas.");
    }
  }

  public boolean esPartidaVacia(Partida p) {
    return ((p.getJugadorBlancas() == "") && (p.getJugadorNegras() == ""));
  }

  public class WebSocketConnection extends MessageInbound
  {
    String partida = "";
    String connectionId;
    String nombre;

    WebSocketConnection(String paramString1, String paramString2)
    {
      this.connectionId = paramString1;
      this.nombre = paramString2;
    }

    public void onOpen(WsOutbound outbound)
    {
      if (!(AjedrezServlet.this.conexiones.containsKey(this.connectionId))) {
        AjedrezServlet.this.conexiones.put(this.connectionId, this);
        System.out.println("Conexión abierta a " + this.nombre + " " + this.connectionId);
      }

      for (WebSocketConnection value : AjedrezServlet.this.conexiones.values())
        try {
          value.getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"mensajeCorrectoChat\" , \"contenido\":\"SISTEMA: ha entrado " + this.nombre + "\"}"));
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      
      try
      {
        getWsOutbound().writeTextMessage(CharBuffer.wrap(AjedrezServlet.this.partidasJSON()));
      }
      catch (IOException e)
      {
        System.out.println("Error recibiendo partidas en " + this.nombre);
      }
    }

    public void onClose(int status)
    {
      System.out.println("Conexión cerrada a" + this.nombre);
      boolean esNuevaLista = false;
      for (Partida p : AjedrezServlet.partidas.values()){
    	  if (p.getEstadoActual().equals("Jugando")){
    		  if (p.getJugadorBlancas().equals(this.nombre)){
        		  p.setEstadoActual("Ganan negras");
        		  
        		  esNuevaLista = true;
        		  
         		 try {
    				p.getConexNegras()
    				 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  p.getJugadorBlancas() + "\", \"negras\":\"" +  p.getJugadorNegras() + "\" , \"accion\":\"ganas\" }"));
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        	  }else if(p.getJugadorNegras().equals(this.nombre)){
        		  p.setEstadoActual("Ganan blancas");
        		 
        		  esNuevaLista = true;
        		  try {
    				p.getConexBlancas()
    					 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  p.getJugadorBlancas() + "\", \"negras\":\"" +  p.getJugadorNegras() + "\" , \"accion\":\"ganas\" }"));
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
        	  }
    	  }
    	  
    		
      }
      AjedrezServlet.this.conexiones.remove(this.connectionId);
      for (WebSocketConnection value : AjedrezServlet.this.conexiones.values()) {
        try {
        	
        	value.getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"mensajeCorrectoChat\" , \"contenido\":\"SISTEMA: ha salido " + this.nombre + "\"}"));
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
     
      if (esNuevaLista)
      try
      {
        AjedrezServlet.this.actualizarPartidas();
      }
      catch (IOException e) {
        System.out.println("Error actualizandoPartidas desde onClose.");
      }
    }

    public void onTextMessage(CharBuffer charBuffer) throws IOException {
      String datos = charBuffer.toString();
      try
      {
        JSONObject json = (JSONObject)new JSONParser().parse(datos);
        String tipo = (String)json.get("tipo");
       // System.out.println(tipo);
        
        switch (tipo)
        {
        case "movimiento":
        	if (!(this.partida.equals(""))) {
        		if (AjedrezServlet.partidas.get(this.partida)!=null){
        			 String estado = AjedrezServlet.partidas.get(this.partida).getEstadoActual();
        			 AjedrezServlet.partidas.get(this.partida).movimiento(this.nombre, datos);
        			 AjedrezServlet.this.actualizarPartidas();
        			 String estadoNuevo = AjedrezServlet.partidas.get(this.partida).getEstadoActual();
                     System.out.println("Estdos: "+estado+" -> "+estadoNuevo);
                     if (estado.equals("Jugando") && estadoNuevo!=estado){
                    	 
                    	 if (estadoNuevo.equals("Ganan blancas")){
                    		 System.out.println("Enviando ganan blancas");
                    		 AjedrezServlet.partidas.get(this.partida).getConexBlancas()
                    		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"ganas\" }"));
                    		 AjedrezServlet.partidas.get(this.partida).getConexNegras()
                    		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"pierdes\" }"));
                    	}
                    	if (estadoNuevo.equals("Ganan negras")){
                    		 System.out.println("Enviando ganan negras");
                    		 AjedrezServlet.partidas.get(this.partida).getConexBlancas()
                    		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"pierdes\" }"));
                    		 AjedrezServlet.partidas.get(this.partida).getConexNegras()
                    		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"ganas\" }"));
                    	}
                    	if (estadoNuevo.equals("Tablas")){
                    		 System.out.println("Enviando tablas");
	                   		 AjedrezServlet.partidas.get(this.partida).getConexBlancas()
	                   		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"tablas\" }"));
	                   		 AjedrezServlet.partidas.get(this.partida).getConexNegras()
	                   		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"tablas\" }"));
                    	}	
                     }
        		}
            }
          break;
        case "mensajeUsuario":
        	boolean enviado = false;
        	String de2 = this.nombre;
            String a2 = StringEscapeUtils.escapeHtml4((String)json.get("a"));
        	String contenido2 = StringEscapeUtils.escapeHtml4((String)json.get("contenido"));
        	String mensaje2 = "{\"tipo\":\"mensajeUsuario\",\"contenido\":\"" + contenido2 + "\", \"de\":\"" + de2 + "\" , \"a\":\"" + a2 + "\" }";
        	for (WebSocketConnection value : AjedrezServlet.this.conexiones.values()) {
                System.out.println(value.nombre + " " + ((String)json.get("a")));
                if (value.nombre.equals((String)json.get("a"))) {
                  value.getWsOutbound().writeTextMessage(CharBuffer.wrap(mensaje2));
                  enviado = true;
                }
              }
        	if (enviado){
        		String men = "{ \"tipo\":\"mensajeReplica\", \"contenido\": \"" + contenido2 + "\" , \"de\":\"" + de2 + "\" , \"a\":\"" + a2 + "\"}";
                getWsOutbound().writeTextMessage(CharBuffer.wrap(men));
            }else{
        		 String men = "{ \"tipo\":\"mensajeReplica\", \"contenido\": \"Usuario no encontrado.\" , \"de\":\"SISTEMA\" , \"a\":\"" + a2 + "\"}";
                 getWsOutbound().writeTextMessage(CharBuffer.wrap(men));
        	}
          break;
        case "mensajeGeneral":
        	 String contenido = StringEscapeUtils.escapeHtml4((String)json.get("contenido"));

             String de = this.nombre;
             String mensaje = "{\"tipo\":\"mensajeGeneral\",\"contenido\":\"" + contenido + "\", \"de\":\"" + de + "\"}";
             for (WebSocketConnection value : AjedrezServlet.this.conexiones.values()) {
               value.getWsOutbound().writeTextMessage(CharBuffer.wrap(mensaje));
             }
            
          break;
        case "resetearPartida":
         
          break;
        case "unirsePartida":
        	//Doy por abandonada la partida si la tubiese en la que estaba por cliquear unirse a otra.
        	if (!(this.partida.equals(""))) {
               if(AjedrezServlet.partidas.get(this.partida)!=null){
            	   if (AjedrezServlet.partidas.get(this.partida).getEstadoActual().equals("Jugando")){
            		   if (AjedrezServlet.partidas.get(this.partida).getJugadorBlancas().equals(this.nombre)){
            			   AjedrezServlet.partidas.get(this.partida).setEstadoActual("Ganan negras");
            			   AjedrezServlet.partidas.get(this.partida).getConexBlancas()
                  		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"pierdes\" }"));
                  		 AjedrezServlet.partidas.get(this.partida).getConexNegras()
                  		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"ganas\" }"));
            		   }else{
            			   AjedrezServlet.partidas.get(this.partida).setEstadoActual("Ganan blancas");
            			   AjedrezServlet.partidas.get(this.partida).getConexBlancas()
                  		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"ganas\" }"));
                  		 AjedrezServlet.partidas.get(this.partida).getConexNegras()
                  		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"pierdes\" }"));
            			 }
            		   //AjedrezServlet.this.actualizarPartidas();
            	   }
            	   AjedrezServlet.partidas.get(this.partida).setEstadoActual("finalizada");
            	  partidas.remove(this.partida);
               	  AjedrezServlet.this.actualizarPartidas();
               }else{
            	   System.out.println("TeniaPartida sin crear");
               }
              
        	}else{
        		 System.out.println("NoTeniaPartida");
        	}
        	
        	//Ahora no esta jugando
        	this.partida = "";
        	
        	//Compruebo en que posicion le tocaria jugar
        	if(AjedrezServlet.partidas.get((String)json.get("idPartida"))!=null){
	        	if(AjedrezServlet.partidas.get((String)json.get("idPartida")).getJugadorNegras()!= this.nombre &&
	        	   AjedrezServlet.partidas.get((String)json.get("idPartida")).getJugadorBlancas()!= this.nombre	){
	        		System.out.println("Blancas->"+AjedrezServlet.partidas.get((String)json.get("idPartida")).getJugadorBlancas());
	        		System.out.println("Negras->"+AjedrezServlet.partidas.get((String)json.get("idPartida")).getJugadorNegras());
	        		if (AjedrezServlet.partidas.get((String)json.get("idPartida")).getJugadorBlancas() == "" &&
	        			AjedrezServlet.partidas.get((String)json.get("idPartida")).getJugadorBlancas()!= this.nombre) {
	                     (AjedrezServlet.partidas.get((String)json.get("idPartida"))).setJugadorBlancas(this.nombre);
	                     AjedrezServlet.partidas.get((String)json.get("idPartida")).setConexBlancas(this);
	                     this.partida = (String)json.get("idPartida");
	        		 }else
	        		
	        		 if (AjedrezServlet.partidas.get((String)json.get("idPartida")).getJugadorNegras() == "" &&
	             		 AjedrezServlet.partidas.get((String)json.get("idPartida")).getJugadorNegras()!= this.nombre) {
	                     AjedrezServlet.partidas.get((String)json.get("idPartida")).setJugadorNegras(this.nombre);
	                     AjedrezServlet.partidas.get((String)json.get("idPartida")).setConexNegras(this);
	                     this.partida = (String)json.get("idPartida");
	                 }
	        	}	 
            }
        	System.out.println("Ahora: "+this.partida +" Quiere"+(String)json.get("idPartida"));
        	if (this.partida!=""){
        		AjedrezServlet.partidas.get(this.partida).setEstadoActual("Jugando"); 
	        	AjedrezServlet.partidas.get(this.partida).iniciarRelojes(); 
	        	AjedrezServlet.this.actualizarPartidas();
        	}
             

             
          break;
        case "webrtc":
        	System.out.println(this.nombre+"->"+datos);
        	
        	if (!(this.partida.equals(""))) {
        		 if(AjedrezServlet.partidas.get(this.partida)!=null){
        			 if (AjedrezServlet.partidas.get(this.partida).getJugadorBlancas().equals(this.nombre)){
        				 AjedrezServlet.partidas.get(this.partida).getConexNegras()
                  		 .getWsOutbound().writeTextMessage(charBuffer);
            		   
        			 }else{
        				 AjedrezServlet.partidas.get(this.partida).getConexBlancas()
                  		 .getWsOutbound().writeTextMessage(charBuffer);
                  		
        			 }
        			
        		 }
        	}
        	break;
        case "ping":
        	
        break;
        case "actualizarPartidas":
        	 
        	break;
        case "salirPartida":
          
        	break;
        case "crearPartida":
        	//Doy por abandonada la partida si la tubiese en la que estaba por cliquear crar a otra.
        	if (!(this.partida.equals(""))) {
               if(AjedrezServlet.partidas.get(this.partida)!=null){
            	   
            	   if (AjedrezServlet.partidas.get(this.partida).getEstadoActual().equals("Jugando")){
            		   if (AjedrezServlet.partidas.get(this.partida).getJugadorBlancas().equals(this.nombre)){
            			   AjedrezServlet.partidas.get(this.partida).getConexBlancas()
                  		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"pierdes\" }"));
                  		 AjedrezServlet.partidas.get(this.partida).getConexNegras()
                  		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"ganas\" }"));
            		   }else{
            			   AjedrezServlet.partidas.get(this.partida).getConexBlancas()
                  		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"ganas\" }"));
                  		 AjedrezServlet.partidas.get(this.partida).getConexNegras()
                  		 .getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"tipo\":\"finPartida\",\"blancas\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorBlancas() + "\", \"negras\":\"" +  AjedrezServlet.partidas.get(this.partida).getJugadorNegras() + "\" , \"accion\":\"pierdes\" }"));
            			 }
            		 
            		  
            	   }
            	   AjedrezServlet.partidas.get(this.partida).setEstadoActual("finalizada");
            	 partidas.remove(this.partida);
            	 AjedrezServlet.this.actualizarPartidas();
               }
        	}
        
        	this.partida = this.nombre;
           
        	AjedrezServlet.partidas.put(this.nombre, new Partida(this.nombre, this)); 
        	
        	AjedrezServlet.this.actualizarPartidas();
           
        	break;
        }

      }
      catch (ParseException e)
      {
        //System.out.println("Fallo la conversión del JSON-> "+charBuffer);
        //System.out.println(datos);
    	  if (datos.contains("webrtc")){
    		  	
    	    	if (!(this.partida.equals(""))) {
    	    		 if(AjedrezServlet.partidas.get(this.partida)!=null){
    	    			 if (AjedrezServlet.partidas.get(this.partida).getJugadorBlancas().equals(this.nombre)){
    	    				 AjedrezServlet.partidas.get(this.partida).getConexNegras()
    	              		 .getWsOutbound().writeTextMessage(charBuffer);
    	        		   
    	    			 }else{
    	    				 AjedrezServlet.partidas.get(this.partida).getConexBlancas()
    	              		 .getWsOutbound().writeTextMessage(charBuffer);
    	              		
    	    			 }
    	    			
    	    		 }
    	    	}
    	    	System.out.println("ErrorPArseJSON.webrtc->"+charBuffer);
    	  }else{
    		  System.out.println("ErrorPArseJSON.nosesabe->"+charBuffer);
    	  }
    	
      }
    }

    public void onBinaryMessage(ByteBuffer arg0)
      throws IOException
    {
     
    }
    public void comprobarFinal(){
    	
    }
  }

@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true){
			//Para mantener las conexiones abiertas por si acaso
			//Hago pings a todos las conexiones
			for (WebSocketConnection conexion : conexiones.values()) {
				try {
					conexion.getWsOutbound().writeTextMessage(CharBuffer.wrap("ping"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				wait(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}