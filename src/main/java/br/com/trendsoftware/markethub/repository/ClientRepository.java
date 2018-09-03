package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Cliente;

public interface ClientRepository extends JpaRepository<Cliente, String> {

	public Cliente findByNrDocumento(String nrDocumento);
	
}
