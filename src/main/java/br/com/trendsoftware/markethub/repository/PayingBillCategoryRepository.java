package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.CategoriaContaPagar;

public interface PayingBillCategoryRepository extends JpaRepository<CategoriaContaPagar, Long> {

}
