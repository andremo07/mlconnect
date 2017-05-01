package br.com.mpconnect.manager;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.model.AcessoMl;


public interface AcessoManagerBo {
	
	public void conectarMl();
	public AcessoMl recuperarUltimoAcesso();
	
}
