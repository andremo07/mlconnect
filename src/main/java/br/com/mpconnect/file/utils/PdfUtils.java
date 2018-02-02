package br.com.mpconnect.file.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfUtils {

	PDDocument pdfDocument;

	public PdfUtils(){
	}

	public PdfUtils(InputStream is) throws IOException{
		pdfDocument = PDDocument.load(is);
	}

	public PdfUtils(byte[] bytes) throws IOException{
		pdfDocument = PDDocument.load(bytes);
	}

	public static List<String> localizarString(InputStream is,String pattern) throws IOException{
		PDFTextStripper stripper = new PDFTextStripper();
		PDDocument pdfDoc = PDDocument.load(is);
		String texto = stripper.getText(pdfDoc);
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(texto);
		List<String> texts = new ArrayList<String>();
		while (m.find( )) {
			texts.add(m.group(2));
		}
		pdfDoc.close();
		is.close();
		return texts;
	}

	public void close() throws IOException{
		pdfDocument.close();
	}

	public static void save(InputStream is, File targetFile) throws IOException{
		PDDocument pdfDoc = PDDocument.load(is);
		pdfDoc.save(targetFile);
		pdfDoc.close();
		is.close();
	}

	public static void merge(List<InputStream> inputStreams, File targetFile) throws IOException{
		PDFMergerUtility pdfMerger = new PDFMergerUtility();
		PDDocument mergedDoc = new PDDocument();
		for(InputStream is : inputStreams){
			PDDocument doc = PDDocument.load(is);
			pdfMerger.appendDocument(mergedDoc, doc);
			doc.close();
		}
		mergedDoc.save(targetFile);
		mergedDoc.close();
	}

	public PDDocument getPdfDocument() {
		return pdfDocument;
	} 

}
