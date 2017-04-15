package br.com.mpconnect.ml.data;

public class EnderecoML {

	//shipping -> receiver_address -> id
	private String id;
	
	//shipping -> receiver_address -> zip_code
	private Long cep;
	
	//shipping -> receiver_address -> street_number
	private String numero;
	
	//shipping -> receiver_address ->
	private String nomeRua;
	
	//shipping -> receiver_address -> state -> name
	private String nomeEstado;
	
	//shipping -> receiver_address -> state -> id
	private String ufEstado;
	
	//shipping -> receiver_address -> neighborhood -> name
	private String bairro;
	
	//shipping -> receiver_address -> comment
	private String comentario;
	
	//shipping -> receiver_address -> country -> name
	private String pais;
	
	//shipping -> receiver_address -> city -> name
	private String cidade;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getCep() {
		return cep;
	}

	public void setCep(Long cep) {
		this.cep = cep;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNomeRua() {
		return nomeRua;
	}

	public void setNomeRua(String nomeRua) {
		this.nomeRua = nomeRua;
	}

	public String getNomeEstado() {
		return nomeEstado;
	}

	public void setNomeEstado(String nomeEstado) {
		this.nomeEstado = nomeEstado;
	}

	public String getUfEstado() {
		return ufEstado;
	}

	public void setUfEstado(String ufEstado) {
		this.ufEstado = ufEstado;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}	
}
