package br.com.mpconnect.utils.comparator;

import java.util.Comparator;

import br.com.mpconnect.ml.data.MensagemVendaML;

public class MensagemMLComparator implements Comparator<MensagemVendaML>{

	@Override
	public int compare(MensagemVendaML msg1, MensagemVendaML msg2) {
		if (msg2.getData() == null || msg1.getData() == null)
			return 0;
		return msg1.getData().compareTo(msg2.getData());
	}

}
