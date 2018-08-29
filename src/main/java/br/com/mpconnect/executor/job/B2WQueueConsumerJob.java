package br.com.mpconnect.executor.job;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.dao.VendedorDao;
import br.com.mpconnect.data.parser.B2WParser;
import br.com.mpconnect.holder.B2wConfigurationHolder;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.model.Vendedor;
import br.com.trendsoftware.b2wprovider.dataprovider.B2wOrderProvider;
import br.com.trendsoftware.b2wprovider.dto.OrderStatus;
import br.com.trendsoftware.b2wprovider.dto.SkyHubOrder;
import br.com.trendsoftware.b2wprovider.dto.SkyHubUserCredencials;
import br.com.trendsoftware.restProvider.exception.ProviderException;
import br.com.trendsoftware.restProvider.response.RestResponse;

@Service
public class B2WQueueConsumerJob implements Job
{
	@Autowired
	private B2wOrderProvider b2wOrderProvider;

	@Autowired
	private OrderBusiness orderBusiness;

	@Resource
	public VendaDao vendaDao;

	@Resource
	public VendedorDao vendedorDao;

	@Autowired
	private Gson parser;

	final Logger logger = Logger.getLogger(this.getClass().getName());

	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			b2wOrderProvider.setLogger(logger);
			SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(),B2wConfigurationHolder.getInstance().getApiKey(),B2wConfigurationHolder.getInstance().getAccountManagerKey());			
			RestResponse response = b2wOrderProvider.listQueueOrder(userCredencials);
			SkyHubOrder skyHubOrder = parser.fromJson(response.getBody(), SkyHubOrder.class);
			if(skyHubOrder!=null){
				if(skyHubOrder.getStatus().getCode().equals(OrderStatus.APPROVED.getName())){
					if(vendaDao.recuperaUm(skyHubOrder.getCode().split("-")[1])==null){
						Venda marketHubOrder = B2WParser.parseOrder(skyHubOrder);
						Origem origem = new Origem();
						origem.setId(3L);
						marketHubOrder.setOrigem(origem);
						Vendedor vendedor = vendedorDao.recuperaUm(1L);
						marketHubOrder.setVendedor(vendedor);
						orderBusiness.salvarVenda(marketHubOrder);
					}
					logger.debug("Aprovando pedido "+skyHubOrder.getCode());
					System.out.println("Aprovando pedido "+skyHubOrder.getCode());
				}
				b2wOrderProvider.processQueueOrder(userCredencials,skyHubOrder.getCode());
			}
		} catch (ProviderException e) {
			logger.debug("Erro");
			System.out.println("Erro!");
		} catch (DaoException e) {
			logger.debug("Erro");
			System.out.println("Erro!");;
		}
	}

	public B2wOrderProvider getB2wOrderProvider() {
		return b2wOrderProvider;
	}

	public void setB2wOrderProvider(B2wOrderProvider b2wOrderProvider) {
		this.b2wOrderProvider = b2wOrderProvider;
	}

}
