package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Vendedor;

public interface SellerRepository extends JpaRepository<Vendedor, Long> {
	
	public Vendedor findByCnpj(String cnpj);	
	
}
