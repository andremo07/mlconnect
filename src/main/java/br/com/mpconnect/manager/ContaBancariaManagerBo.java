package br.com.mpconnect.manager;

import java.util.List;

import br.com.mpconnect.bo.SaldoBo;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.model.ContaBancaria;

public interface ContaBancariaManagerBo {
	
	public void salvarConta(ContaBancaria conta) throws DaoException;
	public List<SaldoBo> retornaSaldosTotaisEmConta();
	
}
