package br.com.mpconnect.dao;

import br.com.mpconnect.model.AcessoMl;

public interface AcessoMlDao extends DaoCrud<AcessoMl>{
	
	public AcessoMl recuperarUltimo() throws DaoException;

}
