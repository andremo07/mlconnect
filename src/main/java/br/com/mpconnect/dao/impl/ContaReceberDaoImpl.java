package br.com.mpconnect.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import br.com.mpconnect.bo.ContaBo;
import br.com.mpconnect.dao.ContaReceberDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.model.ContaReceber;


@Service("contaReceberDao")
public class ContaReceberDaoImpl extends DaoCrudImpJpa<ContaReceber> implements ContaReceberDao{

	@Override
	public List<ContaBo> obterRecebimentosAgrupados(Integer ano){
		String query = "select month(cr.dataBaixa),ccp.nome,sum(cr.valor) "+
				"from ContaReceber as cr "+
				"inner join cr.categoria as ccp "+
				"where cr.status = 'RECEBIDO' AND year(cr.dataBaixa)=:year "+
				"group by ccp.nome, month(cr.dataBaixa)";
		
		Query q = getEntityManager().createQuery(query);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("year", ano);
		
		for (String chave : params.keySet()) {
			q.setParameter(chave, params.get(chave));
		}
		
		List results = q.getResultList();
		List<ContaBo> recebimentos = new ArrayList<ContaBo>();
		for(int index=0;index<results.size();index++){
			Object[] resultado = (Object[]) results.get(index);
			String categoria = (String) resultado[1];
			ContaBo recebimento = new ContaBo();
			recebimento.setCategoria(categoria);
			Map<Integer,Double> valoresMensais = null;
			if(recebimentos.contains(recebimento)){
				int i = recebimentos.indexOf(recebimento);
				recebimento = recebimentos.get(i);
				valoresMensais = recebimento.getValoresMensais();
			}
			else{
				valoresMensais = new HashMap<Integer, Double>();
				recebimentos.add(recebimento);
			}
			BigDecimal bd = new BigDecimal((Double) resultado[2]);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			valoresMensais.put((Integer)resultado[0],(Double) bd.doubleValue());
			recebimento.setValoresMensais(valoresMensais);
		}
		return recebimentos;
	}

	@Override
	public List<ContaReceber> recuperaTodosPorIntervalo(int first, int max, Map<String,Object> filters) throws DaoException {
		String msg = new String("Erro ao realizar o recuperar todos no DAO.");
		try {

			Criteria criteria = getSession().createCriteria(oClass)
					.setFirstResult(first).setMaxResults(max);

			for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
				String filterProperty = it.next();
				Object filterValue = filters.get(filterProperty);
				if(!NumberUtils.isNumber((String) filterValue))
					criteria.add(Restrictions.like(filterProperty, filterValue.toString(), MatchMode.ANYWHERE));
				else
					criteria.add(Restrictions.eq(filterProperty, filterValue.toString()));
			}
			
			criteria.addOrder(Order.desc("dataVencimento"));
			
			return criteria.list();
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e.getCause(), null);
		}
	}
	
	
	@Override
	public List<ContaReceber> recuperarContaPorNrTransacao(String nrTransacao){
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("nrTransacao", nrTransacao);
			List<ContaReceber> contasReceber = recuperaPorParams("from ContaReceber cr where cr.nrTransacao =:nrTransacao", params);
			return contasReceber;
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
