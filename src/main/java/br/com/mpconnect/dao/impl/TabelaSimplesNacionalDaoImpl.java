package br.com.mpconnect.dao.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.TabelaSimplesNacionalDao;
import br.com.mpconnect.model.TabelaSimplesNacional;


@Service("tabelaSimpleNacionalDao")
public class TabelaSimplesNacionalDaoImpl extends DaoCrudImpJpa<TabelaSimplesNacional> implements TabelaSimplesNacionalDao{

}
