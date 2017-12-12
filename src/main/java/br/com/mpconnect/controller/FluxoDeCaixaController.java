package br.com.mpconnect.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.bo.ContaBo;
import br.com.mpconnect.bo.FluxoDeCaixaBo;
import br.com.mpconnect.bo.SaldoBo;
import br.com.mpconnect.dao.ContaPagarDao;
import br.com.mpconnect.dao.ContaReceberDao;
import br.com.mpconnect.manager.ContaBancariaManagerBo;
import br.com.mpconnect.ml.api.enums.MesesEnum;
import br.com.mpconnect.util.DateUtils;

@Component
@Scope(value="view")
public class FluxoDeCaixaController {

	private List<ContaBo> pagamentos;

	private List<ContaBo> recebimentos;

	private List<SaldoBo> saldos;

	private List<String> meses;

	private List<Double> totaisPagamentos;

	private List<Double> totaisRecebimentos;

	private List<Double> resultadosOperacionais;
	
	private List<Double> saldosFinaisDeCaixa;

	private List<FluxoDeCaixaBo> fluxoDeCaixas;

	@Autowired
	public ContaPagarDao contaPagarDao;

	@Autowired
	public ContaReceberDao contaReceberDao;

	@Autowired
	private ContaBancariaManagerBo contaManager;

	public FluxoDeCaixaController(){

		pagamentos = new ArrayList<ContaBo>();
		recebimentos = new ArrayList<ContaBo>();
		saldos = new ArrayList<SaldoBo>();
		meses = new ArrayList<String>();
		totaisPagamentos = new ArrayList<Double>();
		totaisRecebimentos = new ArrayList<Double>();
		fluxoDeCaixas = new ArrayList<FluxoDeCaixaBo>();
		resultadosOperacionais = new ArrayList<Double>();
		saldosFinaisDeCaixa = new ArrayList<Double>();

	}

	@PostConstruct
	public void init(){

		MesesEnum[] mesesEnum = MesesEnum.values();
		for(int i=0; i < mesesEnum.length;i++)
			meses.add(mesesEnum[i].getValue());

		recebimentos = contaReceberDao.obterRecebimentosAgrupados();
		FluxoDeCaixaBo fluxoDeEntrada = populaFluxoDeCaixa(recebimentos, totaisRecebimentos,"Total das Entradas");
		fluxoDeCaixas.add(fluxoDeEntrada);

		pagamentos = contaPagarDao.obterDespesasAgrupadas();
		FluxoDeCaixaBo fluxoDeSaida = populaFluxoDeCaixa(pagamentos, totaisPagamentos,"Total das Saidas");
		fluxoDeCaixas.add(fluxoDeSaida);

		saldos = contaManager.retornaSaldosTotaisEmConta(DateUtils.getAno(new Date()));
//		if(saldos.size()<12){
//			int colunas = mesesEnum.length-saldos.size();
//			for(int i=0; i < colunas;i++){
//				SaldoBo saldo = new SaldoBo();
//				saldos.add(saldo);
//			}
//		}

		calculaResultadoOperacional();
		calculaSaldoFinalDeCaixa();
	}

	private FluxoDeCaixaBo populaFluxoDeCaixa(List<ContaBo> contas,List<Double> totais, String tipoFluxo){

		for(int i=1;i<=12;i++){
			Double total = 0.0;
			for(ContaBo conta : contas){
				Map<Integer,Double> valoresMensais = conta.getValoresMensais();
				Double valorMensal = (Double) valoresMensais.get(i);
				if(valorMensal!=null){
					total = total + (Double) valoresMensais.get(i);
				}
			}
			BigDecimal bd = new BigDecimal((Double) total);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			totais.add(bd.doubleValue());	
		}

		FluxoDeCaixaBo fluxoDeCaixa = new FluxoDeCaixaBo();
		fluxoDeCaixa.setContas(contas);
		fluxoDeCaixa.setTotais(totais);
		fluxoDeCaixa.setTipoFluxo(tipoFluxo);

		return fluxoDeCaixa;
	}

	private void calculaResultadoOperacional(){

		for(int index=0; index<totaisRecebimentos.size();index++){
			Double valor = totaisRecebimentos.get(index)-totaisPagamentos.get(index);
			BigDecimal bd = new BigDecimal((Double) valor);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			resultadosOperacionais.add(bd.doubleValue());
		}		
	}
	
	private void calculaSaldoFinalDeCaixa(){

		for(int index=0; index<meses.size();index++){
			SaldoBo saldo = saldos.get(index);
			double resultadoOperacional = resultadosOperacionais.get(index);
			double valor = resultadoOperacional+saldo.getValor();
			BigDecimal bd = new BigDecimal((Double) valor);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			saldosFinaisDeCaixa.add(bd.doubleValue());
			saldo = new SaldoBo();
			saldo.setValor(bd.doubleValue());
			saldo.setMes(index+2);
			saldos.add(saldo);
		}		
	}

	public List<ContaBo> getPagamentos() {
		return pagamentos;
	}

	public void setPagamentos(List<ContaBo> pagamentos) {
		this.pagamentos = pagamentos;
	}

	public List<String> getMeses() {
		return meses;
	}

	public void setMeses(List<String> meses) {
		this.meses = meses;
	}

	public List<ContaBo> getRecebimentos() {
		return recebimentos;
	}

	public void setRecebimentos(List<ContaBo> recebimentos) {
		this.recebimentos = recebimentos;
	}

	public List<Double> getTotaisPagamentos() {
		return totaisPagamentos;
	}

	public void setTotaisPagamentos(List<Double> totaisPagamentos) {
		this.totaisPagamentos = totaisPagamentos;
	}

	public List<Double> getTotaisRecebimentos() {
		return totaisRecebimentos;
	}

	public void setTotaisRecebimentos(List<Double> totaisRecebimentos) {
		this.totaisRecebimentos = totaisRecebimentos;
	}

	public List<FluxoDeCaixaBo> getFluxoDeCaixas() {
		return fluxoDeCaixas;
	}

	public void setFluxoDeCaixas(List<FluxoDeCaixaBo> fluxoDeCaixas) {
		this.fluxoDeCaixas = fluxoDeCaixas;
	}

	public List<Double> getResultadosOperacionais() {
		return resultadosOperacionais;
	}

	public void setResultadosOperacionais(List<Double> resultadosOperacionais) {
		this.resultadosOperacionais = resultadosOperacionais;
	}

	public List<SaldoBo> getSaldos() {
		return saldos;
	}

	public void setSaldos(List<SaldoBo> saldos) {
		this.saldos = saldos;
	}

	public List<Double> getSaldosFinaisDeCaixa() {
		return saldosFinaisDeCaixa;
	}

	public void setSaldosFinaisDeCaixa(List<Double> saldosFinaisDeCaixa) {
		this.saldosFinaisDeCaixa = saldosFinaisDeCaixa;
	}

}
