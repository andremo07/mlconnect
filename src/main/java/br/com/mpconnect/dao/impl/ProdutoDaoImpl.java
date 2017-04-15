package br.com.mpconnect.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.ProdutoDao;
import br.com.mpconnect.model.Produto;


@Service("produtoDao")
public class ProdutoDaoImpl extends DaoCrudImpJpa<Produto> implements ProdutoDao, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4161950402043397303L;

}
