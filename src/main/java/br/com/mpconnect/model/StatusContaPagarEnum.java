package br.com.mpconnect.model;

public enum StatusContaPagarEnum {
	
    PAGO("PAGO"),
    A_PAGAR("ABERTO");
 
	protected String value;
		
	private StatusContaPagarEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
