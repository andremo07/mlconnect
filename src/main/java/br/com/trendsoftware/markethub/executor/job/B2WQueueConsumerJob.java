package br.com.trendsoftware.markethub.executor.job;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.holder.B2wConfigurationHolder;
import br.com.mpconnect.model.Venda;
import br.com.trendsoftware.b2wprovider.dataprovider.B2wOrderProvider;
import br.com.trendsoftware.b2wprovider.dto.OrderStatus;
import br.com.trendsoftware.b2wprovider.dto.SkyHubOrder;
import br.com.trendsoftware.b2wprovider.dto.SkyHubUserCredencials;
import br.com.trendsoftware.markethub.business.OrderBusiness;
import br.com.trendsoftware.markethub.data.parser.B2WParser;
import br.com.trendsoftware.restProvider.exception.ProviderException;
import br.com.trendsoftware.restProvider.response.RestResponse;

@Service
public class B2WQueueConsumerJob implements Job
{
	@Autowired
	private B2wOrderProvider b2wOrderProvider;

	@Autowired
	@Qualifier("b2WOrderBusiness")
	private OrderBusiness orderBusiness;

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
					Venda marketHubOrder = B2WParser.parseOrder(skyHubOrder);
					orderBusiness.save(marketHubOrder);
					logger.debug("Aprovando pedido "+skyHubOrder.getCode());
					System.out.println("Aprovando pedido "+skyHubOrder.getCode());
				}
				b2wOrderProvider.processQueueOrder(userCredencials,skyHubOrder.getCode());
			}
		} catch (ProviderException e) {
			logger.debug("Erro");
			System.out.println("Erro!");
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	public B2wOrderProvider getB2wOrderProvider() {
		return b2wOrderProvider;
	}

	public void setB2wOrderProvider(B2wOrderProvider b2wOrderProvider) {
		this.b2wOrderProvider = b2wOrderProvider;
	}

}
