package br.com.mpconnect.ml.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mercadolibre.sdk.MeliException;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;

import br.com.mpconnect.ml.dto.PerguntaML;
import br.com.mpconnect.util.DateUtils;
import br.com.mpconnect.utils.comparator.PerguntaMLComparator;

@Component
public class ApiPerguntas{

	@Autowired
	private ApiMl apiMl;
	
	public List<PerguntaML> recuperarPerguntasVenda(String itemId, String clientId){
		try {

			FluentStringsMap params = new FluentStringsMap();
			params.add("access_token", apiMl.getMe().getAccessToken());
			params.add("item", itemId);
			params.add("from", clientId);
			Response meliResponse = apiMl.getMe().get("/questions/search",params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			JSONArray perguntas = jsonObject.getJSONArray("questions");

			List<PerguntaML> perguntasMl = new ArrayList<PerguntaML>();
			for (int index=0;index < perguntas.length();index++){
				JSONObject jsonObj = perguntas.getJSONObject(index);
				PerguntaML mensagem = parseJsonToPerguntaML(jsonObj);
				perguntasMl.add(mensagem);
			}			
			Collections.sort(perguntasMl,new PerguntaMLComparator());
			return perguntasMl;

		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public PerguntaML parseJsonToPerguntaML(JSONObject jsonObj){

		try {

			String dataString = jsonObj.getString("date_created");
			Date data = DateUtils.getDataFormatada(dataString,"yyyy-MM-dd'T'HH:mm:ss");
			String textoPergunta = jsonObj.getString("text");
			String textoResposta="";
			if(jsonObj.has("answer") && !jsonObj.isNull("answer"))
				textoResposta = jsonObj.getJSONObject("answer").getString("text");
			PerguntaML pergunta = new PerguntaML(data, textoPergunta, textoResposta);
			return pergunta;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


}
