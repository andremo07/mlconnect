package br.com.mpconnect.dao;

import java.util.List;
import java.util.Map;

import br.com.mpconnect.bo.ContaBo;
import br.com.mpconnect.model.ContaReceber;

public interface ContaReceberDao extends DaoCrud<ContaReceber>{
	
	public List<ContaBo> obterRecebimentosAgrupados();
	public List<ContaReceber> recuperarContaPorNrTransacao(String nrTransacao);
	public List<ContaReceber> recuperaTodosPorIntervalo(int first, int max, Map<String,Object> filters) throws DaoException;

}
