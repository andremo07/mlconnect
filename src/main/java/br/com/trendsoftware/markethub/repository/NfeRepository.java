package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.NfeConfig;

public interface NfeRepository extends JpaRepository<NfeConfig, Long> {

}
