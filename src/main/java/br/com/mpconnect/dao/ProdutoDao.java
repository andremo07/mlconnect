package br.com.mpconnect.dao;

import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.model.Produto;

public interface ProdutoDao extends DaoCrud<Produto>{
	
	@Transactional
	public void alterar(Produto persistente) throws DaoException;

}
