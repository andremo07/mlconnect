package br.com.mpconnect.file.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
	
	private FileOutputStream fos;
	private ZipOutputStream zos;
	
	public ZipUtils(String fileName) throws FileNotFoundException{
		fos = new FileOutputStream(fileName);
		zos = new ZipOutputStream(new BufferedOutputStream(fos));
	}
	
	public ZipUtils(File file) throws FileNotFoundException{
		fos = new FileOutputStream(file);
		zos = new ZipOutputStream(new BufferedOutputStream(fos));
	}

	public void adicionarArquivo(String fileName, InputStream is) throws FileNotFoundException, IOException {

		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = is.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}
		zos.flush();
		zos.closeEntry();
		is.close();
	}
	
	public void finalizarGravacao() throws IOException{
		zos.finish();
		zos.close();
	}

}
