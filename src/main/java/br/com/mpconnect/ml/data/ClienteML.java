package br.com.mpconnect.ml.data;

public class ClienteML {
	
	//buyer -> id
	private String id;
	
	//buyer -> nickname
	private String apelido;
	
	//buyer -> email
	private String email;
	
	//buyer -> phone -> number
	private String telefone;
	
	//buyer -> phone -> area_code
	private String area;
	
	//buyer -> first_name
	private String primeiroNome;
	
	//buyer -> last_name
	private String ultimoNome;
	
	//buyer -> billing_info -> doc_type
	private String tipoDocumento;
	
	//buyer -> billing_info -> doc_number
	private String numeroDocumento;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPrimeiroNome() {
		return primeiroNome;
	}

	public void setPrimeiroNome(String primeiroNome) {
		this.primeiroNome = primeiroNome;
	}

	public String getUltimoNome() {
		return ultimoNome;
	}

	public void setUltimoNome(String ultimoNome) {
		this.ultimoNome = ultimoNome;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
}
