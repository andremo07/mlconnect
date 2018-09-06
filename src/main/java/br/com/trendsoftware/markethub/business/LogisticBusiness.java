package br.com.trendsoftware.markethub.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.Produto;
import br.com.mpconnect.model.Venda;
import br.com.trendsoftware.markethub.utils.ExcelUtils;

public abstract class LogisticBusiness extends MarketHubBusiness {
	
	public abstract List<InputStream> generateShippingSheetAndTags(List<Venda> vendasSelecionadas,String accessToken) throws BusinessException;
	
	public ByteArrayOutputStream criarPlanilhaExcelEnvio(LinkedHashMap<String,Venda> mapEnvios) throws IOException{

		XSSFWorkbook workbook = new XSSFWorkbook();
		ExcelUtils excelUtils = new ExcelUtils(workbook);
		XSSFSheet sheet = excelUtils.criarFolha("FirstSheet");  

		XSSFRow cabecalhoRow = sheet.createRow((short)0);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "NF", 0);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "CODIGO DO PRODUTO", 1);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "QTD", 2);

		int rowIndex=0;
		for (Map.Entry<String, Venda> entry : mapEnvios.entrySet())
		{			
			Venda venda = entry.getValue();
			DetalheVenda dv = venda.getDetalhesVenda().get(0);
			Produto produto = dv.getProduto();
			if(produto==null||produto.getSku()==null){
				produto=new Produto();
				produto.setSku("");
			}
			XSSFRow row = sheet.createRow((short)rowIndex+1);
			excelUtils.criarCelula(row, entry.getKey(), 0, true);
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
}
