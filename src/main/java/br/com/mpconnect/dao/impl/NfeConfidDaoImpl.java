package br.com.mpconnect.dao.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.NfeConfigDao;
import br.com.mpconnect.model.NfeConfig;


@Service("nfeConfidDao")
public class NfeConfidDaoImpl extends DaoCrudImpJpa<NfeConfig> implements NfeConfigDao{

}
