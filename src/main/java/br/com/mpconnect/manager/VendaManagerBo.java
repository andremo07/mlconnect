package br.com.mpconnect.manager;

import java.util.Set;

import br.com.mpconnect.ml.data.VendaML;
import br.com.mpconnect.model.Produto;
import br.com.mpconnect.model.Venda;



public interface VendaManagerBo {
	
	public Venda parseVendaMltoVenda(VendaML vendaMl);
	public void salvarVenda(Venda venda);
	public void cadastrarVenda(Venda venda);
	public void cadastrarVendaUnitaria(Venda venda, Produto produto);
	public void carregaVendasRecentes();
	public void atualizarVenda(Venda venda);
	public void atualizarCustosEnvios();
	public Venda recuperarVenda(String id);
	public Long getMaxIdVenda();
	public Set<VendaML> retornaVendasNaoExistentes(Set<VendaML> vendasMl);
	
}
