package br.com.mpconnect.ml.data;

import java.util.List;

public class MetodoEnvioML {
	
	private String modoEnvio;
	
	private Boolean retiradaLocal;
	
	private Boolean freteGratis;
	
	private String metodoGratisId;
		
	private String metodoGratisModo;
	
	private List<String> metodoGratisValores;

	public String getModoEnvio() {
		return modoEnvio;
	}

	public void setModoEnvio(String modoEnvio) {
		this.modoEnvio = modoEnvio;
	}

	public Boolean getRetiradaLocal() {
		return retiradaLocal;
	}

	public void setRetiradaLocal(Boolean retiradaLocal) {
		this.retiradaLocal = retiradaLocal;
	}

	public Boolean getFreteGratis() {
		return freteGratis;
	}

	public void setFreteGratis(Boolean freteGratis) {
		this.freteGratis = freteGratis;
	}

	public String getMetodoGratisId() {
		return metodoGratisId;
	}

	public void setMetodoGratisId(String metodoGratisId) {
		this.metodoGratisId = metodoGratisId;
	}

	public String getMetodoGratisModo() {
		return metodoGratisModo;
	}

	public void setMetodoGratisModo(String metodoGratisModo) {
		this.metodoGratisModo = metodoGratisModo;
	}

	public List<String> getMetodoGratisValores() {
		return metodoGratisValores;
	}

	public void setMetodoGratisValores(List<String> metodoGratisValores) {
		this.metodoGratisValores = metodoGratisValores;
	}
}

