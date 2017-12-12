package br.com.mpconnect.utils.comparator;

import java.util.Comparator;

import br.com.mpconnect.ml.dto.PerguntaML;

public class PerguntaMLComparator implements Comparator<PerguntaML>{

	@Override
	public int compare(PerguntaML msg1, PerguntaML msg2) {
		if (msg2.getData() == null || msg1.getData() == null)
			return 0;
		return msg1.getData().compareTo(msg2.getData());
	}

}
