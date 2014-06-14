package beans;

import java.util.Date;
/**
 * Clase Evento
 * Esta clase es usada para registrar en el calendario las fechas 
 * que los usuarios marquen. 
 * @author alumno
 *
 */
public class Evento {
	Date fecha;
	String contenido;
	String usuario;
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
}
