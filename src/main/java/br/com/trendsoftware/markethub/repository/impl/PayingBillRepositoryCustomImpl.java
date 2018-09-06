package br.com.trendsoftware.markethub.repository.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.mpconnect.model.ContaPagar;
import br.com.mpconnect.model.Persistente;
import br.com.trendsoftware.markethub.repository.PayingBillRepositoryCustom;

public class PayingBillRepositoryCustomImpl<P extends Persistente> extends JpaRepositoryCustomImpl<ContaPagar> implements PayingBillRepositoryCustom{

	@Override
	public Class<ContaPagar> getParameterizedType() {
		return ContaPagar.class;
	}
	
	@Override
	public List<ContaPagar> findAllByPagingAndFilters(int first, int max, Map<String, Object> filters)
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

		criteria.addOrder(Order.desc("dataVencimento"));

		return criteria.list();
	}
}
