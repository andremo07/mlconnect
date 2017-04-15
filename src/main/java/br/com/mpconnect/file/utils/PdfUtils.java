package br.com.mpconnect.file.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfUtils {
	
	PDDocument pdfDocument;
	
	public PdfUtils(InputStream is) throws IOException{
		pdfDocument = PDDocument.load(is);
	}
	
	public List<String> localizarString(String pattern) throws IOException{
		
		PDFTextStripper stripper = new PDFTextStripper();
		String texto = stripper.getText(pdfDocument);
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(texto);
		List<String> texts = new ArrayList<String>();
		while (m.find( )) {
			texts.add(m.group(2));
		}
		return texts;
	}
	
	public void close() throws IOException{
		pdfDocument.close();
	}
	
	public void save(File file) throws IOException{
		pdfDocument.save(file);
	}

}
