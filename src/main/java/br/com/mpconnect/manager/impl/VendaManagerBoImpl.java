package br.com.mpconnect.manager.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.dao.AnuncioDao;
import br.com.mpconnect.dao.CategoriaContaPagarDao;
import br.com.mpconnect.dao.CategoriaContaReceberDao;
import br.com.mpconnect.dao.ClienteDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.EnvioDao;
import br.com.mpconnect.dao.OrigemDao;
import br.com.mpconnect.dao.ProdutoDao;
import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.dao.VendedorDao;
import br.com.mpconnect.manager.FluxoDeCaixaManagerBo;
import br.com.mpconnect.manager.VendaManagerBo;
import br.com.mpconnect.ml.api.ApiEnvios;
import br.com.mpconnect.ml.api.ApiProdutos;
import br.com.mpconnect.ml.api.ApiUsuario;
import br.com.mpconnect.ml.api.ApiVendas;
import br.com.mpconnect.ml.api.enums.TipoPessoaEnum;
import br.com.mpconnect.ml.data.AnuncioML;
import br.com.mpconnect.ml.data.ClienteML;
import br.com.mpconnect.ml.data.DetalheVendaML;
import br.com.mpconnect.ml.data.EnderecoML;
import br.com.mpconnect.ml.data.EnvioML;
import br.com.mpconnect.ml.data.PagamentoML;
import br.com.mpconnect.ml.data.UsuarioML;
import br.com.mpconnect.ml.data.VendaML;
import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Cliente;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.Envio;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Pagamento;
import br.com.mpconnect.model.Produto;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.model.Vendedor;
import br.com.mpconnect.utils.DateUtils;

