package br.com.mpconnect.dao;

import br.com.mpconnect.model.Vendedor;

public interface VendedorDao extends DaoCrud<Vendedor>{
	
	public Vendedor recuperarVendedorPorIdMl(String idMl);

}
