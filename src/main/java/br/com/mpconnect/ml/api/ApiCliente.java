package br.com.mpconnect.ml.api;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.ml.dto.ClienteML;

@Component
public class ApiCliente{
	
	@Autowired
	private ApiMl apiMl;
	
	public ClienteML parseJson(JSONObject jsonObj){

		try {
			ClienteML cliente = new ClienteML();
			cliente.setId(jsonObj.getString("id"));
			cliente.setApelido(jsonObj.getString("nickname"));
			cliente.setEmail(jsonObj.getString("email"));
			cliente.setTelefone(jsonObj.getJSONObject("phone").getString("number"));
			cliente.setArea(jsonObj.getJSONObject("phone").getString("area_code"));
			cliente.setPrimeiroNome(jsonObj.getString("first_name"));
			cliente.setUltimoNome(jsonObj.getString("last_name"));
			cliente.setTipoDocumento(jsonObj.getJSONObject("billing_info").getString("doc_type"));
			cliente.setNumeroDocumento(jsonObj.getJSONObject("billing_info").getString("doc_number"));
			return cliente;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
