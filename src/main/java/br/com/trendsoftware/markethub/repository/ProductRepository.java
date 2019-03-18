package br.com.trendsoftware.markethub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Produto;

public interface ProductRepository extends JpaRepository<Produto, Long>, ProductRepositoryCustom {
	public List<Produto> findDistinctBySku(String sku);
}
