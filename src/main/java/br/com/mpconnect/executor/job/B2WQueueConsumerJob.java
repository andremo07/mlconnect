package br.com.mpconnect.executor.job;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.data.parser.B2WParser;
import br.com.mpconnect.holder.B2wConfigurationHolder;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Venda;
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

	@Autowired
	private Gson parser;

	final Logger logger = Logger.getLogger(this.getClass().getName());

	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			b2wOrderProvider.setLogger(logger);

			SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(),B2wConfigurationHolder.getInstance().getApiKey(),B2wConfigurationHolder.getInstance().getAccountManagerKey());			
			RestResponse response = b2wOrderProvider.listQueueOrder(userCredencials);
			SkyHubOrder order = parser.fromJson(response.getBody(), SkyHubOrder.class);
			if(order!=null){
				if(order.getStatus().getCode().equals(OrderStatus.COMPLETE.getName()) || order.getStatus().getCode().equals(OrderStatus.SHIPPED.getName())|| order.getStatus().getCode().equals(OrderStatus.CANCELED.getName())){
					b2wOrderProvider.processQueueOrder(userCredencials,order.getCode());
					System.out.println("Pedido Processado "+order.getCode()+" " + order.getStatus().getCode());
				}
				else{
					Venda venda = B2WParser.parseOrder(order);
					Origem origem = new Origem();
					origem.setId(1L);
					venda.setOrigem(origem);
					//orderBusiness.salvarVenda(venda);
/*					if(vendaDao.recuperaUm(venda.getId())==null){

						
					}*/
					logger.debug("Aprovando pedido "+order.getCode());
					System.out.println("Aprovando pedido "+order.getCode());
				}
			}

		} catch (ProviderException e) {

			logger.debug("Erro");
			System.out.println("Erro!");

		}
	}

	public B2wOrderProvider getB2wOrderProvider() {
		return b2wOrderProvider;
	}

	public void setB2wOrderProvider(B2wOrderProvider b2wOrderProvider) {
		this.b2wOrderProvider = b2wOrderProvider;
	}

}
