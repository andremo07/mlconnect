package br.com.mpconnect.bo;

import java.util.Map;

public class ContaBo {
	
	private String categoria;
	
	private Map<Integer,Double> valoresMensais;

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public Map<Integer, Double> getValoresMensais() {
		return valoresMensais;
	}

	public void setValoresMensais(Map<Integer, Double> valoresMensais) {
		this.valoresMensais = valoresMensais;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoria == null) ? 0 : categoria.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContaBo other = (ContaBo) obj;
		if (categoria == null) {
			if (other.categoria != null)
				return false;
		} else if (!categoria.equals(other.categoria))
			return false;
		return true;
	}

	public Double getValorMensal(int mes){
		return valoresMensais.get(mes);
	}

}
