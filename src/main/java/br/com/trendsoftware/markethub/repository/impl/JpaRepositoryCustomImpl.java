package br.com.trendsoftware.markethub.repository.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.mpconnect.model.Persistente;
import br.com.trendsoftware.markethub.repository.JpaRepositoryCustom;

public abstract class JpaRepositoryCustomImpl<P extends Persistente> implements JpaRepositoryCustom<P> {
	
    @PersistenceContext
    EntityManager entityManager;
        
    public abstract Class<P> getParameterizedType();
    
	protected EntityManager getEntityManager() {
		if (entityManager == null)
			throw new IllegalStateException(
					"Contexto de persistencia est� nulo !!!");
		return entityManager;
	}

	public Session getSession() {
		return (Session) this.getEntityManager().getDelegate();
	}

	@Override
	public Long count(Map<String, Object> filters) 
	{
		Criteria criteria = getSession().createCriteria(getParameterizedType()).setProjection(Projections.rowCount());

		for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
			String filterProperty = it.next();
			Object filterValue = filters.get(filterProperty);
			if(!NumberUtils.isNumber((String) filterValue))
				criteria.add(Restrictions.like(filterProperty, filterValue.toString(), MatchMode.ANYWHERE));
			else
				criteria.add(Restrictions.eq(filterProperty, filterValue.toString()));

		}

		return (Long) criteria.uniqueResult();
	}

	@Override
	public List<P> findAllByPagingAndFilters(int first, int max, Map<String, Object> filters)
	{
		Criteria criteria = getSession().createCriteria(getParameterizedType())
				.setFirstResult(first).setMaxResults(max);

		for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
			String filterProperty = it.next();
			Object filterValue = filters.get(filterProperty);
			if(!NumberUtils.isNumber((String) filterValue))
				criteria.add(Restrictions.like(filterProperty, filterValue.toString(), MatchMode.ANYWHERE));
			else
				criteria.add(Restrictions.eq(filterProperty, filterValue.toString()));
		}

		return criteria.list();
	}
}
