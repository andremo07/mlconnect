package br.com.trendsoftware.markethub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.mpconnect.model.ContaPagar;

public interface PayingBillRepository extends JpaRepository<ContaPagar, Long>, PayingBillRepositoryCustom{

	@Query("select month(cp.dataBaixa),ccp.nome,sum(cp.valor) "+
			"from ContaPagar as cp "+
			"inner join cp.categoria as ccp "+
			"where cp.status = 'PAGO' AND year(cp.dataBaixa)=:year "+
			"group by ccp.nome,month(cp.dataBaixa)")
	public List getDespesasAnuais(@Param("year") Integer year);


	@Query("select sum(cp.valor) "+
					"from ContaPagar as cp "+
					"inner join cp.categoria as ccp "+
					"where cp.status = 'PAGO' AND year(cp.dataBaixa)=:year "+
					"group by year(cp.dataBaixa)")
	public double getTotalDespesasMes(@Param("year") Integer year);

}
