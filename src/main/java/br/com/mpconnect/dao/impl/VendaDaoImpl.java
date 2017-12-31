package br.com.mpconnect.dao.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.model.Venda;


@Service("vendaDao")
public class VendaDaoImpl extends DaoCrudImpJpa<Venda> implements VendaDao, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8695014859740008255L;
	
	@Override
	public List<String> recuperarTodosIdsVendas(){
		Query query = getEntityManager().createQuery("select v.id from Venda v");
		return query.getResultList();
	}
	
	@Override
	public List<String> recuperaIdsVendasExistentes(List<String> ids){
		Query query = getEntityManager().createQuery("select v.id from Venda v where v.id in (:ids)");
		query.setParameter("ids", ids);
		return query.getResultList();
	}
	
	@Override
	public List<Venda> recuperarVendasPorPeriodoSemNfe(Date dtIni,Date dtFinal){
		Query query = getEntityManager().createQuery("from Venda v where (v.data between :dtInicio and :dtFim) and v.nrNfe is null");
		query.setParameter("dtInicio", dtIni);
		query.setParameter("dtFim", dtFinal);
		return query.getResultList();
	}

}
