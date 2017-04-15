package br.com.mpconnect.dao;

import java.util.List;
import java.util.Map;

import br.com.mpconnect.bo.ContaBo;
import br.com.mpconnect.model.ContaPagar;

public interface ContaPagarDao extends DaoCrud<ContaPagar>{
	
	public List<ContaBo> obterDespesasAgrupadas();
	public List<ContaPagar> recuperarContaPorNrTransacao(String nrTransacao);
	public List<ContaPagar> recuperaTodosPorIntervalo(int first, int max, Map<String,Object> filters) throws DaoException;


}
