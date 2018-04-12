package br.com.mpconnect.manager;

import java.io.InputStream;
import java.util.List;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.model.Venda;

public interface LogisticBusiness {
	public List<InputStream> generateShippingSheetAndTags(List<Venda> vendasSelecionadas,String tagsPathName,String sheetPathName, String accessToken) throws BusinessException;
}
