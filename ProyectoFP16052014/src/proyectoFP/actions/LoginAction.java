/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package proyectoFP.actions;

import beans.Usuario;

import com.opensymphony.xwork2.ActionSupport;

import dao.FactoriaDAO;

import java.io.PrintStream;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

public class LoginAction extends ActionSupport implements SessionAware {
	private Usuario usuario;
	
	private Map<String, Object> session;

	public String execute() throws Exception {
		
		System.out.printf("Login: Nombre: %s - Password: %s %n", new Object[] {
				this.usuario.getUsuario(), this.usuario.getPassword() });
		if (FactoriaDAO.obtenerDAOUsuario("MySql").verificarUsuario(usuario.getUsuario()) !=null){
			addActionMessage(getText("loginCorrecto"));
			this.session.put("usuario", this.usuario);
			return "success";
		}else{
			addActionMessage(getText("loginIncorrecto"));
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