package br.com.mpconnect.manager.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fincatto.nfe310.classes.nota.NFNotaProcessada;

import br.com.mpconnect.dao.AcessoMlDao;
import br.com.mpconnect.dao.AnuncioDao;
import br.com.mpconnect.dao.ClienteDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.MunicipioDao;
import br.com.mpconnect.dao.NfeConfigDao;
import br.com.mpconnect.dao.OrigemDao;
import br.com.mpconnect.dao.ProdutoDao;
import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.dao.VendedorDao;
import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.exception.BusinessProviderException;
import br.com.mpconnect.file.utils.PdfUtils;
import br.com.mpconnect.holder.MeliConfigurationHolder;
import br.com.mpconnect.manager.FluxoDeCaixaManagerBo;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.ml.data.parser.MlParser;
import br.com.mpconnect.ml.dto.VendaML;
import br.com.mpconnect.model.AcessoMl;
import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Cliente;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.NfeConfig;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Produto;
import br.com.mpconnect.model.Usuario;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.model.Vendedor;
import br.com.mpconnect.provider.NFeProvider;
import br.com.mpconnect.provider.exception.NfeProviderException;
import br.com.mpconnect.util.DateUtils;
import br.com.mpconnect.util.ExceptionUtil;
import br.com.trendsoftware.mlProvider.dataprovider.OrderProvider;
import br.com.trendsoftware.mlProvider.dataprovider.ShippingProvider;
import br.com.trendsoftware.mlProvider.dataprovider.UserProvider;
import br.com.trendsoftware.mlProvider.dto.Order;
import br.com.trendsoftware.mlProvider.dto.OrderList;
import br.com.trendsoftware.mlProvider.dto.OrderStatus;
import br.com.trendsoftware.mlProvider.dto.ShippingStatus;
import br.com.trendsoftware.mlProvider.dto.ShippingSubStatus;
import br.com.trendsoftware.mlProvider.dto.UserCredencials;
import br.com.trendsoftware.mlProvider.response.Response;
import br.com.trendsoftware.restProvider.exception.ProviderException;

