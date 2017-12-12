package br.com.mpconnect.ml.api;

import java.util.Random;

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

import br.com.trendsoftware.mlProvider.dto.Notification;
import br.com.trendsoftware.mlProvider.dto.Topic;

@Component
@Path("/notification")
public class NotificationApi
{
	final Logger logger = Logger.getLogger(this.getClass().getName());

	final String accessToken = "APP_USR-4013368235398167-082517-361b315e418da87ddd0c241b6625be8a__N_B__-146216892";

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

			getLogger().debug("starting getMlNotification");

			Topic topic = Topic.lookup(notification.getTopic());

			if(topic!=null){

				getLogger().debug("ml notification received: "+topic.getName());

				Random rand = new Random();

				Integer n = rand.nextInt();
				
				if(n<0)
					n=n*(-1);
		
				switch (topic) {
				case ITEMS:{

					break;
				}
				case ORDERS:

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

	}

	public Logger getLogger() {
		return logger;
	}

}
