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

import websocket.AjedrezServlet.WebSocketConnection;



@WebServlet(urlPatterns={"/grupo"})
public class AjedrezGrupoServlet extends WebSocketServlet{

	public static HashMap<String, PartidaGrupo> partidas = new HashMap<String,PartidaGrupo>();

	public HashMap<String, WebSocketConnection> conexiones = new HashMap<String,WebSocketConnection>();

	public StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request){
		 // String connectionId = request.getSession().getId();
		
		  String userName = (String)request.getSession().getAttribute("usuario");
		  String connectionId = userName;
		 return new WebSocketConnection(connectionId, userName);
	}
	public void crearPartida(String creador){
		partidas.put(creador, new PartidaGrupo(creador));
	}
	public String obtenerSalaUsuario(String usuario){
		for (PartidaGrupo sala : partidas.values()){
			if(sala.estaUsuarioEnEstaSala(usuario)){
				return sala.creador;
			}
			
		}
		return "";
	}
	public void enviarMensaje(String mensaje, String sala){
		
		for (String usuario : partidas.get(sala).usuarios.keySet()){
			try {
				conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap(
						mensaje
						));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void quitarConexion(String usuario){
		conexiones.remove(usuario);
	}
	public void unirseApartida(String usuario, String sala){
		for (PartidaGrupo salab : partidas.values()) {
			if (salab.estaUsuarioEnEstaSala(usuario)){
				salab.eliminarUsuarioSala(usuario);
				refrescarListaUsuariosdePartida(salab.creador);
			}
		}
		if (partidas.containsKey(sala)){
			partidas.get(sala).addUsuario(usuario);
			if (sala.equals(usuario)){
				try {
					conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap("{ \"tipo\" : \"ui\" , \"modelo\" : \"admin\" }"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Fallo al enviar accion ui admin");
				}
			}else{
				try {
					conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap("{ \"tipo\" : \"ui\" , \"modelo\": \"normal\" }"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Fallo al enviar accion ui normal");
				}
			}
			
		}
		refrescarListaUsuariosdePartida(sala);
	}
	public void salirPartida(String usuario,String sala){
		partidas.get(sala).eliminarUsuarioSala(usuario);
		try {
			conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap("{ \"tipo\" : \"ui\" , \"modelo\" : \"idle\" }"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void enviarOrdenAusuario(String usuario, String accion){
		
	}
	public void cerrarSala(String sala){
		for (String usuario : partidas.get(sala).usuarios.keySet()){
			try {
				conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap("{ \"tipo\" : \"ui\" , \"modelo\" : \"idle\" }"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		partidas.remove(sala);
		enviarListaPartidasAusuarios();
	}
	
	public void enviarListaPartidasAusuarios() {
		String str = listaSalasEnJson();
		for (WebSocketConnection conn : conexiones.values()) {
			try {
				conn.getWsOutbound().writeTextMessage(CharBuffer.wrap(str));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Fallo Al enviar salas");
			}
		}
	}
	public void refrescarListaUsuariosdePartida(String sala){
		
		for (String usuario : partidas.get(sala).usuarios.keySet()){
			try {
				conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap(crearListaUsuariosDeSalaJson(sala)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public String crearListaUsuariosDeSalaJson(String sala){
		String str2 = "{ \"tipo\":\"listaUsuarios\" , \"usuarios\":  [ ";
		int con=0;
		for(String usuario : partidas.get(sala).usuarios.keySet()){
			if(con==0){
				str2 += "{ \"nombre\": \""+usuario +"\" }";
			}else{
				str2 += ",{ \"nombre\": \""+usuario +"\" }";
			}
			con++;
		}
		str2+= "]}";
		return str2;
	}
	 public String listaSalasEnJson(){
	    	String str = "{ \"tipo\":\"listaSalas\" , \"salas\": [" ;
	    	int c = 0;
	    	for (PartidaGrupo sala: partidas.values()) {
	    		if (c==0){
	    			str+= "{ \"creador\": \""+sala.creador+"\"} ";
	    		}else{
	    			str+= ",{ \"creador\": \""+sala.creador+"\"} ";
	    		}
	    		c ++;
			}
	    	
	    	str+= "]}";
	    	return str;
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
        System.out.println("Conexi�n abierta a " + this.nombre + " " + this.connectionId);
      }
    	try {
			this.getWsOutbound().writeTextMessage(CharBuffer.wrap(listaSalasEnJson()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
    }
   
    public void onClose(int status)
    {
    	String sala = obtenerSalaUsuario(this.nombre);
    	if (sala != ""){
    		if (sala.equals(this.nombre)){
    			cerrarSala(this.nombre);
    		}else{
    			salirPartida(this.nombre, sala);
        		refrescarListaUsuariosdePartida(sala);
    		}
    		
    	}
    	quitarConexion(this.nombre);
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
        System.out.println(tipo);
        if (tipo!=null)
        switch (tipo)
        {
        case "movimiento":
        	
          
          break;
        case "mensajeUsuario":
        	
        	String sala = obtenerSalaUsuario(this.nombre);
        	if (sala != ""){
        		boolean enviado = false;
            	String de2 = this.nombre;
                String a2 = StringEscapeUtils.escapeHtml4((String)json.get("a"));
            	String contenido2 = StringEscapeUtils.escapeHtml4((String)json.get("contenido"));
            	String mensaje2 = "{\"tipo\":\"mensajeUsuario\",\"contenido\":\"" + contenido2 + "\", \"de\":\"" + de2 + "\" , \"a\":\"" + a2 + "\" }";
            	
            	if (partidas.get(sala).estaUsuarioEnEstaSala(a2)){
            		conexiones.get(a2).getWsOutbound().writeTextMessage(CharBuffer.wrap(mensaje2));
            		enviado=true;
            	}
                if (enviado){
            		String men = "{ \"tipo\":\"mensajeReplica\", \"contenido\": \"" + contenido2 + "\" , \"de\":\"" + de2 + "\" , \"a\":\"" + a2 + "\"}";
                    getWsOutbound().writeTextMessage(CharBuffer.wrap(men));
                }
        	}
          break;
        case "mensajeGeneral":
        	
            
            
        	String sala2 = obtenerSalaUsuario(this.nombre);
        	if (sala2 != ""){
        		
        		String contenido = StringEscapeUtils.escapeHtml4((String)json.get("contenido"));

                String de = this.nombre;
                String mensaje = "{\"tipo\":\"mensajeGeneral\",\"contenido\":\"" + contenido + "\", \"de\":\"" + de + "\"}";
        		
                enviarMensaje(mensaje, sala2);
        		
        	}
            
          break;
        case "resetearPartida":
         
          break;
        case "unirsePartida":
        	unirseApartida(this.nombre, (String)json.get("idPartida"));
        	refrescarListaUsuariosdePartida((String)json.get("idPartida"));
          break;
        case "webrtc":
        	
        	break;
        case "ping":
        	
        break;
        case "actualizarPartidas":
        	 
        	break;
        case "salirSala":
        	String sala3 = obtenerSalaUsuario(this.nombre);
        	if (sala3 != ""){
        		if (sala3.equals(this.nombre)){
        			cerrarSala(this.nombre);
        		}else{
        			salirPartida(this.nombre, sala3);
            		refrescarListaUsuariosdePartida(sala3);
        		}
        		
        	}
        	
        	break;
        case "aceptaTablas":
        	
        	break;
        case "pedirTablas":
        	
        	break;
        case "crearPartida":
        		System.out.println("CreandoPartida");	
        		crearPartida(this.nombre);
        		unirseApartida(this.nombre, this.nombre);
        		enviarListaPartidasAusuarios();
        	break;
        }

      }
      catch (ParseException e)
      {
        //System.out.println("Fallo la conversi�n del JSON-> "+charBuffer);
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