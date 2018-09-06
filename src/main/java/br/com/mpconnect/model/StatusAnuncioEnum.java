package br.com.mpconnect.model;

public enum StatusAnuncioEnum {
	
    ATIVO("active"),
    PAUSADO("paused"),
    FINALIZADO("stopped");
 
	protected String value;
		
	private StatusAnuncioEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
