package br.com.mpconnect.dao;

import java.util.List;

import br.com.mpconnect.model.Anuncio;

public interface AnuncioDao extends DaoCrud<Anuncio>{
	
	public List<String> recuperaIdsAnunciosExistentes(List<String> ids);

}
