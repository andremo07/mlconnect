package br.com.trendsoftware.markethub.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import br.com.mpconnect.api.exception.custom.APIProviderException;
import br.com.mpconnect.api.exception.custom.PreConditionFailedException;
import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.exception.BusinessProviderException;
import br.com.mpconnect.model.Venda;
import br.com.trendsoftware.markethub.business.OrderBusiness;
import br.com.trendsoftware.mlProvider.dto.Notification;
import br.com.trendsoftware.mlProvider.dto.Topic;

@Component
@Path("/notification")
public class NotificationApi
{
	final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	@Qualifier("mlOrderBusiness")
	private OrderBusiness orderBusiness;
	
	@Autowired
	private Gson parser;

	@Context 
	private HttpServletRequest servletRequest;
	
	@POST
	@Path("/ml")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMlNotification(Notification notification)
	{
		getLogger().debug("ml notification received: "+notification.getTopic());

		try 
		{
			Topic topic = Topic.lookup(notification.getTopic());

			if(topic!=null)
			{
				switch (topic) {
				case ITEMS:{

					break;
				}
				case ORDERS:
					getLogger().debug(getParser().toJson(notification));
					
					String resource = notification.getResource();
					String orderId = resource.split("/")[2];
					String userId = notification.getUser_id();
					Venda order = getOrderBusiness().searchPartnerOrder(userId, orderId);
					getOrderBusiness().save(order);
					break;
				case QUESTIONS:

					break;
				case SHIPMENTS:

					break;
				case PAYMENTS:

					break;
				default:
					break;
				}
			}
			return Response.ok().build();
		} catch (BusinessProviderException e) {
			throw new APIProviderException(e.getMessage());
		} catch (BusinessException e) {
			throw new PreConditionFailedException(e.getMessage());
		}

	}
	
	public Gson getParser() {
		return parser;
	}

	public Logger getLogger() {
		return logger;
	}

	public OrderBusiness getOrderBusiness() {
		return orderBusiness;
	}
	
}
