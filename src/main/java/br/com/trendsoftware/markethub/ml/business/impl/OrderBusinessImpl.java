package br.com.trendsoftware.markethub.ml.business.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.exception.BusinessProviderException;
import br.com.mpconnect.holder.MeliConfigurationHolder;
import br.com.mpconnect.model.AcessoMl;
import br.com.mpconnect.model.Channel;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Usuario;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.model.Vendedor;
import br.com.trendsoftware.markethub.business.FluxoCaixaBusiness;
import br.com.trendsoftware.markethub.business.OrderBusiness;
import br.com.trendsoftware.markethub.data.parser.MlParser;
import br.com.trendsoftware.markethub.repository.AccessRepository;
import br.com.trendsoftware.markethub.repository.UserRepository;
import br.com.trendsoftware.markethub.utils.DateUtils;
import br.com.trendsoftware.markethub.utils.ExceptionUtil;
import br.com.trendsoftware.mlProvider.dataprovider.OrderProvider;
import br.com.trendsoftware.mlProvider.dataprovider.UserProvider;
import br.com.trendsoftware.mlProvider.dto.Order;
import br.com.trendsoftware.mlProvider.dto.OrderList;
import br.com.trendsoftware.mlProvider.dto.OrderStatus;
import br.com.trendsoftware.mlProvider.dto.ShippingStatus;
import br.com.trendsoftware.mlProvider.dto.ShippingSubStatus;
import br.com.trendsoftware.mlProvider.dto.User;
import br.com.trendsoftware.mlProvider.dto.UserCredencials;
import br.com.trendsoftware.mlProvider.response.Response;
import br.com.trendsoftware.restProvider.exception.ProviderException;

@Service("mlOrderBusiness")
public class OrderBusinessImpl extends OrderBusiness implements Serializable {

	private static final long serialVersionUID = -6462524421141281130L;

	@Autowired
	public FluxoCaixaBusiness fluxoCaixaBusiness;

	@Autowired
	private UserProvider userProvider;

	@Autowired
	private OrderProvider orderProvider;

	@Autowired
	private AccessRepository accessRepository;

	@Autowired
	private UserRepository userRepository;

	@PostConstruct
	public void init(){
		getUserProvider().setLogger(logger);
		getOrderProvider().setLogger(logger);
	}

	public Origem getChannel(){
		return Channel.ML.getOrigem();
	}

	@Override
	@Transactional
	public void save(Venda order) throws BusinessException {
		Venda venda = saveOrder(order);
		if(venda!=null)
			fluxoCaixaBusiness.gerarFluxoDeCaixa(order);
	}

	public Venda searchPartnerOrder(String userId, String orderId) throws BusinessException {

		try 
		{
			Optional<AcessoMl> result = accessRepository.findById(MeliConfigurationHolder.getInstance().getClientId());

			AcessoMl acessoML = result.get();

			//MUDAR PARA BUSCA NA TABELA PELA COLUNA USER_ID
			Response response = userProvider.login(MeliConfigurationHolder.getInstance().getClientId().toString(), MeliConfigurationHolder.getInstance().getClientSecret(), acessoML.getRefreshToken());
			UserCredencials credencial = (UserCredencials) response.getData();

			response = orderProvider.searchOrderById(userId,orderId, credencial.getAccessToken());
			Order mlOrder = (Order) response.getData();

			response = userProvider.getUserInfo(credencial.getAccessToken());
			User user = (User) response.getData();

			Venda order = MlParser.parseOrder(mlOrder);

			Vendedor vendedor = sellerRepository.findByCnpj(user.getIdentification().getNumber());
			order.setVendedor(vendedor);

			return order;

		} catch (ProviderException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("");
			throw new BusinessProviderException(exception);
		}
	}
	
	public Venda searchPartnerOrderTest(String orderId) throws BusinessException {

		try 
		{
			Usuario usuario = getSessionUserLogin();

			String userId = usuario.getAcessoMercadoLivre().getIdMl();
			String accessToken = usuario.getAcessoMercadoLivre().getAccessToken();

			Response response = orderProvider.searchOrderById(userId,orderId, accessToken);
			Order mlOrder = (Order) response.getData();

			Venda order = MlParser.parseOrder(mlOrder);

			return order;

		} catch (ProviderException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("");
			throw new BusinessProviderException(exception);
		}
	}

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

			List<Order> mlOrders = new ArrayList<Order>();

			while(orderList.getPaging().getTotal() > mlOrders.size()){
				mlOrders.addAll(orderList.getOrders());				
				offset=offset+10;
				response = orderProvider.listOrdersByDate(userId, fromDate, toDate, OrderStatus.PAID, offset,10, accessToken);
				orderList = (OrderList) response.getData();
			}
			mlOrders = retornaVendasNaoCadastradas(mlOrders);

			response = userProvider.getUserInfo(accessToken);
			User user = (User) response.getData();

			Vendedor vendedor = sellerRepository.findByCnpj(user.getIdentification().getNumber());

			for(Order mlOrder: mlOrders){
				Venda marketHubOrder = MlParser.parseOrder(mlOrder);
				marketHubOrder.setOrigem(getChannel());
				marketHubOrder.setVendedor(vendedor);
				marketHubOrder = saveOrder(marketHubOrder);
				if(marketHubOrder!=null)
					fluxoCaixaBusiness.gerarFluxoDeCaixa(marketHubOrder);
			}

		} catch (ProviderException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("msisdn: %s - message: %s", e.getCode(), e.getCodeMessage());
			throw new BusinessProviderException(exception);
		}
	}

	@Transactional
	public void loadOrdersByDateTest(Date fromDate, Date toDate) throws BusinessException{

		getLogger().debug("carregando vendas recentes");

		try {

			AcessoMl acessoMl = accessRepository.findByClientId(MeliConfigurationHolder.getInstance().getClientId());

			Response tokenResponse = userProvider.login(MeliConfigurationHolder.getInstance().getClientId().toString(), MeliConfigurationHolder.getInstance().getClientSecret(), acessoMl.getRefreshToken());
			UserCredencials token = (UserCredencials) tokenResponse.getData();

			int offset = 0;
			Response response = orderProvider.listOrdersByDate("146216892", fromDate, toDate, OrderStatus.PAID, offset, 10, token.getAccessToken());
			OrderList orderList = (OrderList) response.getData();

			List<Order> mlOrders = new ArrayList<Order>();

			while(orderList.getPaging().getTotal() > mlOrders.size()){
				mlOrders.addAll(orderList.getOrders());				
				offset=offset+10;
				response = orderProvider.listOrdersByDate("146216892", fromDate, toDate, OrderStatus.PAID, offset,10, token.getAccessToken());
				orderList = (OrderList) response.getData();
			}
		} catch (ProviderException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("msisdn: %s - message: %s", e.getCode(), e.getCodeMessage());
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

			response = userProvider.getUserInfo(accessToken);
			User user = (User) response.getData();

			Vendedor vendedor = sellerRepository.findByCnpj(user.getIdentification().getNumber());

			List<Venda> vendas = new ArrayList<Venda>();
			for(Order order: orders){
				if(shippingStatus.equals(ShippingStatus.lookup(order.getShipping().getStatus())) && 
						shippingSubStatus.equals(ShippingSubStatus.lookup(order.getShipping().getSubstatus()))){
					Venda venda = MlParser.parseOrder(order);
					venda.setVendedor(vendedor);
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

	public UserProvider getUserProvider() {
		return userProvider;
	}

	public OrderProvider getOrderProvider() {
		return orderProvider;
	}
}
