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
import br.com.mpconnect.dao.ContaPagarDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.model.ContaPagar;


@Service("contaPagarDao")
public class ContaPagarDaoImpl extends DaoCrudImpJpa<ContaPagar> implements ContaPagarDao{
	
	
	public List<ContaBo> obterDespesasAgrupadas(){
		String query = "select month(cp.dataBaixa),ccp.nome,sum(cp.valor) "+
						"from ContaPagar as cp "+
						"inner join cp.categoria as ccp "+
						"where cp.status = 'PAGO' "+
						"group by ccp.nome,month(cp.dataBaixa)";
		Query q = getEntityManager().createQuery(query);
		List results = q.getResultList();
		List<ContaBo> pagamentos = new ArrayList<ContaBo>();
		for(int index=0;index<results.size();index++){
			Object[] resultado = (Object[]) results.get(index);
			String categoria = (String) resultado[1];
			ContaBo pagamento = new ContaBo();
			pagamento.setCategoria(categoria);
			Map<Integer,Double> valoresMensais = null;
			if(pagamentos.contains(pagamento)){
				int i = pagamentos.indexOf(pagamento);
				pagamento = pagamentos.get(i);
				valoresMensais = pagamento.getValoresMensais();
			}
			else{
				valoresMensais = new HashMap<Integer, Double>();
				pagamentos.add(pagamento);
			}
			BigDecimal bd = new BigDecimal((Double) resultado[2]);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			valoresMensais.put((Integer)resultado[0],(Double) bd.doubleValue());
			pagamento.setValoresMensais(valoresMensais);
		}
		
		return pagamentos;
	}
	
	@Override
	public List<ContaPagar> recuperaTodosPorIntervalo(int first, int max, Map<String,Object> filters) throws DaoException {
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
	
	
	public List<ContaPagar> recuperarContaPorNrTransacao(String nrTransacao){
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("nrTransacao", nrTransacao);
			List<ContaPagar> contaPagar = recuperaPorParams("from ContaPagar cp where cp.nrTransacao =:nrTransacao", params);
			return contaPagar;
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
