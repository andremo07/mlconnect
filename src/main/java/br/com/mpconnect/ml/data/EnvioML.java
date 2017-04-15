package br.com.mpconnect.ml.data;

public class EnvioML {
	
	//shipping -> id
	private String id;
	
	//shipping -> shipping_mode
	private String modoEnvio;
	
	//shipping -> shipping_type
	private String tipoEnvio;
	
	//shipping -> receiver_address
	private EnderecoML endereco;
	
	//shipping -> shipping_option -> name
	private String metodoEnvio;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModoEnvio() {
		return modoEnvio;
	}

	public void setModoEnvio(String modoEnvio) {
		this.modoEnvio = modoEnvio;
	}

	public String getTipoEnvio() {
		return tipoEnvio;
	}

	public void setTipoEnvio(String tipoEnvio) {
		this.tipoEnvio = tipoEnvio;
	}

	public EnderecoML getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoML endereco) {
		this.endereco = endereco;
	}

	public String getMetodoEnvio() {
		return metodoEnvio;
	}

	public void setMetodoEnvio(String metodoEnvio) {
		this.metodoEnvio = metodoEnvio;
	}
}
