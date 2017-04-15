package br.com.mpconnect.dao.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.FuncionarioDao;
import br.com.mpconnect.model.Funcionario;


@Service("funcionarioDao")
public class FuncionarioDaoImpl extends DaoCrudImpJpa<Funcionario> implements FuncionarioDao{

}
