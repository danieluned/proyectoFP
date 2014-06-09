package dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import beans.Evento;
import beans.Usuario;
import db.DBFacade;

public class DAOMysqlEvento implements InterfazDAOEvento {

	private DBFacade db = null;
	public DAOMysqlEvento() throws Exception {
		try {
			db = new DBFacade();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception(e);
		}
	}
	

	@Override
	public ArrayList<Evento> obtenerEventos(String usuario) throws Exception {
		ArrayList<Evento> eventos = new ArrayList<Evento>();
		
		try{
			db.abrirConexion();
			String sql =  "select * from evento where Usuario_usuario like '"+usuario+"';";
			ResultSet rs = db.ejecutarConsulta(sql);
			while (rs.next()){
				Evento e = new Evento();
				e.setFecha(rs.getDate("fecha"));
				e.setContenido(rs.getString("contenido"));
				eventos.add(e);
			}
		}catch (Exception e){
			throw new Exception(e);
		}
		finally {
			try{
				
				db.cerrarConexion();
			}catch (Exception e1){
				throw new Exception (e1);
			}
		}
		return eventos;
	}

	@Override
	public boolean insertarEvento(Evento evento) throws Exception {
		boolean correcto = false;
		try{
			db.abrirConexion();
			String sql =  "insert into evento (fecha,contenido,usuario) values ("+
					 "'"+evento.getFecha()+"'"+","+
					 "'"+evento.getContenido()+"'"+","+
					 "'"+evento.getUsuario()+"'"+""+
					 ");";
			if (db.ejecutarUpdate(sql)>0){
				correcto = true;
				 db.commit();
			}
		 
			
		}catch (Exception e){
			System.out.println(e.getMessage());
			
		}
		finally {
			try{
				db.cerrarConexion();
			}catch (Exception e1){
				System.out.println(e1.getMessage());
				
			}
		}
		return correcto;
	}

}
