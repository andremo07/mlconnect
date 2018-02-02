package br.com.mpconnect.dao.impl;

import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.VendedorDao;
import br.com.mpconnect.model.Vendedor;


@Service("vendedorDao")
public class VendedorDaoImpl extends DaoCrudImpJpa<Vendedor> implements VendedorDao, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2930271449581494168L;

	@Override
	public Vendedor recuperarVendedorPorIdMl(String idMl){

		Criteria criteria = getSession().createCriteria(oClass)
				.add(Restrictions.eq("idMl", idMl));

		return (Vendedor) criteria.uniqueResult();

	}

}
