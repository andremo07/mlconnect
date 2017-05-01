package br.com.mpconnect.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.UsuarioDao;
import br.com.mpconnect.model.Usuario;


@Service("usuarioDao")
public class UsuarioDaoImpl extends DaoCrudImpJpa<Usuario> implements UsuarioDao{

	public Usuario getUsuarioByLoginSenha(String login,String senha) throws DaoException{
		Usuario user = null;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("login", login);
		params.put("senha", senha);
		user = recuperaUmPorParams("from Usuario u where u.login=:login and u.senha=:senha",params);
		return user;
	}


}
