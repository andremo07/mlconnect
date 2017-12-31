package br.com.mpconnect.dao.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.MunicipioDao;
import br.com.mpconnect.model.Municipio;


@Service("municipioDao")
public class MunicipioDaoImpl extends DaoCrudImpJpa<Municipio> implements MunicipioDao{

}
