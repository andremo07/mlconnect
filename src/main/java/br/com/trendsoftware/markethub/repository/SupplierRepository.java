package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Fornecedor;

public interface SupplierRepository extends JpaRepository<Fornecedor, Long> {

}
