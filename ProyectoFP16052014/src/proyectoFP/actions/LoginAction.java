/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package proyectoFP.actions;

import beans.Usuario;

import com.opensymphony.xwork2.ActionSupport;

import dao.FactoriaDAO;

import java.io.PrintStream;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
/**
 * Clase LoginAction
 * Esta clase es llamada cuando se logea un usuario,
 * comprueba con la base de datos si el usuario esta autentificado
 * y entonces lo autoriza en el sistema mediante una inserción de sus datos en la session
 * @author alumno
 *
 */
public class LoginAction extends ActionSupport implements SessionAware {
	private Usuario usuario;
	
	private Map<String, Object> session;

	public String execute() throws Exception {
		try{
			
		
		System.out.printf("Login-> Nombre: %s - Password: %s ", new Object[] {
				this.usuario.getUsuario(), this.usuario.getPassword() });
		Usuario user = FactoriaDAO.obtenerDAOUsuario("MySql").verificarUsuario(usuario);
		if (user !=null){
			addActionMessage(getText("loginCorrecto"));
			this.session.put("usuario", user.getUsuario());
			this.session.put("json",user.getJsonConfi());
			return "success";
		}else{
			addActionMessage(getText("loginIncorrecto"));
			return "fail";
		}
		}catch(Exception e){
			addActionMessage(getText("errorProcesando"));
			return "fail";

		}
		
	}
	
	public Usuario getUsuario() {
		return usuario;
	}



	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}



	public Map<String, Object> getSession() {
		return session;
	}



	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}
}