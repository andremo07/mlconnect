package br.com.mpconnect.dao.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.FornecedorDao;
import br.com.mpconnect.model.Fornecedor;


@Service("fornecedorDao")
public class FornecedorDaoImpl extends DaoCrudImpJpa<Fornecedor> implements FornecedorDao{

}
