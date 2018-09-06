package br.com.trendsoftware.markethub.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.model.CategoriaContaPagar;
import br.com.mpconnect.model.CategoriaContaReceber;
import br.com.mpconnect.model.ContaPagar;
import br.com.mpconnect.model.ContaReceber;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.Fornecedor;
import br.com.mpconnect.model.Pagamento;
import br.com.mpconnect.model.StatusContaPagarEnum;
import br.com.mpconnect.model.StatusContaReceberEnum;
import br.com.mpconnect.model.TipoPagamentoEnum;
import br.com.mpconnect.model.Venda;
import br.com.trendsoftware.markethub.dto.ContaBo;
import br.com.trendsoftware.markethub.repository.BankAccountRepository;
import br.com.trendsoftware.markethub.repository.PayingBillCategoryRepository;
import br.com.trendsoftware.markethub.repository.PayingBillRepository;
import br.com.trendsoftware.markethub.repository.ReceivingBillCategoryRepository;
import br.com.trendsoftware.markethub.repository.ReceivingBillRepository;
import br.com.trendsoftware.markethub.repository.SupplierRepository;

@Service("fluxoCaixaBusiness")
public class FluxoCaixaBusiness {

	@Resource
	public BankAccountRepository bankAccountRepository;
	
	@Resource
	public PayingBillRepository payingBillRepository;

	@Resource
	public ReceivingBillRepository receivingBillRepository;

	@Resource
	public PayingBillCategoryRepository payingBillCategoryRepository;

	@Resource
	public ReceivingBillCategoryRepository receivingBillCategoryRepository;

	@Resource
	public SupplierRepository supplierRepository;

	@Transactional
	public void gerarFluxoDeCaixa(Venda venda) 
	{
		Optional<CategoriaContaPagar> resultQueryCategoriaContaPagar = payingBillCategoryRepository.findById(5L);
		CategoriaContaPagar categoriaComissao = resultQueryCategoriaContaPagar.get();

		resultQueryCategoriaContaPagar = payingBillCategoryRepository.findById(7L);
		CategoriaContaPagar categoriaFrete = resultQueryCategoriaContaPagar.get();

		Optional<CategoriaContaReceber> resultQueryCategoriaContaReceber = receivingBillCategoryRepository.findById(1L);
		CategoriaContaReceber categoriaVenda = resultQueryCategoriaContaReceber.get();

		if(venda.getStatus().equals("paid"))
		{
			List<Pagamento> pagamentos = venda.getPagamentos();
			double valorTotalPago=0.0;
			for(Pagamento pagamento: pagamentos)
				valorTotalPago = valorTotalPago + pagamento.getValorTransacao();

			BigDecimal bdPagamento = new BigDecimal(valorTotalPago);
			bdPagamento = bdPagamento.setScale(2, RoundingMode.HALF_UP);

			ContaReceber contaReceber = criarContaReceber(venda, categoriaVenda, bdPagamento.doubleValue());
			receivingBillRepository.save(contaReceber);

			List<DetalheVenda> dvs = venda.getDetalhesVenda();
			double valorTotalComissao=0.0;
			for(DetalheVenda dv: dvs)
				valorTotalComissao = valorTotalComissao + dv.getTarifaVenda();

			BigDecimal bdComissao = new BigDecimal(valorTotalComissao);
			bdComissao = bdComissao.setScale(2, RoundingMode.HALF_UP);

			ContaPagar contaPagarComissao = criarContaPagar(venda, categoriaComissao, bdComissao.doubleValue());
			payingBillRepository.save(contaPagarComissao);

			if(venda.getEnvio() !=null && venda.getEnvio().getCustoVendedor()!=null)
				if(venda.getEnvio().getCustoVendedor()>0.0)
					if(venda.getEnvio().getModo()!= null && (venda.getEnvio().getModo().equals("pagseguro")||venda.getEnvio().getModo().equals("me2")))
					{
						ContaPagar contaPagarFrete = criarContaPagar(venda, categoriaFrete, venda.getEnvio().getCustoVendedor());
						payingBillRepository.save(contaPagarFrete);
					}	
		}
	}

