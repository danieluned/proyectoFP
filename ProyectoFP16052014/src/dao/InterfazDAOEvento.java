package dao;


import java.util.ArrayList;

import beans.Usuario;
import beans.Evento;
public interface InterfazDAOEvento {
	public ArrayList<Evento> obtenerEventos(String usuario) throws Exception;
	public boolean insertarEvento(Evento evento) throws Exception;
	
}
