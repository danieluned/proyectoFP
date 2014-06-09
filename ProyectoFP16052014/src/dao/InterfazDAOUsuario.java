package dao;


import beans.Usuario;

public interface InterfazDAOUsuario {
	public Usuario verificarUsuario(String usuario) throws Exception;
	public boolean insertarUsuario(Usuario usuario) throws Exception;
	
}
