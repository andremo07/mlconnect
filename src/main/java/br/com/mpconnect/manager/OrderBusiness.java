package br.com.mpconnect.manager;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.model.Produto;
import br.com.mpconnect.model.Venda;
import br.com.trendsoftware.mlProvider.dto.ShippingStatus;
import br.com.trendsoftware.mlProvider.dto.ShippingSubStatus;

public interface OrderBusiness {
	
	public void save(String userId, String orderId) throws BusinessException;
	public void salvarVenda(Venda venda);
	public void cadastrarVendaUnitaria(Venda venda, Produto produto);
	public void loadOrdersByDate(Date fromDate, Date toDate) throws BusinessException;
	public Venda listOrderById(String id) throws BusinessException;
	public List<Venda> listOrdersByShippingStatus(ShippingStatus shippingStatus, ShippingSubStatus shippingSubStatus) throws BusinessException;
	public void atualizarVenda(Venda venda);
	public Venda recuperarVenda(String id);
	public Long getMaxIdVenda();
	public List<Venda> retornaVendasPorPerioSemNfe(Date dtIni,Date dtFinal);
	public InputStream generateNfeFileStream(List<Venda> vendas, String filePath) throws BusinessException;
}
