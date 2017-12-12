package br.com.mpconnect.exception;

public class BusinessException extends Exception
{	
	private static final long serialVersionUID = 6157865509044355801L;

	public BusinessException(String exception) {
		super(exception);
	}
}
