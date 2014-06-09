/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package proyectoFP.actions;

import com.opensymphony.xwork2.ActionSupport;
import java.io.PrintStream;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

public class DeslogearseAction extends ActionSupport
  implements SessionAware
{
  private Map<String, Object> session;

  public String execute()
    throws Exception
  {
    System.out.println("Deslogeado");
    addActionMessage(getText("desloginCorrecto"));
    this.session.remove("usuario");
    return "success";
  }

  public void setSession(Map<String, Object> arg0)
  {
    this.session = arg0;
  }
}