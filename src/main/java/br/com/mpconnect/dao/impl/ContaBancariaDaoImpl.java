package br.com.mpconnect.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Service;

import br.com.mpconnect.bo.SaldoBo;
import br.com.mpconnect.dao.ContaBancariaDao;
import br.com.mpconnect.model.ContaBancaria;


@Service("contaBancariaDao")
public class ContaBancariaDaoImpl extends DaoCrudImpJpa<ContaBancaria> implements ContaBancariaDao{

	public List<SaldoBo> recuperaSaldosTotaisEmConta(int ano){

		String query = "select month(s.data), sum(s.valor) "+
				"from Saldo as s "+
				"where year(s.data)=:year "+
				"group by month(s.data)";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("year", ano);
		
		Query q = getEntityManager().createQuery(query);
		
		for (String chave : params.keySet()) {
			q.setParameter(chave, params.get(chave));
		}

		List results = q.getResultList();
		if(results!=null && !results.isEmpty()){
			List<SaldoBo> saldos = new ArrayList<SaldoBo>();
			for(int index=0;index<results.size();index++){
				Object[] resultado = (Object[]) results.get(index);
				int mes = (Integer) resultado[0];
				double valor = (Double) resultado[1];
				BigDecimal bd = new BigDecimal((Double) valor);
				bd = bd.setScale(2, RoundingMode.HALF_UP);
				SaldoBo saldo = new SaldoBo();
				saldo.setMes(mes);
				saldo.setValor(bd.doubleValue());
				saldos.add(saldo);
			}
			return saldos;
		}
		else
			return null;
	}

}
