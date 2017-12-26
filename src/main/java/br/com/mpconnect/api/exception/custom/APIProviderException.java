package br.com.mpconnect.api.exception.custom;

import javax.ws.rs.core.Response.Status;

import br.com.mpconnect.api.exception.ApiException;

public class APIProviderException extends ApiException
{
	private static final long serialVersionUID = 804243493099425962L;

	public APIProviderException(String message) {
		super(message, Status.SERVICE_UNAVAILABLE);
	}
}
