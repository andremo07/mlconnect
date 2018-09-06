package br.com.mpconnect.model;

public enum MesesEnum {
	
    JANEIRO("Janeiro"),
    FEVEREIRO("Fevereiro"),
    MAR�O("Mar�o"),
    ABRIL("Abril"),
    MAIO("Maio"),
    JUNHO("Junho"),
    JULHO("Julho"),
    AGOSTO("Agosto"),
    SETEMBRO("Setembro"),
    OUTUBRO("Outubro"),
    NOVEMBRO("Novembro"),
    DEZEMBRO("Dezembro");
 
	protected String value;
		
	private MesesEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
