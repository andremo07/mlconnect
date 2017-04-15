package br.com.mpconnect.dao.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.OrigemDao;
import br.com.mpconnect.model.Origem;


@Service("origemDao")
public class OrigemDaoImpl extends DaoCrudImpJpa<Origem> implements OrigemDao{

}
