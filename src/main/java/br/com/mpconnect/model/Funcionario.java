package br.com.mpconnect.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

//@Entity
//@Table(name="FUNCIONARIO")
public class Funcionario implements Persistente{
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private String nome;
	
	@Column(name="DT_NASCIMENTO")
	private Date dtNascimento;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ID_CARGO")
	private Cargo cargo;

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

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

}
