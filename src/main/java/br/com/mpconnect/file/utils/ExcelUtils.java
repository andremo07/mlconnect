package br.com.mpconnect.file.utils;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	
	XSSFWorkbook workbook;
	
	public ExcelUtils(XSSFWorkbook workbook){
		this.workbook = workbook;
	}
	
	public void defineEstiloCelulaCabecalhoPadrao(XSSFCell cellCabecalho){
		
		XSSFCellStyle csCabecalho = workbook.createCellStyle();
		XSSFFont font= workbook.createFont();
		font.setBold(true);
		font.setFontName("Calibri");
		csCabecalho.setFont(font);
		csCabecalho.setAlignment(HorizontalAlignment.CENTER);
		cellCabecalho.setCellStyle(csCabecalho);
	}
	
	public void defineEstiloPadrao(XSSFCell cell){
		
		XSSFCellStyle csPlanilha = workbook.createCellStyle();
		csPlanilha.setAlignment(HorizontalAlignment.CENTER);
		csPlanilha.setBorderBottom(BorderStyle.THIN);
		csPlanilha.setBorderTop(BorderStyle.THIN);
		csPlanilha.setBorderRight(BorderStyle.THIN);
		csPlanilha.setBorderLeft(BorderStyle.THIN);
		cell.setCellStyle(csPlanilha);
	}
	
	public void criarCelulaCabecalho(XSSFRow row,String valor,int index){
		XSSFCell cellCabecalho = row.createCell(index);
		cellCabecalho.setCellValue(valor);
		defineEstiloCelulaCabecalhoPadrao(cellCabecalho);
	}
	
	public void criarCelula(XSSFRow row,String valor,int index,boolean comborda){
		XSSFCell cell = row.createCell(index);
		cell.setCellValue(valor);
		if(comborda)
			defineEstiloPadrao(cell);
	}
	
	public XSSFSheet criarFolha(String nome){
		return workbook.createSheet(nome);
	}

	public XSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}

}
