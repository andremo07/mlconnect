package br.com.mpconnect.dao;

import br.com.mpconnect.model.Municipio;

public interface MunicipioDao extends DaoCrud<Municipio>{
	
	public Municipio findMunicipioByNameAndUf(String nmMunicipio,String uf);

}
