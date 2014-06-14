/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package proyectoFP.actions;

import beans.Usuario;

import com.opensymphony.xwork2.ActionSupport;

import dao.FactoriaDAO;
import dao.InterfazDAOUsuario;

import java.io.PrintStream;
/**
 * Clase RegistroAction
 * Esta clase es llamada cuando se quiere crear un nuevo usuario.
 * La clase recoge los datos de la base de datos y comprueba
 * @author alumno
 *
 */
public class RegistroAction extends ActionSupport {
	private Usuario usuario;
	public String execute() throws Exception {
		try{
			
		
		InterfazDAOUsuario daoUsuario = FactoriaDAO.obtenerDAOUsuario("MySql");
		if (daoUsuario.verificarUsuario(usuario)==null){
			if (daoUsuario.insertarUsuario(usuario)){
				addActionMessage(getText("creacionCorrecta"));
				
				return "success";
			}else{
				addActionMessage(getText("creadoFail"));
				return "fail";
			}
		}else{
			addActionMessage(getText("usuarioYaExiste"));
			return "fail";
		}
		}catch(Exception e){
			addActionMessage(getText("errorRegistro"));
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