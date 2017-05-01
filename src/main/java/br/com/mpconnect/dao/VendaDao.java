package br.com.mpconnect.dao;

import java.util.Date;
import java.util.List;

import br.com.mpconnect.model.Venda;

public interface VendaDao extends DaoCrud<Venda>{
	
	public List<String> recuperarTodosIdsVendas();
	public List<String> recuperaIdsVendasExistentes(List<String> ids);
	public List<Venda> recuperarVendasPorPeriodoSemNfe(Date dtIni,Date dtFinal);
	
}
