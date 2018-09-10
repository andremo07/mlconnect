package br.com.mpconnect.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class ApiException extends WebApplicationException
{
	private static final long serialVersionUID = -6576650918668030125L;
	
	public final Logger logger = LogManager.getLogger(this.getClass());
	
	public ApiException(String message, Status status) {
		super(Response.status(status).entity(message).type(MediaType.APPLICATION_JSON).build());
		logger.error(message);
	}

	public ApiException(String message, Integer upgradeRequired) {
		super(Response.status(upgradeRequired).entity(message).type(MediaType.APPLICATION_JSON).build());
		logger.error(message);
	}
}
