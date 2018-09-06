package br.com.trendsoftware.markethub.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Envio;

public interface ShippingRepository extends JpaRepository<Envio, Long> {
	public List<Envio> findByDataBetween(Date startDate, Date endDate);
}
