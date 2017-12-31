package br.com.mpconnect.dao.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.EnvioDao;
import br.com.mpconnect.model.Envio;


@Service("envioDao")
public class EnvioDaoImpl extends DaoCrudImpJpa<Envio> implements EnvioDao, Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7183819607214181309L;

	@Override
	public Envio recuperaEnvioPorIdMl(String idMl){
		Envio envio = null;
		try{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idMl", idMl);
			envio = recuperaUmPorParams("from Envio e where e.idMl=:idMl",params);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return envio;

	}
	
	@Override
	public List<Envio> listarEnvios(Date dtInicio, Date dtFim){
		
		try{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("dtInicio", dtInicio);
			params.put("dtFim", dtFim);
			List<Envio> envios = recuperaPorParams("from Envio e where e.data between :dtInicio and :dtFim order by e.data",params);
			return envios;
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
		
	}

}
