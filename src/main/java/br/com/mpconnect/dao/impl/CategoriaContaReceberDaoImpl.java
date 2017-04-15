package br.com.mpconnect.dao.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.CategoriaContaReceberDao;
import br.com.mpconnect.model.CategoriaContaReceber;


@Service("categoriaContaReceberDao")
public class CategoriaContaReceberDaoImpl extends DaoCrudImpJpa<CategoriaContaReceber> implements CategoriaContaReceberDao{

}
