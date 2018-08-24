package br.com.mpconnect.manager.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.dao.AnuncioDao;
import br.com.mpconnect.dao.ClienteDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.OrigemDao;
import br.com.mpconnect.data.parser.MlParser;
import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.exception.BusinessProviderException;
import br.com.mpconnect.holder.MeliConfigurationHolder;
import br.com.mpconnect.manager.FluxoDeCaixaManagerBo;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.model.AcessoMl;
import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Cliente;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Usuario;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.model.Vendedor;
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
public class OrderBusinessImpl extends OrderBusiness implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6462524421141281130L;

	@Resource
	public AnuncioDao anuncioDao;

	@Resource
	public ClienteDao clienteDao;

	@Resource
	public OrigemDao origemDao;

	@Autowired
	public FluxoDeCaixaManagerBo fluxoDeCaixaManager;

	@Autowired
	private UserProvider userProvider;

	@Autowired
	private OrderProvider orderProvider;

	@PostConstruct
	public void init(){
		getUserProvider().setLogger(logger);
		getOrderProvider().setLogger(logger);
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



	public void saveOrder(Venda venda) throws BusinessException
	{
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
			if(vendaDao.recuperaUm(venda.getId())==null){
				Origem origem = new Origem();
				origem.setId(1L);
				venda.setOrigem(origem);
				saveOrder(venda);
			}

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

	public UserProvider getUserProvider() {
		return userProvider;
	}

	public OrderProvider getOrderProvider() {
		return orderProvider;
	}

}
