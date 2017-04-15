package br.com.mpconnect.ml.data;

import java.util.Date;

public class PerguntaML {

	private Date data;
	
	private String textoPergunta;
	
	private String textoResposta;
	
	public PerguntaML(){
		
	}

	public PerguntaML(Date data,String textoPergunta, String textoResposta) {
		this.data = data;
		this.textoPergunta = textoPergunta;
		this.textoResposta = textoResposta;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getTextoPergunta() {
		return textoPergunta;
	}

	public void setTextoPergunta(String textoPergunta) {
		this.textoPergunta = textoPergunta;
	}

	public String getTextoResposta() {
		return textoResposta;
	}

	public void setTextoResposta(String textoResposta) {
		this.textoResposta = textoResposta;
	}
	
}