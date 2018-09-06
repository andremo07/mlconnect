package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.AcessoMl;

public interface AccessRepository extends JpaRepository<AcessoMl, Long> {
	public AcessoMl findByClientId(Long clientId);
}
