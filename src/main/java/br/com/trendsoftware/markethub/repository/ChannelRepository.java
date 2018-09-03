package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Origem;

public interface ChannelRepository extends JpaRepository<Origem, Long> {

}
