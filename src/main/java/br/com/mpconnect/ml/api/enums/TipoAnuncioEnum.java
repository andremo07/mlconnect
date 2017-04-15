package br.com.mpconnect.ml.api.enums;

import br.com.mpconnect.ml.data.TipoAnuncioML;

public enum TipoAnuncioEnum {
	
    ML_PREMIUM(new TipoAnuncioML("gold_pro","Premium")),
    ML_CLASSICO(new TipoAnuncioML("gold_special","Clássico"));
 
	protected TipoAnuncioML value;
		
	private TipoAnuncioEnum(TipoAnuncioML value) {
		this.value = value;
	}
	
	public TipoAnuncioML getValue() {
		return value;
	}
	
}
