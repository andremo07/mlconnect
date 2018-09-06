package br.com.trendsoftware.markethub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.mpconnect.model.ContaReceber;

public interface ReceivingBillRepository extends JpaRepository<ContaReceber, Long>, ReceivingBillRepositoryCustom{
	
	@Query("select month(cr.dataBaixa),ccp.nome,sum(cr.valor) "+
			"from ContaReceber as cr "+
			"inner join cr.categoria as ccp "+
			"where cr.status = 'RECEBIDO' AND year(cr.dataBaixa)=:year "+
			"group by ccp.nome, month(cr.dataBaixa)")
	public List getRecebimentosAnuais(@Param("year") Integer year);
	
	@Query("select sum(cr.valor) as total "+
					"from ContaReceber as cr "+
					"inner join cr.categoria as ccp "+
					"where cr.status = 'RECEBIDO' AND year(cr.dataBaixa)=:year "+
					"group by year(cr.dataBaixa)")
	public double getTotalRecebimentosPorMes(@Param("year") Integer year);

}
