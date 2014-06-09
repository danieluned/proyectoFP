package dao;

public class FactoriaDAO {
	public static InterfazDAOUsuario obtenerDAOUsuario (String basededatos) throws Exception{
		if ("MySql".equals(basededatos)){
			return new DAOMysqlUsuario();
		}else {
			return null;
		}
		
	}
	public static InterfazDAOEvento obtenerDAOEvento (String basededatos) throws Exception{
		if ("MySql".equals(basededatos)){
			return new DAOMysqlEvento();
		}else {
			return null;
		}
		
	}
	
}
