package br.com.mpconnect.utils.comparator;

import java.util.Comparator;

import br.com.mpconnect.model.Venda;

public class VendaComparator implements Comparator<Venda>{

	@Override
	public int compare(Venda v1, Venda v2) {
		if (v1.getData() == null || v2.getData() == null)
			return 0;
		return v2.getData().compareTo(v1.getData());
	}

}
