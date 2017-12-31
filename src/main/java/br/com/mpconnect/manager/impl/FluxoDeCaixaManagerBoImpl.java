package br.com.mpconnect.manager.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.dao.CategoriaContaPagarDao;
import br.com.mpconnect.dao.CategoriaContaReceberDao;
import br.com.mpconnect.dao.ContaPagarDao;
import br.com.mpconnect.dao.ContaReceberDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.FornecedorDao;
import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.manager.FluxoDeCaixaManagerBo;
import br.com.mpconnect.ml.api.enums.StatusContaPagarEnum;
import br.com.mpconnect.ml.api.enums.StatusContaReceberEnum;
import br.com.mpconnect.ml.api.enums.TipoPagamentoEnum;
import br.com.mpconnect.model.CategoriaContaPagar;
import br.com.mpconnect.model.CategoriaContaReceber;
import br.com.mpconnect.model.ContaPagar;
import br.com.mpconnect.model.ContaReceber;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.Fornecedor;
import br.com.mpconnect.model.Pagamento;
import br.com.mpconnect.model.Venda;

@Service("fluxoDeCaixaManager")
public class FluxoDeCaixaManagerBoImpl implements FluxoDeCaixaManagerBo{

	@Resource
	public ContaPagarDao contaPagarDao;

	@Resource
	public ContaReceberDao contaReceberDao;

	@Resource
	public CategoriaContaPagarDao categoriaContaPagarDao;

	@Resource
	public CategoriaContaReceberDao categoriaContaReceberDao;

	@Resource
	public VendaDao vendaDao;

	@Resource
	public FornecedorDao fornecedorDao;

	@Override
	@Transactional
	public void gerarFluxoDeCaixaVendaMl(Venda venda) {

		try {
			CategoriaContaPagar categoriaComissao = categoriaContaPagarDao.recuperaUm(new Long(5));
			CategoriaContaPagar categoriaFrete = categoriaContaPagarDao.recuperaUm(new Long(7));
			CategoriaContaReceber categoriaVenda = categoriaContaReceberDao.recuperaUm(new Long(1));

			if(venda.getStatus().equals("paid")){

				List<Pagamento> pagamentos = venda.getPagamentos();
				double valorTotalPago=0.0;
				for(Pagamento pagamento: pagamentos)
					valorTotalPago = valorTotalPago + pagamento.getValorTransacao();

				BigDecimal bdPagamento = new BigDecimal(valorTotalPago);
				bdPagamento = bdPagamento.setScale(2, RoundingMode.HALF_UP);

				ContaReceber contaReceber = criarContaReceber(venda, categoriaVenda, bdPagamento.doubleValue());
				contaReceberDao.gravar(contaReceber);

				List<DetalheVenda> dvs = venda.getDetalhesVenda();
				double valorTotalComissao=0.0;
				for(DetalheVenda dv: dvs)
					valorTotalComissao = valorTotalComissao + dv.getTarifaVenda();

				BigDecimal bdComissao = new BigDecimal(valorTotalComissao);
				bdComissao = bdComissao.setScale(2, RoundingMode.HALF_UP);

				ContaPagar contaPagarComissao = criarContaPagar(venda, categoriaComissao, bdComissao.doubleValue());
				contaPagarDao.gravar(contaPagarComissao);

				if(venda.getEnvio() !=null && venda.getEnvio().getCusto()!=null){
					if(venda.getEnvio().getCusto()>0.0){
						if(venda.getEnvio().getModo()!= null && (venda.getEnvio().getModo().equals("pagseguro")||venda.getEnvio().getModo().equals("me2"))){
							ContaPagar contaPagarFrete = criarContaPagar(venda, categoriaFrete, venda.getEnvio().getCusto());
							contaPagarDao.gravar(contaPagarFrete);
						}
					}
				}	
			}
		}
		catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ContaPagar criarContaPagar(Venda venda,CategoriaContaPagar categoria, double valor) throws DaoException{
		ContaPagar contaPagar = new ContaPagar();
		contaPagar.setNrTransacao(venda.getId());

		Fornecedor fornecedor=null;
		if(venda.getOrigem().getNome().equals("ML")){
			fornecedor = fornecedorDao.recuperaUm(new Long(15780));
		}
		else{
			fornecedor = fornecedorDao.recuperaUm(new Long(15781));
		}

		contaPagar.setBeneficiario(fornecedor);
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


}
