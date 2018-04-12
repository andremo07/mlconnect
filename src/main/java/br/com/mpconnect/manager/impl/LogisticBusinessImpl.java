package br.com.mpconnect.manager.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	
	public List<InputStream> generateShippingSheetAndTags(List<Venda> vendasSelecionadas,String tagsPathName,String sheetPathName,String accessToken) throws BusinessException
	{
		try {	
			List<String> shippingIds = new ArrayList<String>();
			for(Venda venda: vendasSelecionadas)
				shippingIds.add(venda.getEnvio().getIdMl());

			//GERA��O DO ARQUIVO PDF		
			InputStream pdfInputStream = shippingProvider.printTags(shippingIds, accessToken);

			//LEITURA PDF
			if(pdfInputStream!=null){
				List<InputStream> inputStreams = new ArrayList<InputStream>();
				//GERANDO ETIQUETAS UMA A UMA PARA GARANTIA DE ORDEM
				Map<String, Venda> mapEnvios = new HashMap<String, Venda>();
				for(Venda venda : vendasSelecionadas){
					InputStream is = shippingProvider.printTags(Collections.singletonList(venda.getEnvio().getIdMl()),accessToken);
					List<String> codigosNf = PdfUtils.localizarString(is,"(NF: )(\\d+)");
					mapEnvios.put(codigosNf.get(0), venda);
					is.close();		
				}

				List<String> codigosNfs = PdfUtils.localizarString(pdfInputStream,"(NF: )(\\d+)");
				File filePdf = new File(tagsPathName);
				filePdf.createNewFile();
				pdfInputStream.reset();
				PdfUtils.save(pdfInputStream,filePdf);
				pdfInputStream = new FileInputStream(filePdf);		
				inputStreams.add(pdfInputStream);
				
				//GERA�AO PLANILHA EXCEL				
				XSSFWorkbook workbook = criarPlanilhaExcelEnvio(codigosNfs,mapEnvios);
				File fileExcel = new File(sheetPathName);
				FileOutputStream fos = new FileOutputStream(fileExcel);
				workbook.write(fos);
				workbook.close();
				fos.flush();
				fos.close();
				InputStream excelInputStream = new FileInputStream(fileExcel);
				inputStreams.add(excelInputStream);
				return inputStreams;
			}
			else
				throw new BusinessException("EMPTY_INPUT_ERROR");
			
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
	
	public XSSFWorkbook criarPlanilhaExcelEnvio(List<String> codigosNfs,Map<String,Venda> mapEnvios){

		XSSFWorkbook workbook = new XSSFWorkbook();
		ExcelUtils excelUtils = new ExcelUtils(workbook);
		XSSFSheet sheet = excelUtils.criarFolha("FirstSheet");  

		XSSFRow cabecalhoRow = sheet.createRow((short)0);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "NF", 0);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "CODIGO DO PRODUTO", 1);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "QTD", 2);

		int rowIndex=0;
		for (String codNf: codigosNfs)
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

		return workbook;
	}

	public ShippingProvider getShippingProvider() {
		return shippingProvider;
	}
	
}
