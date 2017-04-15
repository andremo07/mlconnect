package br.com.mpconnect.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="CONTA_BANCARIA")
public class ContaBancaria implements Persistente{
	
	@Id
	@Column(name="ID")
	@GeneratedValue
	private Long id;

	@Column(name="NOME")
	private String nome;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="VENDEDOR_ID" , nullable=false)
	private Vendedor vendedor;
	
	@OneToMany(fetch = FetchType.LAZY,cascade={CascadeType.ALL})
	@JoinColumn(name="CONTA_ID" , nullable=false)
	private List<Saldo> saldos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	public List<Saldo> getSaldos() {
		return saldos;
	}

	public void setSaldos(List<Saldo> saldos) {
		this.saldos = saldos;
	}
}
