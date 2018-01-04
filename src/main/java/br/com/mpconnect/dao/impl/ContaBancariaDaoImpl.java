package br.com.mpconnect.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import br.com.mpconnect.bo.SaldoBo;
import br.com.mpconnect.dao.ContaBancariaDao;
import br.com.mpconnect.model.ContaBancaria;


@Service("contaBancariaDao")
public class ContaBancariaDaoImpl extends DaoCrudImpJpa<ContaBancaria> implements ContaBancariaDao{

	@Override
	public List<SaldoBo> recuperaSaldosTotaisEmConta(){

		String query = "select month(s.data), sum(s.valor) "+
				"from Saldo as s "+
				"group by month(s.data)";

		Query q = getEntityManager().createQuery(query);

		List results = q.getResultList();
		if(results!=null && !results.isEmpty()){
			List<SaldoBo> saldos = new ArrayList<SaldoBo>();
			for(int index=0;index<results.size();index++){
				Object[] resultado = (Object[]) results.get(index);
				int mes = (Integer) resultado[0];
				double valor = (Double) resultado[1];
				BigDecimal bd = new BigDecimal(valor);
				bd = bd.setScale(2, RoundingMode.HALF_UP);
				SaldoBo saldo = new SaldoBo(mes,bd.doubleValue());
				saldos.add(saldo);
			}
			return saldos;
		}
		else
			return null;
	}

	@Override
	public Double recuperaSaldoTotalEmConta(){
		try{
			String query = "select sum(s.valor) "+
					"from Saldo as s "+
					"group by month(s.data)";

			Query q = getEntityManager().createQuery(query);

			return (Double) q.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
	}

}
