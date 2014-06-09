/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package proyectoFP.actions;

import beans.Usuario;

import com.opensymphony.xwork2.ActionSupport;

import dao.FactoriaDAO;

import java.io.PrintStream;

public class RegistroAction extends ActionSupport {
	private Usuario usuario;
	public String execute() throws Exception {
		
		if (FactoriaDAO.obtenerDAOUsuario("MySql").insertarUsuario(usuario)){
			addActionMessage(getText("creacionCorrecta"));
			
			return "success";
		}else{
			addActionMessage(getText("creadoFail"));
			return "fail";
		}
		
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	
}