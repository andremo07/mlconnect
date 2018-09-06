package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Produto;

public interface ProductRepository extends JpaRepository<Produto, Long>, ProductRepositoryCustom {
	public Produto findBySku(String sku);
}