@Service("vendasManager")
public class VendaManagerBoImpl implements VendaManagerBo, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6462524421141281130L;

	@Resource
	public AnuncioDao anuncioDao;

	@Autowired
	public ApiEnvios apiEnvios;

	@Autowired
	public ApiVendas apiVendas;

	@Autowired
	public ApiUsuario apiUsuario;

	@Autowired
	public ApiProdutos apiProdutos;

	@Resource
	public ClienteDao clienteDao;

	@Resource
	public VendedorDao vendedorDao;

	@Resource
	public EnvioDao envioDao;

	@Resource
	public VendaDao vendaDao;

	@Resource
	public ProdutoDao produtoDao;

	@Resource
	public OrigemDao origemDao;

	@Resource
	public CategoriaContaPagarDao categoriaContaPagarDao;

	@Resource
	public CategoriaContaReceberDao categoriaContaReceberDao;

	@Autowired
	public FluxoDeCaixaManagerBo fluxoDeCaixaManager;

	@Transactional
	public void cadastrarVenda(Venda venda){
		Venda vendaExistente =null;
		if(venda.getId()!=null)
			vendaExistente = recuperarVenda(venda.getId());

		//		if(vendaExistente!=null){
		//			int index =0;
		//			for(Pagamento pagamento :vendaExistente.getPagamentos()){
		//				venda.getPagamentos().get(index).setId(pagamento.getId());
		//				index++;
		//			}
		//
		//			index=0;
		//			for(DetalheVenda detalheVenda :vendaExistente.getDetalhesVenda()){
		//				venda.getDetalhesVenda().get(index).setId(detalheVenda.getId());
		//				index++;
		//			}
		//
		//			venda.getEnvio().setId(vendaExistente.getEnvio().getId());
		//			venda.getEnvio().setData(vendaExistente.getEnvio().getData());
		//
		//			venda.getCliente().setId(vendaExistente.getCliente().getId());
		//			venda.getVendedor().setId(vendaExistente.getVendedor().getId());
		//			atualizarVenda(venda);
		//		}
		//		else
		//			salvarVenda(venda);
	}


	@Transactional
	public void cadastrarVendaUnitaria(Venda venda, Produto produto){

		try {
			salvarVenda(venda);
			produto.setQuantidadeDisponivel(produto.getQuantidadeDisponivel()-1);
			produtoDao.merge(produto);
			fluxoDeCaixaManager.gerarFluxoDeCaixaVendaMl(venda);
		} catch (DaoException e) {
			e.printStackTrace();
		}		

	}

	@Transactional
	public void salvarVenda(Venda venda){

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			for(DetalheVenda dv: venda.getDetalhesVenda()){

				if(params.containsKey("idMl"))
					params.replace("idMl", dv.getAnuncio().getIdMl());
				else
					params.put("idMl", dv.getAnuncio().getIdMl());

				Anuncio anuncio = anuncioDao.recuperaUmPorParamsTrataNull("select a from Anuncio a where a.idMl =:idMl", params);
				if(anuncio==null){
					anuncio=dv.getAnuncio();
					anuncioDao.gravar(anuncio);
				}
				else{
					dv.getAnuncio().setId(anuncio.getId());
					dv.getAnuncio().setProdutos(anuncio.getProdutos());
					anuncioDao.merge(dv.getAnuncio());
				}
			}

			if(params.get("idMl")!=null)
				params.replace("idMl", venda.getVendedor().getIdMl());
			Vendedor vendedor = vendedorDao.recuperaUmPorParamsTrataNull("select v from Vendedor v where v.idMl =:idMl", params);
			if(vendedor==null){
				vendedor=venda.getVendedor();
				vendedorDao.gravar(vendedor);
			}
			venda.setVendedor(vendedor);

			if(params.get("idMl")!=null)
				params.replace("idMl", venda.getCliente().getIdMl());
			Cliente cliente = clienteDao.recuperaUmPorParamsTrataNull("select c from Cliente c where c.idMl =:idMl", params);
			if(cliente==null){
				cliente=venda.getCliente();
				clienteDao.gravar(cliente);
			}
			venda.setCliente(cliente);

			vendaDao.gravar(venda);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Transactional
	public void carregaVendasRecentes(){

		Set<VendaML> vendasMl = apiVendas.recuperaVendasPeriodo(apiUsuario.getIdUsuarioLogado(), DateUtils.adicionaDias(new Date(), -15), new Date());
		vendasMl = retornaVendasNaoExistentes(vendasMl);


		for(Iterator<VendaML> iterVendas = vendasMl.iterator();iterVendas.hasNext();){
			VendaML vendaMl = iterVendas.next();
			Venda venda = parseVendaMltoVenda(vendaMl);
			salvarVenda(venda);
			fluxoDeCaixaManager.gerarFluxoDeCaixaVendaMl(venda);
		}
	}

	public Long getMaxIdVenda(){

		Criteria criteria = vendaDao.getSession()
				.createCriteria(Venda.class)
				.setProjection(Projections.max("id"));
		String maxAge = (String)criteria.uniqueResult();
		return new Long(maxAge);

	}

	@Transactional
	public void atualizarVenda(Venda venda){

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			for(DetalheVenda dv: venda.getDetalhesVenda()){
				if(params.containsKey("idMl"))
					params.replace("idMl", dv.getAnuncio().getIdMl());
				else
					params.put("idMl", dv.getAnuncio().getIdMl());

				Anuncio anuncio = anuncioDao.recuperaUmPorParamsTrataNull("select a from Anuncio a where a.idMl =:idMl", params);
				if(anuncio==null){
					anuncio=dv.getAnuncio();
					anuncioDao.gravar(anuncio);
				}
				else{
					dv.getAnuncio().setId(anuncio.getId());
					dv.getAnuncio().setProdutos(anuncio.getProdutos());
					anuncioDao.merge(dv.getAnuncio());
				}
			}
			vendaDao.merge(venda);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	@Transactional
	public void atualizarCustosEnvios(){

		try {
			List<Venda> vendas = vendaDao.recuperaTodos();
			for(Venda venda: vendas){
				DetalheVenda detalheVenda = venda.getDetalhesVenda().get(0);
				Anuncio anuncio = detalheVenda.getAnuncio();
				Envio envio = venda.getEnvio();
				Vendedor vendedor = venda.getVendedor();
				VendaML vendaMl = apiVendas.retornaVendaPorId(venda.getId(), vendedor.getIdMl());
				PagamentoML pagamentoML = vendaMl.getPagamentos().get(0);
				Double custoEnvio = pagamentoML.getCustoEnvio();
				if(custoEnvio!=null){
					if(custoEnvio==0){
						Double custoEnvioAnuncio = apiEnvios.calculaValorEnvio(vendedor.getIdMl(),anuncio.getCategoria());
						envio.setCusto(custoEnvioAnuncio);
					}
					else
						envio.setCusto(0.0);
					envioDao.merge(envio);
				}
			}


		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void atualizarCustoEnvio(Venda venda){

		DetalheVenda detalheVenda = venda.getDetalhesVenda().get(0);
		Anuncio anuncio = detalheVenda.getAnuncio();
		Envio envio = venda.getEnvio();
		Vendedor vendedor = venda.getVendedor();
		if(envio.getCusto() != null && envio.getCusto()==0){
			Double custoEnvio = apiEnvios.calculaValorEnvio(vendedor.getIdMl(),anuncio.getCategoria());
			envio.setCusto(custoEnvio);
		}
		else
			envio.setCusto(0.0);
	}

	public Venda parseVendaMltoVenda(VendaML vendaMl){

		Venda venda = new Venda();
		try {

			venda.setId(vendaMl.getId());

			String dataVendaString = vendaMl.getData();
			Date dataVenda = DateUtils.getDataFormatada(dataVendaString, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			venda.setData(dataVenda);

			List<DetalheVenda> detalhesVenda = new ArrayList<DetalheVenda>();
			List<DetalheVendaML> detalhesVendaMl = vendaMl.getDetalhesVenda();
			for(DetalheVendaML detalheVendaML : detalhesVendaMl){

				detalheVendaML.getIdAnuncio();
				DetalheVenda detalheVenda = new DetalheVenda();
				detalheVenda.setQuantidade(detalheVendaML.getQuantidade());
				detalheVenda.setTarifaVenda(detalheVendaML.getTarifaVenda());

				AnuncioML anuncioMl = apiProdutos.getAnuncioPorId(detalheVendaML.getIdAnuncio());
				if(anuncioMl!=null){
					Anuncio anuncio = new Anuncio();
					anuncio.setDescricao(anuncioMl.getDescricao());
					anuncio.setCategoria(anuncioMl.getIdCategoria());
					anuncio.setIdMl(anuncioMl.getId());
					anuncio.setStatus(anuncioMl.getStatus());
					anuncio.setTitulo(anuncioMl.getTitulo());
					anuncio.setTipo(anuncioMl.getTipo().getNome());
					anuncio.setValor(anuncioMl.getValor());
					Produto produto = new Produto();
					produto.setNome(anuncioMl.getTitulo());
					produto.setQuantidadeDisponivel(10);
					Set<Produto> produtos = new HashSet<Produto>();
					produtos.add(produto);
					anuncio.setProdutos(produtos);	
					detalheVenda.setAnuncio(anuncio);
					//detalheVenda.setVenda(venda);
					detalhesVenda.add(detalheVenda);	
				}				
			}
			venda.setDetalhesVenda(detalhesVenda);

			List<Pagamento> pagamentos = new ArrayList<Pagamento>();
			Double valorTotalTransacao = 0.0;
			for(PagamentoML pagamentoMl : vendaMl.getPagamentos()){
				Pagamento pagamento = new Pagamento();
				pagamento.setIdML(pagamentoMl.getId());
				pagamento.setTipo(pagamentoMl.getTipoPagamento());
				pagamento.setTotalPago(pagamentoMl.getTotalPago());
				pagamento.setValorTransacao(pagamentoMl.getValorTransacao());
				pagamento.setNumeroParcelas(pagamentoMl.getNumeroParcelas());
				valorTotalTransacao = valorTotalTransacao+pagamentoMl.getValorTransacao();
				//pagamento.setVenda(venda);
				pagamentos.add(pagamento);
			}
			venda.setPagamentos(pagamentos);

			if(venda.getDetalhesVenda().get(0).getAnuncio().getTipo().equals("gold_pro")){
				BigDecimal bd = new BigDecimal(valorTotalTransacao*0.16);
				bd = bd.setScale(2, RoundingMode.HALF_UP);
				venda.getDetalhesVenda().get(0).setTarifaVenda(bd.doubleValue());
			}
			else{
				BigDecimal bd = new BigDecimal(valorTotalTransacao*0.11);
				bd = bd.setScale(2, RoundingMode.HALF_UP);
				venda.getDetalhesVenda().get(0).setTarifaVenda(bd.doubleValue());
			}

			EnvioML envioMl = vendaMl.getEnvio();
			Envio envio = null;
			if(envioMl!=null && !vendaMl.getPagamentos().isEmpty()){
				envio = new Envio();
				if(vendaMl.getPagamentos().get(0).getCustoEnvio()==0){
					Anuncio anuncio = detalhesVenda.get(0).getAnuncio();
					Double custoEnvio = apiEnvios.calculaValorEnvio(vendaMl.getUsuario().getId(),anuncio.getCategoria());
					envio.setCusto(custoEnvio);
				}
				else
					envio.setCusto(0.0);
				envio.setIdMl(envioMl.getId());
				envio.setMetodo(envioMl.getMetodoEnvio());
				envio.setModo(envioMl.getModoEnvio());
				envio.setTipo(envioMl.getTipoEnvio());
				
				if(envioMl.getEndereco()!=null){
					EnderecoML enderecoMl = envioMl.getEndereco();
					envio.setLogradouro(enderecoMl.getNomeRua());
					envio.setMunicipio(enderecoMl.getCidade());
					envio.setCep(enderecoMl.getCep());
					envio.setPais(enderecoMl.getPais());
					envio.setNumero(enderecoMl.getNumero());
					envio.setComplemento(enderecoMl.getComentario());
					envio.setUf(enderecoMl.getUfEstado());
					envio.setBairro(enderecoMl.getBairro());
				}	
			}
			venda.setEnvio(envio);

			ClienteML clienteML = vendaMl.getCliente();
			Cliente cliente = new Cliente();
			cliente.setNome(clienteML.getPrimeiroNome()+" "+clienteML.getUltimoNome());
			cliente.setNrDocumento(clienteML.getNumeroDocumento());
			cliente.setApelido(clienteML.getApelido());
			cliente.setIdMl(clienteML.getId());
			cliente.setEmail(clienteML.getEmail());
			cliente.setTelefone(clienteML.getTelefone());
			if(clienteML.getArea()!=null && !clienteML.getArea().equals("null"))
				if(clienteML.getTelefone()!=null)
					cliente.setTelefone(clienteML.getArea().trim()+" "+clienteML.getTelefone().trim());
			if(clienteML.getNumeroDocumento()!=null)
				if(clienteML.getNumeroDocumento().length()>11){
					cliente.setTipo(TipoPessoaEnum.JURIDICA.getValue());
					cliente.setTipoContribuinteIcms(9);
				}
				else{
					cliente.setTipo(TipoPessoaEnum.FISICA.getValue());
					cliente.setTipoContribuinteIcms(9);
				}
			else{
				cliente.setTipo(TipoPessoaEnum.FISICA.getValue());
				cliente.setTipoContribuinteIcms(9);
			}
			venda.setCliente(cliente);

			UsuarioML usuarioMl = vendaMl.getUsuario();
			Vendedor vendedor = new Vendedor();
			vendedor.setIdMl(usuarioMl.getId());
			vendedor.setApelido(usuarioMl.getApelido());
			venda.setVendedor(vendedor);
			
			//MUDAR PARA 1
			Origem origem = origemDao.recuperaUm(new Long(1));

			venda.setOrigem(origem);
			venda.setStatus(vendaMl.getStatus());
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return venda;

	}


	@Override
	public Venda recuperarVenda(String id) {
		try {
			Venda venda = vendaDao.recuperaUm(id);
			return venda;
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Set<VendaML> retornaVendasNaoExistentes(Set<VendaML> vendasMl){

		List<String> idsVendas = new ArrayList<String>();
		for(VendaML vendaMl : vendasMl)
			idsVendas.add(vendaMl.getId());

		List<String> idsVendasExistentes  = vendaDao.recuperaIdsVendasExistentes(idsVendas);
		idsVendas.removeAll(idsVendasExistentes);

		Set<VendaML> vendasNaoExistentes = new HashSet<VendaML>();
		for(VendaML vendaMl : vendasMl){
			String id = vendaMl.getId();
			if(idsVendas.contains(id))
				vendasNaoExistentes.add(vendaMl);
		}

		return vendasNaoExistentes;

	}

}
