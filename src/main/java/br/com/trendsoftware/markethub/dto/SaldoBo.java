package br.com.trendsoftware.markethub.dto;

public class SaldoBo {
	
	private int mes;
	private double valor;
	
	public SaldoBo(int mes, double valor) {
		super();
		this.mes = mes;
		this.valor = valor;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
}