package br.com.mpconnect.manager;

import java.util.List;

import org.primefaces.model.StreamedContent;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.model.Venda;

public interface LogisticBusiness {
	public StreamedContent generateShippingSheetAndTags(List<Venda> vendasSelecionadas, String accessToken) throws BusinessException;
}
