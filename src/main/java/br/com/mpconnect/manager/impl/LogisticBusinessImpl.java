package br.com.mpconnect.manager.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.exception.BusinessProviderException;
import br.com.mpconnect.file.utils.ExcelUtils;
import br.com.mpconnect.file.utils.PdfUtils;
import br.com.mpconnect.manager.LogisticBusiness;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.Produto;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.util.ExceptionUtil;
import br.com.trendsoftware.mlProvider.dataprovider.ShippingProvider;
import br.com.trendsoftware.restProvider.exception.ProviderException;

@Service("logisticBusiness")
public class LogisticBusinessImpl extends MarketHubBusiness implements LogisticBusiness, Serializable {

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

	public List<InputStream> generateShippingSheetAndTags(List<Venda> vendas,String accessToken) throws BusinessException
	{
		try {	
			List<String> shippingIds = new ArrayList<String>();
			for(Venda venda: vendas)
				shippingIds.add(venda.getEnvio().getIdMl());

			List<InputStream> inputStreams = new ArrayList<InputStream>();
			Map<String, Venda> mapEnvios = new HashMap<String, Venda>();
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

	public ByteArrayOutputStream criarPlanilhaExcelEnvio(Map<String,Venda> mapEnvios) throws IOException{

		XSSFWorkbook workbook = new XSSFWorkbook();
		ExcelUtils excelUtils = new ExcelUtils(workbook);
		XSSFSheet sheet = excelUtils.criarFolha("FirstSheet");  

		XSSFRow cabecalhoRow = sheet.createRow((short)0);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "NF", 0);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "CODIGO DO PRODUTO", 1);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "QTD", 2);

		int rowIndex=0;
		for (String codNf: mapEnvios.keySet())
		{			
			Venda venda = mapEnvios.get(codNf);
			DetalheVenda dv = venda.getDetalhesVenda().get(0);
			Produto produto = dv.getProduto();
			if(produto==null||produto.getSku()==null){
				produto=new Produto();
				produto.setSku("");
			}
			XSSFRow row = sheet.createRow((short)rowIndex+1);
			excelUtils.criarCelula(row, codNf, 0, true);
			excelUtils.criarCelula(row, produto.getSku(), 1, true);
			excelUtils.criarCelula(row, dv.getQuantidade().toString(), 2, true);
			rowIndex++;
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		workbook.close();
		out.close();
		return out;
	}

	public ShippingProvider getShippingProvider() {
		return shippingProvider;
	}

}
