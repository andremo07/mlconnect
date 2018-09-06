package br.com.trendsoftware.markethub.ml.business.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.exception.BusinessProviderException;
import br.com.mpconnect.model.Channel;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Venda;
import br.com.trendsoftware.markethub.business.LogisticBusiness;
import br.com.trendsoftware.markethub.utils.ExceptionUtil;
import br.com.trendsoftware.markethub.utils.PdfUtils;
import br.com.trendsoftware.mlProvider.dataprovider.ShippingProvider;
import br.com.trendsoftware.restProvider.exception.ProviderException;

@Service("mlLogisticBusiness")
public class LogisticBusinessImpl extends LogisticBusiness implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6462524421141281130L;

	@Autowired
	private ShippingProvider shippingProvider;

	@PostConstruct
	public void init(){
		getShippingProvider().setLogger(logger);
	}
	
	@Override
	public Origem getChannel() {
		return Channel.ML.getOrigem();
	}

	public List<InputStream> generateShippingSheetAndTags(List<Venda> vendas,String accessToken) throws BusinessException
	{
		try {	
			List<String> shippingIds = new ArrayList<String>();
			for(Venda venda: vendas)
				shippingIds.add(venda.getEnvio().getIdMl());

			List<InputStream> inputStreams = new ArrayList<InputStream>();
			LinkedHashMap<String, Venda> mapEnvios = new LinkedHashMap<String, Venda>();
			for(Venda venda : vendas){
				InputStream is = shippingProvider.printTags(Collections.singletonList(venda.getEnvio().getIdMl()),accessToken);
				List<String> codigosNf = PdfUtils.localizarString(is,"(NF: )(\\d+)");
				if(!mapEnvios.containsKey(codigosNf.get(0))){
					mapEnvios.put(codigosNf.get(0), venda);
					is = new ByteArrayInputStream(PdfUtils.removePages(is,new int[]{0,2}).toByteArray()); 
					inputStreams.add(is);
				}
			}
			
			InputStream pdfInputStream = new ByteArrayInputStream(PdfUtils.merge(inputStreams).toByteArray());		
			inputStreams.clear();
			inputStreams.add(pdfInputStream);

			//GERAÇAO PLANILHA EXCEL				
			InputStream excelInputStream = new ByteArrayInputStream(criarPlanilhaExcelEnvio(mapEnvios).toByteArray());
			inputStreams.add(excelInputStream);
			return inputStreams;

		} catch (IOException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("FILE_MANIPULATION_ERROR");
			throw new BusinessException(exception);
		} catch (ProviderException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("SHIPPING_PROVIDER_ERROR", e.getCode(), e.getCodeMessage());
			throw new BusinessProviderException(exception);
		}
	}

	public ShippingProvider getShippingProvider() {
		return shippingProvider;
	}
	
}
