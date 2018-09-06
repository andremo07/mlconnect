package br.com.mpconnect.model;

public enum TipoPagamentoEnum {
	
    BOLETO("ticket"),
    DEBITO("debito"),
    CARTAO("credit_card");
 
	protected String value;
		
	private TipoPagamentoEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
