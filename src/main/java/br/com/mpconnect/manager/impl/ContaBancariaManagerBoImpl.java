package br.com.mpconnect.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import br.com.mpconnect.bo.SaldoBo;
import br.com.mpconnect.dao.ContaBancariaDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.manager.ContaBancariaManagerBo;
import br.com.mpconnect.model.ContaBancaria;

@Service("contaBancariaManager")
public class ContaBancariaManagerBoImpl implements ContaBancariaManagerBo{

	@Resource
	public ContaBancariaDao contaDao;

	@Override
	public void salvarConta(ContaBancaria conta) throws DaoException {
		
		contaDao.gravar(conta);
		
	}
	
	@Override
	public List<SaldoBo> retornaSaldosTotaisEmConta(int ano){
		
		return contaDao.recuperaSaldosTotaisEmConta(ano);
		
	}
	
	
}
