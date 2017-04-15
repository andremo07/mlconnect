package br.com.mpconnect.dao;

import br.com.mpconnect.model.Cliente;

public interface ClienteDao extends DaoCrud<Cliente>{
	
	public Cliente recuperaClientePorIdMl(String idMl);

}
