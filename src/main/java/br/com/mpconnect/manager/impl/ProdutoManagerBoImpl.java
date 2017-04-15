package br.com.mpconnect.manager.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.EnvioDao;
import br.com.mpconnect.dao.ProdutoDao;
import br.com.mpconnect.manager.ProdutoManagerBo;
import br.com.mpconnect.model.Envio;
import br.com.mpconnect.model.Produto;
import br.com.mpconnect.utils.DateUtils;

@Service("produtoManager")
public class ProdutoManagerBoImpl implements ProdutoManagerBo, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Resource
	public ProdutoDao produtoDao;

	@Resource
	public EnvioDao envioDao;

	@Override
	public Produto recuperaProdutoPorSku(String sku) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sku", sku);
			Produto produto = produtoDao.recuperaUmPorParams("from Produto p where p.sku=:sku",params);
			return produto;
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Transactional
	public void reporEstoqueProdutos(InputStream is, List<Produto> produtosAtualizadosList, List<Produto> produtosCadastradosList){

		try {
			XSSFWorkbook myWorkBook = new XSSFWorkbook(is);
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			Iterator<Row> rowIter = mySheet.rowIterator();
			int rowNum = 0;
			while(rowIter.hasNext()){
				if(rowNum>0){
					XSSFRow linha = (XSSFRow) rowIter.next();
					XSSFCell celulaSku = linha.getCell(1);
					XSSFCell celulaQtd = linha.getCell(2);
					String sku = celulaSku.getStringCellValue().replaceAll("\u00A0", "");
					System.out.println(sku);
					Double qtdItens = celulaQtd.getNumericCellValue();
					Produto produto = recuperaProdutoPorSku(sku);
					if(produto!=null){
						if(produto.getQuantidadeDisponivel()!=null && produto.getQuantidadeDisponivel()>=0){
							int qtd = produto.getQuantidadeDisponivel();
							qtd = qtd + qtdItens.intValue();
							produto.setQuantidadeDisponivel(qtd);
							produtoDao.alterar(produto);
							produtosAtualizadosList.add(produto);
						}
					}
					else{
						XSSFCell celulaNomeProduto = linha.getCell(0);
						String nomeProduto = celulaNomeProduto.getStringCellValue();
						produto = new Produto();
						produto.setNome(nomeProduto);
						produto.setSku(sku);
						produto.setQuantidadeDisponivel(qtdItens.intValue());
						produtoDao.gravar(produto);
						produtosCadastradosList.add(produto);
					}
				}
				else
					//PULA O CABEÇALHO
					rowIter.next();
				rowNum++;
			}
			myWorkBook.close();
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Transactional
	public void baixarEstoqueProdutos(InputStream is, List<Produto> produtosAtualizadosList){

		try {
			XSSFWorkbook myWorkBook = new XSSFWorkbook(is);
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			Iterator<Row> rowIter = mySheet.rowIterator();
			int rowNum = 0;
			Map<String,Produto> produtos = new HashMap<String,Produto>();
			while(rowIter.hasNext()){
				if(rowNum>0){
					XSSFRow linha = (XSSFRow) rowIter.next();
					XSSFCell celulaIdEnvio = linha.getCell(0);
					celulaIdEnvio.setCellType(CellType.STRING);
					String idEnvio = celulaIdEnvio.getStringCellValue().replaceAll("\u00A0", "");
					if(StringUtils.isNumeric(idEnvio)){
						Envio envio = envioDao.recuperaEnvioPorIdMl(idEnvio);
						if(envio!=null && envio.getData()==null){
							XSSFCell celulaSku = linha.getCell(2);
							String sku = celulaSku.getStringCellValue().replaceAll("\u00A0", "");
							Produto produto = null;
							if(produtos.containsKey(sku))
								produto = produtos.get(sku);
							else{
								produto = recuperaProdutoPorSku(sku);
							}
							if(produto!=null && produto.getQuantidadeDisponivel()!=null && produto.getQuantidadeDisponivel()>0){
								int qtd = produto.getQuantidadeDisponivel();
								qtd--;
								produto.setQuantidadeDisponivel(qtd);
								produtoDao.alterar(produto);
								envio.setData(new Date());
								//envio.setData(DateUtils.adicionaDias(new Date(), -1));
								envioDao.alterar(envio);
								if(!produtosAtualizadosList.contains(produto))
									produtosAtualizadosList.add(produto);
								produtos.put(produto.getSku(), produto);
							}
						}
					}
				}
				else
					//PULA O CABEÇALHO
					rowIter.next();
				rowNum++;
			}
			myWorkBook.close();
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
