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



@WebServlet(urlPatterns={"/grupo"})
public class AjedrezGrupoServlet extends WebSocketServlet{

	public static HashMap<String, PartidaGrupo> partidas = new HashMap<String,PartidaGrupo>();

	public HashMap<String, WebSocketConnection> conexiones = new HashMap<String,WebSocketConnection>();

	public StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request){
		  String connectionId = request.getSession().getId();
		  String userName = (String)request.getSession().getAttribute("usuario");
		 return new WebSocketConnection(connectionId, userName);
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
    	if (!(AjedrezGrupoServlet.this.conexiones.containsKey(this.connectionId))) {
        AjedrezGrupoServlet.this.conexiones.put(this.connectionId, this);
        System.out.println("Conexión abierta a " + this.nombre + " " + this.connectionId);
      }

      
    }

    public void onClose(int status)
    {
    	
    }

    public void onTextMessage(CharBuffer charBuffer) throws IOException {
      String datos = charBuffer.toString();
      System.out.println(datos);
      try
      {
        JSONObject json = (JSONObject)new JSONParser().parse(datos);
        
        if(json.get("sdp")!=null){
	  		  	
	  	    	
	  	    	
	    }
        //
        String tipo = (String)json.get("tipo");
        //System.out.println(tipo);
        if (tipo!=null)
        switch (tipo)
        {
        case "movimiento":
        	
          
          break;
        case "mensajeUsuario":
        	
          break;
        case "mensajeGeneral":
        	
            
          break;
        case "resetearPartida":
         
          break;
        case "unirsePartida":
        	
          break;
        case "webrtc":
        	
        	break;
        case "ping":
        	
        break;
        case "actualizarPartidas":
        	 
        	break;
        case "pedirAbandono":
        	
        	break;
        case "aceptaTablas":
        	
        	break;
        case "pedirTablas":
        	
        	break;
        case "crearPartida":
        	
        	break;
        }

      }
      catch (ParseException e)
      {
        //System.out.println("Fallo la conversión del JSON-> "+charBuffer);
    	  System.out.println("ErrorPArseJSON.nosesabe->"+charBuffer);
    
    	
      }
    }

    public void onBinaryMessage(ByteBuffer arg0)
      throws IOException
    {
     
    }
    public void comprobarFinal(){
    	
    }
  }


}