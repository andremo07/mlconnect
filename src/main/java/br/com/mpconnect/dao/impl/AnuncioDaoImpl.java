package br.com.mpconnect.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.AnuncioDao;
import br.com.mpconnect.model.Anuncio;


@Service("anuncioDao")
public class AnuncioDaoImpl extends DaoCrudImpJpa<Anuncio> implements AnuncioDao, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8619762730628567105L;
	
	@Override
	public List<String> recuperaIdsAnunciosExistentes(List<String> ids){
		Query query = getEntityManager().createQuery("select a.idMl from Anuncio a where a.idMl in (:ids)");
		query.setParameter("ids", ids);
		return query.getResultList();
	}
	
	@Override
	public Anuncio recuperarAnuncioPorIdMl(String idMl){

		Criteria criteria = getSession().createCriteria(oClass)
				.add(Restrictions.eq("idMl", idMl));

		return (Anuncio) criteria.uniqueResult();

	}

}
