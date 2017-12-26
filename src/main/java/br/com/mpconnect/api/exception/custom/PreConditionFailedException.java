package br.com.mpconnect.api.exception.custom;

import javax.ws.rs.core.Response.Status;

import br.com.mpconnect.api.exception.ApiException;

public class PreConditionFailedException extends ApiException
{
	private static final long serialVersionUID = 1634372488266040986L;

	public PreConditionFailedException(String message) {
		super(message,Status.PRECONDITION_FAILED);
	}
}
