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
				//Webrtc- cuando se une un usuario preguntara si hay media
				//si hay media, se enviara el id al hoster para que contatacte
				String pedirQueLeLlamen ="{\"tipo\": \"llamame\", \"usuario\": \""+usuario+"\"}";
				try {
					conexiones.get(partidas.get(sala).creador).getWsOutbound().writeTextMessage(CharBuffer.wrap(pedirQueLeLlamen));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//
			}
			try {
				conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap(partidas.get(sala).efectosTableroJson()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap(partidas.get(sala).fichasTableroJson()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	public void enviarMovimiento(String de , String a, String sala){
		partidas.get(sala).moverFicha(de,a);
		String str = partidas.get(sala).fichasTableroJson();
		for (String usuario : partidas.get(sala).usuarios.keySet()){
			try {
				conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap(str));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	public void quitarEfecto(String efecto,String casilla,String sala){
		partidas.get(sala).quitarEfecto(casilla);
		for (String usuario : partidas.get(sala).usuarios.keySet()){
			try {
				
				String jsonEfectos = partidas.get(sala).efectosTableroJson();
				conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap(jsonEfectos));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void quitarFicha(String ficha,String casilla,String sala){
		partidas.get(sala).quitarFicha(casilla);
		for (String usuario : partidas.get(sala).usuarios.keySet()){
			try {
				String jsonEfectos = partidas.get(sala).fichasTableroJson();
				conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap(jsonEfectos));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void colocarEfecto(String efecto,String casilla,String sala){
		partidas.get(sala).ponerEfecto(casilla, efecto);
		for (String usuario : partidas.get(sala).usuarios.keySet()){
			try {
				
				String jsonEfectos = partidas.get(sala).efectosTableroJson();
				conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap(jsonEfectos));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void colocarFicha(String ficha,String casilla,String sala){
		partidas.get(sala).ponerFicha(casilla, ficha);
		for (String usuario : partidas.get(sala).usuarios.keySet()){
			try {
				String posicion = casilla;
				char color = ficha.charAt(0);
				char ficha2 = ficha.charAt(1);
				
				conexiones.get(usuario).getWsOutbound().writeTextMessage(CharBuffer.wrap("{ \"tipo\" : \"crearFicha2\" , \"posicion\" : \""+posicion+"\" ,\"color\": \""+color+"\" , \"ficha\":\""+ficha2+"\" }"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	public void activarMedia(String sala){
		partidas.get(sala).mediaOn=true;
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
    JSONObject offer;
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
    	try {
			this.getWsOutbound().writeTextMessage(CharBuffer.wrap(listaSalasEnJson()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
    }
   
    public void onClose(int status)
    {
    	quitarConexion(this.nombre);
    	String sala = obtenerSalaUsuario(this.nombre);
    	if (sala != ""){
    		if (sala.equals(this.nombre)){
    			cerrarSala(this.nombre);
    		}else{
    			salirPartida(this.nombre, sala);
        		refrescarListaUsuariosdePartida(sala);
    		}
    		
    	}
    	
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
        case "mediaOn":
        	
        	
        	if ( obtenerSalaUsuario(this.nombre) != ""){
        		
        		activarMedia( obtenerSalaUsuario(this.nombre));
        	}
        	break;
        case "movimiento":
        	String sala = obtenerSalaUsuario(this.nombre);
        	String de =(String) json.get("inicio");
        	String a = (String)json.get("fin");
        	if (sala != ""){
        		
        		enviarMovimiento(de,a,sala);
        	}
          
          break;
        case "mensajeUsuario":
        	
        	String sala5 = obtenerSalaUsuario(this.nombre);
        	if (sala5 != ""){
        		boolean enviado = false;
            	String de2 = this.nombre;
                String a2 = StringEscapeUtils.escapeHtml4((String)json.get("a"));
            	String contenido2 = StringEscapeUtils.escapeHtml4((String)json.get("contenido"));
            	String mensaje2 = "{\"tipo\":\"mensajeUsuario\",\"contenido\":\"" + contenido2 + "\", \"de\":\"" + de2 + "\" , \"a\":\"" + a2 + "\" }";
            	
            	if (partidas.get(sala5).estaUsuarioEnEstaSala(a2)){
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

                String de5 = this.nombre;
                String mensaje = "{\"tipo\":\"mensajeGeneral\",\"contenido\":\"" + contenido + "\", \"de\":\"" + de5 + "\"}";
        		
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
        case "colocarEfecto":
        	String sala6 = obtenerSalaUsuario(this.nombre);
        	String casilla6 = (String)json.get("casilla");
        	String efecto6 = (String)json.get("efecto");
        	if (sala6 != ""){
        		colocarEfecto(efecto6,casilla6,sala6);
        		
        	}
        	break;
        case "colocarFicha":
        	String sala4 = obtenerSalaUsuario(this.nombre);
        	String casilla = (String)json.get("casilla");
        	String ficha = (String)json.get("ficha");
        	if (sala4 != ""){
        		colocarFicha(ficha,casilla,sala4);
        		
        	}
        	break;
        case "quitarEfecto":
        	String sala16 = obtenerSalaUsuario(this.nombre);
        	String casilla16 = (String)json.get("casilla");
        	String efecto16 = (String)json.get("efecto");
        	if (sala16 != ""){
        		quitarEfecto(efecto16,casilla16,sala16);
        		
        	}
        	break;
        case "quitarFicha":
        	String sala14 = obtenerSalaUsuario(this.nombre);
        	String casilla1 = (String)json.get("casilla");
        	String ficha1 = (String)json.get("ficha");
        	if (sala14 != ""){
        		quitarFicha(ficha1,casilla1,sala14);
        		
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