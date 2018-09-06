package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Pessoa;

public interface PersonRepository extends JpaRepository<Pessoa, Long> {

}
