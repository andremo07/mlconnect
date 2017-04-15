package br.com.mpconnect.ml.api.enums;

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