@Service("orderBusiness")
public class OrderBusinessImpl extends MarketHubBusiness implements OrderBusiness, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6462524421141281130L;

	@Resource
	public AnuncioDao anuncioDao;

	@Resource
	public NfeConfigDao nfeConfidDao;

	@Resource
	public MunicipioDao munDao;

	@Resource
	public ClienteDao clienteDao;

	@Resource
	public VendedorDao vendedorDao;

	@Resource
	public AcessoMlDao acessoDao;

	@Resource
	public VendaDao vendaDao;

	@Resource
	public ProdutoDao produtoDao;

	@Resource
	public OrigemDao origemDao;

	@Autowired
	public FluxoDeCaixaManagerBo fluxoDeCaixaManager;

	@Autowired
	private UserProvider userProvider;

	@Autowired
	private OrderProvider orderProvider;

	@Autowired
	private ShippingProvider shippingProvider;

	@Autowired
	private NFeProvider nfeProvider;

	@PostConstruct
	public void init(){
		getUserProvider().setLogger(logger);
		getOrderProvider().setLogger(logger);
		getShippingProvider().setLogger(logger);
		getNfeProvider().setLogger(logger);
	}

	@Override
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

	@Override
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

	@Override
	@Transactional
	public void loadOrdersByDate(Date fromDate, Date toDate) throws BusinessException{

		getLogger().debug("carregando vendas recentes");

		try {

			Usuario usuario = getSessionUserLogin();

			String userId = usuario.getAcessoMercadoLivre().getIdMl();
			String accessToken = usuario.getAcessoMercadoLivre().getAccessToken();

			int offset = 0;
			Response response = orderProvider.listOrdersByDate(userId, fromDate, toDate, OrderStatus.PAID, offset, 10, accessToken);
			OrderList orderList = (OrderList) response.getData();

			List<Order> orders = new ArrayList<Order>();

			while(orderList.getPaging().getTotal() > orders.size()){
				orders.addAll(orderList.getOrders());				
				offset=offset+10;
				response = orderProvider.listOrdersByDate(userId, fromDate, toDate, OrderStatus.PAID, offset,10, accessToken);
				orderList = (OrderList) response.getData();
			}
			orders = retornaVendasNaoExistentes(orders);

			Origem origem = origemDao.recuperaUm(new Long(1));

			for(Order order: orders){
				Venda venda = MlParser.parseOrder(order);
				venda.setOrigem(origem);
				salvarVenda(venda);
				fluxoDeCaixaManager.gerarFluxoDeCaixaVendaMl(venda);
			}

		} catch (ProviderException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("msisdn: %s - message: %s", e.getCode(), e.getCodeMessage());
			throw new BusinessProviderException(exception);
		} catch (DaoException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("");
			throw new BusinessException(exception);
		}
	}

	public FileInputStream generateNfeFileStream(List<Venda> vendas, String pathName) throws BusinessException
	{
		getLogger().debug(String.format("iniciando geração de nfe"));
		
		try {
			List<InputStream> nfesInputStreams = generateOrderNfes(vendas);
			File nfeFilePdf = new File(pathName);
			nfeFilePdf.createNewFile();
			PdfUtils.merge(nfesInputStreams,nfeFilePdf);
			
			getLogger().debug(String.format("geração de nfe finalizada")); 
			
			return new FileInputStream(nfeFilePdf);
		} catch (IOException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("FILE_MANIPULATION_ERROR");
			throw new BusinessException(exception);
		}
	}

	public void saveOrder(Venda venda) throws BusinessException
	{
		Origem origem = new Origem();
		origem.setId(1L);
		venda.setOrigem(origem);
		salvarVenda(venda);
		fluxoDeCaixaManager.gerarFluxoDeCaixaVendaMl(venda);
	}

	@Override
	@Transactional
	public void save(String userId, String orderId) throws BusinessException
	{
		try {
			AcessoMl acessoMl = acessoDao.recuperarUltimo();

			//MUDAR PARA BUSCA NA TABELA PELA COLUNA USER_ID
			Response response = userProvider.login(MeliConfigurationHolder.getInstance().getClientId().toString(), MeliConfigurationHolder.getInstance().getClientSecret(), acessoMl.getRefreshToken());
			UserCredencials token = (UserCredencials) response.getData();

			response = orderProvider.searchOrderById(userId,orderId, token.getAccessToken());
			Order order = (Order) response.getData();

			Venda venda = MlParser.parseOrder(order);
			if(vendaDao.recuperaUm(venda.getId())==null)
				saveOrder(venda);

		} catch (DaoException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("");
			throw new BusinessException(exception);
		} catch (ProviderException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("");
			throw new BusinessProviderException(exception);
		}

	}

	@Override
	public List<Venda> listOrdersByShippingStatus(ShippingStatus shippingStatus, ShippingSubStatus shippingSubStatus) throws BusinessException{

		getLogger().debug("carregando vendas com etiquetas para imprimir");

		try {

			Usuario usuario = getSessionUserLogin();

			String userId = usuario.getAcessoMercadoLivre().getIdMl();
			String accessToken = usuario.getAcessoMercadoLivre().getAccessToken();

			int offset = 0;
			//Response response = orderProvider.listOrdersByShippingStatus(userId, shippingStatus, shippingSubStatus, offset, 10, accessToken);
			//MUDAR FUTURAMENTE
			Response response = orderProvider.listOrdersByDate(userId, DateUtils.adicionaDias(new Date(), -5), DateUtils.adicionaDias(new Date(), 1), OrderStatus.PAID, offset, 10, accessToken);
			OrderList orderList = (OrderList) response.getData();

			List<Order> orders = new ArrayList<Order>();

			while(!orderList.getOrders().isEmpty()){
				orders.addAll(orderList.getOrders());				
				offset=offset+10;
				//response = orderProvider.listOrdersByShippingStatus(userId, shippingStatus, shippingSubStatus, offset, 10, accessToken);
				//MUDAR FUTURAMENTE
				response = orderProvider.listOrdersByDate(userId, DateUtils.adicionaDias(new Date(), -5), DateUtils.adicionaDias(new Date(), 1), OrderStatus.PAID, offset, 10, accessToken);
				orderList = (OrderList) response.getData();
			}

			List<Venda> vendas = new ArrayList<Venda>();
			for(Order order: orders){
				if(shippingStatus.equals(ShippingStatus.lookup(order.getShipping().getStatus())) && 
						shippingSubStatus.equals(ShippingSubStatus.lookup(order.getShipping().getSubstatus()))){
					Venda venda = MlParser.parseOrder(order);
					vendas.add(venda);
				}
			}

			getLogger().debug("foram carregadas "+vendas.size()+" vendas");

			return vendas;

		} catch (ProviderException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("msisdn: %s - message: %s", e.getCode(), e.getCodeMessage());
			throw new BusinessException(exception);
		}

	}

	public Venda listOrderById(String id) throws BusinessException{

		getLogger().debug("carregando venda"+ id);

		try {

			Usuario usuario = getSessionUserLogin();

			String accessToken = usuario.getAcessoMercadoLivre().getAccessToken();

			Response response = orderProvider.searchOrderById("",id, accessToken);
			Order order = (Order) response.getData();

			Venda venda = MlParser.parseOrder(order);

			return venda;

		} catch (ProviderException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("msisdn: %s - message: %s", e.getCode(), e.getCodeMessage());
			throw new BusinessException(exception);
		}
	}

	@Override
	public Long getMaxIdVenda(){

		Criteria criteria = vendaDao.getSession()
				.createCriteria(Venda.class)
				.setProjection(Projections.max("id"));
		String maxAge = (String)criteria.uniqueResult();
		return new Long(maxAge);

	}

	@Override
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

	@Override
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

	public List<Order> retornaVendasNaoExistentes(List<Order> vendas){

		List<String> idsVendas = new ArrayList<String>();
		for(Order vendaMl : vendas)
			idsVendas.add(vendaMl.getId().toString());

		List<String> idsVendasExistentes  = vendaDao.recuperaIdsVendasExistentes(idsVendas);
		idsVendas.removeAll(idsVendasExistentes);

		List<Order> vendasNaoExistentes = new ArrayList<Order>();
		for(Order venda : vendas){
			String id = venda.getId().toString();
			if(idsVendas.contains(id))
				vendasNaoExistentes.add(venda);
		}

		return vendasNaoExistentes;
	}

	@Transactional
	public List<InputStream> generateOrderNfes(List<Venda> orders) throws BusinessException{

		try {

			Vendedor vendedor = vendedorDao.recuperarVendedorPorIdMl(orders.get(0).getVendedor().getIdMl());

			for(Venda order: orders){
				Integer code = shippingProvider.searchMunicipyCodeByCep(order.getEnvio().getCep());
				order.getEnvio().setCodMunicipio(code);
				order.setVendedor(vendedor);
				Anuncio anuncio = anuncioDao.recuperarAnuncioPorIdMl(order.getDetalhesVenda().get(0).getAnuncio().getIdMl());
				order.getDetalhesVenda().get(0).setAnuncio(anuncio);
			};

			NfeConfig userNfeConfig = nfeConfidDao.recuperaUm(1L);

			List<NFNotaProcessada> notasProcessadas = nfeProvider.generateNFes(orders,userNfeConfig);

			int index=0;
			for(NFNotaProcessada notaProcessada: notasProcessadas){
				orders.get(index).setNrNfe(Long.valueOf(notaProcessada.getNota().getInfo().getIdentificacao().getNumeroNota()));
				index++;
			}

			List<InputStream> inputStreams = nfeProvider.generateNFePdf(notasProcessadas);

			userNfeConfig.setNrNota(new Integer(Integer.valueOf(userNfeConfig.getNrNota())+notasProcessadas.size()).toString());
			userNfeConfig.setNrLote(new Integer(Integer.valueOf(userNfeConfig.getNrLote())+1).toString());
			nfeConfidDao.alterar(userNfeConfig);

			return inputStreams;
		} catch (DaoException e) {
			String exception = String.format("Error generating Nfes - Database error");
			throw new BusinessException(exception);
		} catch (NfeProviderException e) {
			String exception = String.format("Error generating Nfes - Provider error");
			throw new BusinessProviderException(exception);
		} catch (ProviderException e) {
			String exception = String.format("Error generating Nfes - Querying municipy error");
			throw new BusinessProviderException(exception);
		}
	}

	public UserProvider getUserProvider() {
		return userProvider;
	}

	public OrderProvider getOrderProvider() {
		return orderProvider;
	}

	public NFeProvider getNfeProvider() {
		return nfeProvider;
	}

	public ShippingProvider getShippingProvider() {
		return shippingProvider;
	}

	@Override
	public List<Venda> retornaVendasPorPerioSemNfe(Date dtIni, Date dtFinal) {
		return vendaDao.recuperarVendasPorPeriodoSemNfe(dtIni, dtFinal);
	}

}
