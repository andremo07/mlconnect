package br.com.mpconnect.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

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
}