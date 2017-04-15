package br.com.mpconnect.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import br.com.mpconnect.dao.CargoDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.manager.CargoManagerBo;
import br.com.mpconnect.model.Cargo;

@Service("cargoManager")
public class CargoManagerBoImpl implements CargoManagerBo{

	@Resource
	public CargoDao cargoDao;
	
	public List<Cargo> listarCargos() {
		
		try {
			return cargoDao.recuperaTodos();
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Cargo findCargoById(Long id){
		
		try {
			return cargoDao.recuperaUm(id);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
