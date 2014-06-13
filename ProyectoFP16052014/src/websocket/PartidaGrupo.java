package websocket;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public class PartidaGrupo {

	//Las casillas seran:
	//a1 a2 a3 .. b1 b2 b3 ... h1 h2 ..h8
	ConcurrentHashMap <String,String> fichas = new ConcurrentHashMap <String,String>();
	ConcurrentHashMap <String,String> colores = new ConcurrentHashMap <String,String>();
	boolean mediaOn = false;
	String creador ="";
	
	HashMap<String,String> usuarios = new HashMap<String,String>();
	
	public PartidaGrupo(String creador){
		this.creador = creador;
	}
	public void vaciarTodo(){
		fichas = new ConcurrentHashMap <String,String>();
		colores = new ConcurrentHashMap <String,String>();
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
	
	public void ponerEfecto(String casilla,String color){
		colores.put(casilla, color);
	}
	public void quitarFicha(String casilla){
		fichas.remove(casilla);
	}
	public void quitarEfecto(String casilla){
		colores.remove(casilla);
	}
	public void moverFicha(String de, String a){
		if (fichas.get(de)!= null && fichas.get(de)!=""){
			fichas.put(a,fichas.get(de));
			fichas.remove(de);
		}
		
	}
	public String efectosTableroJson(){
		String str1 ="{ \"tipo\": \"efectosTablero\" , \"efectos\" : [";
		int con = 0;
		for (String efecto : colores.keySet()) {
			if (con==0){
				str1+= "{\"casilla\": \""+efecto+"\", \"efecto\": \""+colores.get(efecto)+"\" } ";
			}else{
				str1+= ",{\"casilla\": \""+efecto+"\", \"efecto\": \""+colores.get(efecto)+"\" } ";
			}
			con++;
		}
		return str1 +="]}";
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
