package br.com.mpconnect.dao.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.ClienteDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.model.Cliente;


@Service("clienteDao")
public class ClienteDaoImpl extends DaoCrudImpJpa<Cliente> implements ClienteDao, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2187725238689881891L;
	
	@Override
	public Cliente recuperaClientePorIdMl(String idMl){
		Cliente cliente = null;
		try{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idMl", idMl);
			cliente = recuperaUmPorParams("from Cliente c where c.idMl=:idMl",params);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return cliente;
	}

}
