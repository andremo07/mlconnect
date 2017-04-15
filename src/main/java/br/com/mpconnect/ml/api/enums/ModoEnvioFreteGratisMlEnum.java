package br.com.mpconnect.ml.api.enums;

public enum ModoEnvioFreteGratisMlEnum {
	
    TODO_BRASIL("country"),
    EXCLUINDO_REGIOES("exclude_region");
 
	protected String value;
		
	private ModoEnvioFreteGratisMlEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
