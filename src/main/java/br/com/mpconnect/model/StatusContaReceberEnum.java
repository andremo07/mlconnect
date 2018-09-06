package br.com.mpconnect.model;

public enum StatusContaReceberEnum {
	
    RECEBIDO("RECEBIDO"),
    A_RECEBER("ABERTO");
 
	protected String value;
		
	private StatusContaReceberEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
