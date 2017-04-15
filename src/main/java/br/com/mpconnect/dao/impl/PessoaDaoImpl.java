package br.com.mpconnect.dao.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.PessoaDao;
import br.com.mpconnect.model.Pessoa;


@Service("pessoaDao")
public class PessoaDaoImpl extends DaoCrudImpJpa<Pessoa> implements PessoaDao{

}
