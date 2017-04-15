package br.com.mpconnect.manager;

import java.util.List;

import br.com.mpconnect.model.Cargo;

public interface CargoManagerBo {
	
	public List<Cargo> listarCargos();
	public Cargo findCargoById(Long id);
}
