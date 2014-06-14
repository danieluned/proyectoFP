/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package proyectoFP.interceptores;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import java.util.Map;
/**
 * Clase ComprobarSessionInterceptor
 * Esta clase sera usada para comprobar si el usuario ya
 * esta en la session antes de acceder al recurso, de no 
 * estarlo, se le impediria el acceso.
 * 
 * @author alumno
 *
 */
public class ComprobarSessionInterceptor implements Interceptor {
	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		Map session = invocation.getInvocationContext().getSession();
		String result = "errorLogin";
		if (session == null) {
			result = "errorLogin";
		} else if (session.get("usuario") == null)
			result = "errorLogin";
		else {
			result = invocation.invoke();
		}

		return result;
	}
}