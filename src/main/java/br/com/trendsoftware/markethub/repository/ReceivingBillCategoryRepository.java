package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.CategoriaContaReceber;

public interface ReceivingBillCategoryRepository extends JpaRepository<CategoriaContaReceber, Long> {

}
