package br.com.mpconnect.ml.data;

import java.util.Date;

public class MensagemVendaML {

	private Date data;
	
	private String remetente;
	
	private String destinatario;
	
	private String texto;
	
	public MensagemVendaML(){
		
	}
	
	public MensagemVendaML(Date data, String remetente, String destinatario,
			String texto) {
		this.data = data;
		this.remetente = remetente;
		this.destinatario = destinatario;
		this.texto = texto;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getRemetente() {
		return remetente;
	}

	public void setRemetente(String remetente) {
		this.remetente = remetente;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
}