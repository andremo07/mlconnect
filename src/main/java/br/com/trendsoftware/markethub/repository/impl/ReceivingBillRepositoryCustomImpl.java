package br.com.trendsoftware.markethub.repository.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.mpconnect.model.ContaReceber;
import br.com.mpconnect.model.Persistente;
import br.com.trendsoftware.markethub.repository.ReceivingBillRepositoryCustom;

public class ReceivingBillRepositoryCustomImpl<P extends Persistente> extends JpaRepositoryCustomImpl<ContaReceber> implements ReceivingBillRepositoryCustom{

	@Override
	public Class<ContaReceber> getParameterizedType() {
		return ContaReceber.class;
	}
	
	@Override
	public List<ContaReceber> findAllByPagingAndFilters(int first, int max, Map<String, Object> filters)
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
