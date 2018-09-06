package br.com.mpconnect.model;

public enum TipoPessoaEnum {
	
    FISICA("FÍSICA"),
    JURIDICA("JURÍDICA");
 
	protected String value;
		
	private TipoPessoaEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
