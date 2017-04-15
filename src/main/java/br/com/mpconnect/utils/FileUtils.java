package br.com.mpconnect.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileUtils {

	public void criarArquivoTexto(String conteudo, String path){
		try {	
			File file = new File(path);
			if(!file.exists())
				file.createNewFile();
			FileWriter arquivo = new FileWriter(file);  
			PrintWriter gravarArq = new PrintWriter(arquivo);
			gravarArq.print(conteudo);
			gravarArq.close();
			arquivo.close(); 
		} catch (IOException e) {  
			e.printStackTrace();  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  

	}

	public String lerArquivoTexto(String path){
		
		String resultado="";
		
		try {
			
			Scanner scanner = new Scanner(new FileReader(path));
			while (scanner.hasNextLine()) {
				resultado = resultado + scanner.nextLine() + System.getProperty("line.separator");
			}
			scanner.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultado;
	}
}
