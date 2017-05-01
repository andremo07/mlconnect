package br.com.mpconnect.dao;

import br.com.mpconnect.model.Usuario;

public interface UsuarioDao extends DaoCrud<Usuario>{
	
	public Usuario getUsuarioByLoginSenha(String login,String senha) throws DaoException;

}
