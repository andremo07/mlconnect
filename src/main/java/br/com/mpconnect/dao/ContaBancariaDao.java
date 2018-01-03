package br.com.mpconnect.dao;

import java.util.List;

import br.com.mpconnect.bo.SaldoBo;
import br.com.mpconnect.model.ContaBancaria;

public interface ContaBancariaDao extends DaoCrud<ContaBancaria>{
	
	public List<SaldoBo> recuperaSaldosTotaisEmConta();
}
