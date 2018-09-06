package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.mpconnect.model.Saldo;

public interface BankAccountRepository extends JpaRepository<Saldo, Long>{

	@Query("select sum(s.valor) "+
			"from Saldo as s "+
			"group by month(s.data)")
	public Double getSaldoTotalEmConta();
}
