package br.com.mpconnect.dao.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.CategoriaContaPagarDao;
import br.com.mpconnect.model.CategoriaContaPagar;


@Service("categoriaContaPagarDao")
public class CategoriaContaPagarDaoImpl extends DaoCrudImpJpa<CategoriaContaPagar> implements CategoriaContaPagarDao{

}
