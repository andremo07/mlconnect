package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Venda;

public interface OrderRepository extends JpaRepository<Venda, String> {

}
