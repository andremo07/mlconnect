package br.com.mpconnect.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PAGAMENTO")
public class Pagamento {
	
	@Id
	@Column(name="ID")
	@GeneratedValue
	private Long id;
	
	@Column(name="IDML")
	private String idML;
	
	@Column(name="VALORTRANSACAO")
	private Double valorTransacao;
	
	@Column(name="TOTALPAGO")
	private Double totalPago;
	
	@Column(name="TIPO")
	private String tipo;
	
	@Column(name="NUMEROPARCELAS")
	private Integer numeroParcelas;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdML() {
		return idML;
	}

	public void setIdML(String idML) {
		this.idML = idML;
	}

	public Double getValorTransacao() {
		return valorTransacao;
	}

	public void setValorTransacao(Double valorTransacao) {
		this.valorTransacao = valorTransacao;
	}

	public Double getTotalPago() {
		return totalPago;
	}

	public void setTotalPago(Double totalPago) {
		this.totalPago = totalPago;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getNumeroParcelas() {
		return numeroParcelas;
	}

	public void setNumeroParcelas(Integer numeroParcelas) {
		this.numeroParcelas = numeroParcelas;
	}

}
