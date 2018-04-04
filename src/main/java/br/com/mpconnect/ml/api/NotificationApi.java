package br.com.mpconnect.ml.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import br.com.mpconnect.api.exception.custom.APIProviderException;
import br.com.mpconnect.api.exception.custom.PreConditionFailedException;
import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.exception.BusinessProviderException;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.trendsoftware.mlProvider.dto.Notification;
import br.com.trendsoftware.mlProvider.dto.Topic;

@Component
@Path("/notification")
public class NotificationApi
{
	final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
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
					getOrderBusiness().save(userId, orderId);
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
