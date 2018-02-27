package br.com.mpconnect.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.MunicipioDao;
import br.com.mpconnect.model.Municipio;


@Service("municipioDao")
public class MunicipioDaoImpl extends DaoCrudImpJpa<Municipio> implements MunicipioDao{

	public Municipio findMunicipioByNameAndUf(String nmMunicipio,String uf){

		try{
			CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<Municipio> query = criteriaBuilder.createQuery(Municipio.class);
			Root<Municipio> from = query.from(Municipio.class);
			TypedQuery<Municipio> typedQuery = getEntityManager().createQuery(
					query.select(from)
					.where(criteriaBuilder.and(
							criteriaBuilder.equal(from.get("nmMunicipio"), nmMunicipio),		
							criteriaBuilder.equal(from.get("cdUf"), uf)
							)
							)
					);
			return typedQuery.getSingleResult();
		}
		catch(NoResultException e){
			return null;
		}
	}

}
