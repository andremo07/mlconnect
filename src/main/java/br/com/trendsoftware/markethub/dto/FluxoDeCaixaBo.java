package br.com.trendsoftware.markethub.dto;

import java.util.List;

public class FluxoDeCaixaBo {
	
	private String tipoFluxo;
	
	private List<ContaBo> contas;
	
	private List<Double> totais;

	public List<ContaBo> getContas() {
		return contas;
	}

	public void setContas(List<ContaBo> contas) {
		this.contas = contas;
	}

	public List<Double> getTotais() {
		return totais;
	}

	public void setTotais(List<Double> totais) {
		this.totais = totais;
	}

	public String getTipoFluxo() {
		return tipoFluxo;
	}

	public void setTipoFluxo(String tipoFluxo) {
		this.tipoFluxo = tipoFluxo;
	}
	
}
