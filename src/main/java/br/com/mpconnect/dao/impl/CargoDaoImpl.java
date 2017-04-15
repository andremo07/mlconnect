package br.com.mpconnect.dao.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.CargoDao;
import br.com.mpconnect.model.Cargo;


@Service("cargoDao")
public class CargoDaoImpl extends DaoCrudImpJpa<Cargo> implements CargoDao{

}
