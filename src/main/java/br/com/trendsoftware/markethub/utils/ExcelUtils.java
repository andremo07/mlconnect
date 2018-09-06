package br.com.trendsoftware.markethub.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
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
	
	public static List<List<String>> read(String fileName) throws IOException 
	{
		FileInputStream excelFile = new FileInputStream(new File(fileName));
		XSSFWorkbook myWorkBook = new XSSFWorkbook(excelFile);
		XSSFSheet datatypeSheet = myWorkBook.getSheetAt(0);
		Iterator<Row> rowIter = datatypeSheet.iterator();
		List<List<String>> sheetValues = new ArrayList<List<String>>();
		while (rowIter.hasNext()) {
			XSSFRow currentRow = (XSSFRow) rowIter.next();
			Iterator<Cell> cellIterator = currentRow.iterator();
			List<String> rowValues = new ArrayList<String>();
			while (cellIterator.hasNext()) {
				Cell currentCell = cellIterator.next();	
				if (currentCell.getCellTypeEnum() == CellType.STRING) {
					rowValues.add(currentCell.getStringCellValue());
				} else if (currentCell.getCellTypeEnum() == CellType.FORMULA) {
					if(currentCell.getCachedFormulaResultTypeEnum() == CellType.STRING)
						rowValues.add(currentCell.getRichStringCellValue().toString());
					else if (currentCell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC){
						rowValues.add(Double.toString(currentCell.getNumericCellValue()));
					}
				}
				else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
					rowValues.add(Double.toString(currentCell.getNumericCellValue()));
				}
				else
					rowValues.add("");
			}
			sheetValues.add(rowValues);
		}
		myWorkBook.close();
		return sheetValues;
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
