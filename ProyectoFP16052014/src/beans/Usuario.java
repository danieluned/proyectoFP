package beans;

import java.util.ArrayList;
/**
 * Clase Usuario
 * Esta clase sera usada para contener la información de los usuarios
 * que accedan al sistema.
 * @author alumno
 *
 */
public class Usuario {
	String usuario;
	String password;
	String jsonConfi;
	ArrayList<Evento> eventos;
	
	public Usuario(){
		
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJsonConfi() {
		return jsonConfi;
	}

	public void setJsonConfi(String jsonConfi) {
		this.jsonConfi = jsonConfi;
	}

	public ArrayList<Evento> getEventos() {
		return eventos;
	}

	public void setEventos(ArrayList<Evento> eventos) {
		this.eventos = eventos;
	}
	
}
