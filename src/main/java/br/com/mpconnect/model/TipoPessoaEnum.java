package br.com.mpconnect.model;

public enum TipoPessoaEnum {
	
    FISICA("F�SICA"),
    JURIDICA("JUR�DICA");
 
	protected String value;
		
	private TipoPessoaEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