	public ContaPagar criarContaPagar(Venda venda,CategoriaContaPagar categoria, double valor){
		ContaPagar contaPagar = new ContaPagar();
		contaPagar.setNrTransacao(venda.getId());

		Optional<Fornecedor> result = null;
		if(venda.getOrigem().getId()==1L)
			result  = supplierRepository.findById(15780L);
		else
			result  = supplierRepository.findById(15781L);

		if(result.isPresent())
			contaPagar.setBeneficiario(result.get());

		contaPagar.setCategoria(categoria);
		contaPagar.setDataBaixa(venda.getData());
		contaPagar.setDataEmissao(venda.getData());
		contaPagar.setDataVencimento(venda.getData());
		contaPagar.setFormaPagamento(TipoPagamentoEnum.DEBITO.getValue());
		contaPagar.setStatus(StatusContaPagarEnum.PAGO.getValue());
		contaPagar.setValor(valor);
		contaPagar.setVendedor(venda.getVendedor());
		return contaPagar;
	}

	public ContaReceber criarContaReceber(Venda venda,CategoriaContaReceber categoria, double valor){
		ContaReceber contaReceber = new ContaReceber();
		contaReceber.setNrTransacao(venda.getId());
		contaReceber.setPagador(venda.getCliente());
		contaReceber.setCategoria(categoria);
		contaReceber.setDataBaixa(venda.getData());
		contaReceber.setDataEmissao(venda.getData());
		contaReceber.setDataVencimento(venda.getData());
		contaReceber.setFormaPagamento(TipoPagamentoEnum.DEBITO.getValue());
		contaReceber.setStatus(StatusContaReceberEnum.RECEBIDO.getValue());
		contaReceber.setValor(valor);
		contaReceber.setVendedor(venda.getVendedor());
		return contaReceber;
	}
	
	
	public List<ContaBo> obterRecebimentosAnuais(Integer ano)
	{
		List results = receivingBillRepository.getRecebimentosAnuais(ano);
		
		List<ContaBo> recebimentos = new ArrayList<ContaBo>();
		for(int index=0;index<results.size();index++){
			Object[] resultado = (Object[]) results.get(index);
			String categoria = (String) resultado[1];
			ContaBo recebimento = new ContaBo();
			recebimento.setCategoria(categoria);
			Map<Integer,Double> valoresMensais = null;
			if(recebimentos.contains(recebimento)){
				int i = recebimentos.indexOf(recebimento);
				recebimento = recebimentos.get(i);
				valoresMensais = recebimento.getValoresMensais();
			}
			else{
				valoresMensais = new HashMap<Integer, Double>();
				recebimentos.add(recebimento);
			}
			BigDecimal bd = new BigDecimal((Double) resultado[2]);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			valoresMensais.put((Integer)resultado[0],(Double) bd.doubleValue());
			recebimento.setValoresMensais(valoresMensais);
		}
		return recebimentos;
	}
	
	public Double obterTotalRecebimentosPorMes(Integer ano)
	{
		Double result = receivingBillRepository.getTotalRecebimentosPorMes(ano);
		return result==null? 0.0 : result;
	}
	
	public List<ContaBo> obterDespesasAnuais(Integer ano)
	{
		List results = payingBillRepository.getDespesasAnuais(ano);
		
		List<ContaBo> pagamentos = new ArrayList<ContaBo>();
		for(int index=0;index<results.size();index++){
			Object[] resultado = (Object[]) results.get(index);
			String categoria = (String) resultado[1];
			ContaBo pagamento = new ContaBo();
			pagamento.setCategoria(categoria);
			Map<Integer,Double> valoresMensais = null;
			if(pagamentos.contains(pagamento)){
				int i = pagamentos.indexOf(pagamento);
				pagamento = pagamentos.get(i);
				valoresMensais = pagamento.getValoresMensais();
			}
			else{
				valoresMensais = new HashMap<Integer, Double>();
				pagamentos.add(pagamento);
			}
			BigDecimal bd = new BigDecimal((Double) resultado[2]);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			valoresMensais.put((Integer)resultado[0],(Double) bd.doubleValue());
			pagamento.setValoresMensais(valoresMensais);
		}

		return pagamentos;
	}
	
	public Double obterTotalDespesasMes(Integer year)
	{
		Double result = payingBillRepository.getTotalDespesasMes(year);
		return result==null? 0.0 : result;
	}
	
	public Double obterSaldoTotalEmConta()
	{
		Double result = bankAccountRepository.getSaldoTotalEmConta();
		return result==null? 0.0 : result;
	}
}
