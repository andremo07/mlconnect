package br.com.mpconnect.controller;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.OnOpen;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.impl.JSONEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.mpconnect.manager.FuncionarioManagerBo;
import br.com.mpconnect.ml.api.ApiNotificacoes;
import br.com.mpconnect.ml.dto.NotificacaoML;


@PushEndpoint("/notifications")
public class WebSocketTest {

	@Autowired
	private FuncionarioManagerBo manager;

	@OnMessage(encoders = {JSONEncoder.class})
	public FacesMessage onMessage(HttpServletRequest request) {
		
		FacesMessage message = null;
		try {
			String jsonString = httpServletRequestToString(request);
			JSONObject jsonObject = new JSONObject(jsonString);
			ApiNotificacoes apiNotificacoes = new ApiNotificacoes();
			NotificacaoML notificacao = apiNotificacoes.parseJson(jsonObject);
			if(notificacao.getTopico().equals(ApiNotificacoes.TOPICO_ITENS)){
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Você teve um item alterado");
			}else if(notificacao.getTopico().equals(ApiNotificacoes.TOPICO_PAGAMENTOS)){
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Você recebeu um novo pagamento");
			}else if (notificacao.getTopico().equals(ApiNotificacoes.TOPICO_PERGUNTAS)){
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Você recebeu uma nova pergunta");
			}else if (notificacao.getTopico().equals(ApiNotificacoes.TOPICO_VENDAS)){
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Você tem uma nova venda");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return message;
	}

	private String httpServletRequestToString(HttpServletRequest arg0){

		try {
			ServletInputStream mServletInputStream;

			mServletInputStream = arg0.getInputStream();

			byte[] httpInData = new byte[arg0.getContentLength()];
			int retVal = -1;
			StringBuilder stringBuilder = new StringBuilder();

			while ((retVal = mServletInputStream.read(httpInData)) != -1)
			{
				for (int i = 0; i < retVal; i++)
				{
					stringBuilder.append(Character.toString((char) httpInData[i]));
				}
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}


	@OnOpen
	public void onOpen(){

	}
}