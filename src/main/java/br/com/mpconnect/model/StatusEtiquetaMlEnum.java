package br.com.mpconnect.model;

public enum StatusEtiquetaMlEnum {
	
	PRONTO_PARA_IMPRIMIR("ready_to_print"),
    IMPRESSA("printed");
 
	protected String value;
		
	private StatusEtiquetaMlEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
