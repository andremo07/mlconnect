package br.com.mpconnect.ml.api;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.ml.data.NotificacaoML;

@Component
public class ApiNotificacoes{

	public final static String TOPICO_ITENS = "items";
	public final static String TOPICO_VENDAS = "orders";
	public final static String TOPICO_PERGUNTAS = "questions";
	public final static String TOPICO_PAGAMENTOS = "payments";
	
	@Autowired
	private ApiMl apiMl;
	
	public NotificacaoML parseJson(JSONObject jsonObj){

		try {
			String origem = jsonObj.getString("resource");
			String idUsuario = jsonObj.getString("user_id");
			String topico = jsonObj.getString("topic");
			String idAplicacao = jsonObj.getString("application_id");
			NotificacaoML notificacao = new NotificacaoML();
			notificacao.setOrigem(origem);
			notificacao.setIdUsuario(idUsuario);
			notificacao.setTopico(topico);
			notificacao.setIdAplicacao(idAplicacao);
			return notificacao;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
