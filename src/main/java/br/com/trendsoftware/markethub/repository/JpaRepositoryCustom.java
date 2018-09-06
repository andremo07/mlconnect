package br.com.trendsoftware.markethub.repository;

import java.util.List;
import java.util.Map;

import br.com.mpconnect.model.Persistente;

public interface JpaRepositoryCustom<P extends Persistente> {
	public Long count(Map<String, Object> filters);
	public List<P> findAllByPagingAndFilters(int first, int max, Map<String,Object> filters);
}
