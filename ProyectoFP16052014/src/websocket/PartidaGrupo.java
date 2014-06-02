package websocket;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public class PartidaGrupo {

	//Las casillas seran:
	//a1 a2 a3 .. b1 b2 b3 ... h1 h2 ..h8
	HashMap<String,String> fichas = new HashMap<String,String>();
	HashMap<String,String> colores = new HashMap<String,String>();
	
	String creador ="";
	
	HashMap<String,String> usuarios = new HashMap<String,String>();
	
	public PartidaGrupo(String creador){
		this.creador = creador;
	}
	public void addUsuario(String usuario){
		usuarios.put(usuario, "");
	}
	public boolean estaUsuarioEnEstaSala(String usuario){
		return usuarios.containsKey(usuario);
	}
	public void eliminarUsuarioSala(String usuario){
		usuarios.remove(usuario);
	}
	public void ponerFicha(String casilla,String ficha){
		fichas.put(casilla, ficha);
	}
	public void ponerColor(String casilla,String color){
		colores.put(casilla, color);
	}
	
	public String fichasTableroJson(){
		String str1 ="{ \"tipo\": \"fichasTablero\" , \"fichas\" : [";
		int con = 0;
		for (String ficha : fichas.keySet()) {
			if (con==0){
				str1+= "{\"casilla\": \""+ficha+"\", \"pieza\": \""+fichas.get(ficha)+"\" } ";
			}else{
				str1+= ",{\"casilla\": \""+ficha+"\", \"pieza\": \""+fichas.get(ficha)+"\" } ";
			}
			con++;
		}
		return str1 +="]}";
	}
}
