package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Anuncio;

public interface AdRepository extends JpaRepository<Anuncio, String> {

	public Anuncio findByIdMl(String partnerId);
	
}
