package br.com.mpconnect.model;

public enum ModoEnvioMlEnum {
	
    MERCADO_ENVIOS_1("me1"),
    MERCADO_ENVIOS_2("me2");
 
	protected String value;
		
	private ModoEnvioMlEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
