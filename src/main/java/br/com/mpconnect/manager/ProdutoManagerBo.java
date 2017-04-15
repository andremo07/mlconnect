package br.com.mpconnect.manager;

import java.io.InputStream;
import java.util.List;

import br.com.mpconnect.model.Produto;

public interface ProdutoManagerBo {
	
	public Produto recuperaProdutoPorSku(String sku);
	public void reporEstoqueProdutos(InputStream is, List<Produto> produtosAtualizadosList, List<Produto> produtosCadastradosList);
	public void baixarEstoqueProdutos(InputStream is, List<Produto> produtosAtualizadosList);


}
