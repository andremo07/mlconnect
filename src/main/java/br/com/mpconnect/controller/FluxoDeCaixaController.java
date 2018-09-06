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

import br.com.mpconnect.model.MesesEnum;
import br.com.trendsoftware.markethub.business.FluxoCaixaBusiness;
import br.com.trendsoftware.markethub.dto.ContaBo;
import br.com.trendsoftware.markethub.dto.FluxoDeCaixaBo;
import br.com.trendsoftware.markethub.dto.SaldoBo;
import br.com.trendsoftware.markethub.utils.DateUtils;

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
	public FluxoCaixaBusiness fluxoCaixaBusiness;

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

		recebimentos = fluxoCaixaBusiness.obterRecebimentosAnuais(DateUtils.getAno(new Date()));
		FluxoDeCaixaBo fluxoDeEntrada = populaFluxoDeCaixa(recebimentos, totaisRecebimentos,"Total das Entradas");
		fluxoDeCaixas.add(fluxoDeEntrada);

		pagamentos = fluxoCaixaBusiness.obterDespesasAnuais(DateUtils.getAno(new Date()));
		FluxoDeCaixaBo fluxoDeSaida = populaFluxoDeCaixa(pagamentos, totaisPagamentos,"Total das Saidas");
		fluxoDeCaixas.add(fluxoDeSaida);

		calculaSaldoInicial();
		calculaResultadoOperacional();
		calculaSaldoFinalDeCaixa();
	}

	private FluxoDeCaixaBo populaFluxoDeCaixa(List<ContaBo> contas,List<Double> totais, String tipoFluxo){

		for(int i=1;i<=12;i++){
			Double total = 0.0;
			for(ContaBo conta : contas){
				Map<Integer,Double> valoresMensais = conta.getValoresMensais();
				Double valorMensal = valoresMensais.get(i);
				if(valorMensal!=null){
					total = total + valoresMensais.get(i);
				}
			}
			BigDecimal bd = new BigDecimal(total);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			totais.add(bd.doubleValue());	
		}

		FluxoDeCaixaBo fluxoDeCaixa = new FluxoDeCaixaBo();
		fluxoDeCaixa.setContas(contas);
		fluxoDeCaixa.setTotais(totais);
		fluxoDeCaixa.setTipoFluxo(tipoFluxo);

		return fluxoDeCaixa;
	}

	private void calculaSaldoInicial(){
		Date dtAtual = new Date();
		double totalEmConta = fluxoCaixaBusiness.obterSaldoTotalEmConta();
		double totalRecebido = fluxoCaixaBusiness.obterTotalRecebimentosPorMes(DateUtils.getAno(dtAtual)-1);
		double totalGasto = fluxoCaixaBusiness.obterTotalDespesasMes(DateUtils.getAno(dtAtual)-1);
		BigDecimal bd = new BigDecimal(totalEmConta+(totalRecebido-totalGasto));
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		SaldoBo saldoIncial = new SaldoBo(0,bd.doubleValue());
		saldos.add(saldoIncial);
	}

	private void calculaResultadoOperacional(){

		for(int index=0; index<totaisRecebimentos.size();index++){
			Double valor = totaisRecebimentos.get(index)-totaisPagamentos.get(index);
			BigDecimal bd = new BigDecimal(valor);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			resultadosOperacionais.add(bd.doubleValue());
		}		
	}

	private void calculaSaldoFinalDeCaixa(){

		for(int index=0; index<meses.size();index++){
			SaldoBo saldo = saldos.get(index);
			double resultadoOperacional = resultadosOperacionais.get(index);
			double valor = resultadoOperacional+saldo.getValor();
			BigDecimal bd = new BigDecimal(valor);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			saldosFinaisDeCaixa.add(bd.doubleValue());
			saldo = new SaldoBo(index+2,bd.doubleValue());
			saldos.add(saldo);
		}
		
		saldos.remove(meses.size());
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
