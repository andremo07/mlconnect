package br.com.mpconnect.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.AcessoMlDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.model.AcessoMl;


@Service("acessoDao")
public class AcessoMlDaoImpl extends DaoCrudImpJpa<AcessoMl> implements AcessoMlDao, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4125606391551235642L;
	
	public AcessoMl recuperarUltimo() throws DaoException{
		String msg = new String("Erro ao realizar o merge no DAO.");
		try {
			AcessoMl acessoMl = (AcessoMl) this.getSession().createCriteria(oClass).uniqueResult();
			return acessoMl;
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e.getCause(), null);
		}
	}

}
