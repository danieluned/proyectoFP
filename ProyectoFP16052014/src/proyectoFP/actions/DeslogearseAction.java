/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package proyectoFP.actions;

import com.opensymphony.xwork2.ActionSupport;
import java.io.PrintStream;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

/**
 * Clase DeslogearseAction
 * Esta clase sera llamada para vaciar la sessión de un usuario
 * elimina las variables de session que se tienen sobre este usuario.
 * @author alumno
 *
 */
public class DeslogearseAction extends ActionSupport
  implements SessionAware
{
  private Map<String, Object> session;

  public String execute()
    throws Exception
  {
	  try {
		  
	 
    System.out.println("Deslogeado");
    addActionMessage(getText("desloginCorrecto"));
    this.session.remove("usuario");
    this.session.remove("json");
    return "success";
	  }catch(Exception e){
		  addActionMessage(getText("desloginIncorrecto"));
		  return "fail";
	  }
  }

  public void setSession(Map<String, Object> arg0)
  {
    this.session = arg0;
  }
}