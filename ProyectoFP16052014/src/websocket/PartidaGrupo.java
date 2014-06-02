package websocket;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public class PartidaGrupo {

	//Las casillas seran:
	//a1 a2 a3 .. b1 b2 b3 ... h1 h2 ..h8
	HashMap<String,String> tablero = new HashMap<String,String>();
	String idPartida ="";
	String creador ="";
	
	
	public PartidaGrupo(){
		
	}
	public void ponerFicha(String casilla,String ficha){
		
		if (tablero.get("casilla")!=null){
			
		}else{
			
		}
	}
	
	
}
