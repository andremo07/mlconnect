package br.com.mpconnect.model;

public enum StatusEnvioMlEnum {
	
    PRONTO_PARA_ENVIO("ready_to_ship"),
    RETIRAR_EM_MAOS("to_be_agreed");
 
	protected String value;
		
	private StatusEnvioMlEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
