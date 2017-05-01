package br.com.mpconnect.ml.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mercadolibre.sdk.MeliException;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;

import br.com.mpconnect.ml.api.enums.StatusAnuncioEnum;
import br.com.mpconnect.ml.data.AnuncioML;
import br.com.mpconnect.ml.data.MetodoEnvioML;
import br.com.mpconnect.ml.data.PictureML;
import br.com.mpconnect.ml.data.TipoAnuncioML;
import br.com.mpconnect.ml.data.ValorVariacaoML;
import br.com.mpconnect.ml.data.VariacaoML;
import br.com.mpconnect.utils.DateUtils;

@Component
public class ApiProdutos{

	@Autowired
	private ApiMl apiMl;

	public List<AnuncioML> recuperaAnuncios(String idUsuario){

		List<AnuncioML> anuncios = new ArrayList<AnuncioML>();
		Set<String> idsAnuncios = null;
		int totalAnuncios = this.totalAnuncios(idUsuario,null);
		idsAnuncios = getListIdsAnuncios(idsAnuncios, totalAnuncios, idUsuario,0,null);

		for (String idAnuncio: idsAnuncios){
			AnuncioML anuncio = getAnuncioPorId(idAnuncio);
			anuncios.add(anuncio);
		}

		return anuncios;
	}

	public List<AnuncioML> recuperaAnunciosCadastrados(String idUsuario, StatusAnuncioEnum statusEnum){

		List<String> idsAnuncios = recuperaIdsAnunciosCadastrados(idUsuario,statusEnum);
		List<AnuncioML> anuncios = new ArrayList<AnuncioML>();
		for (String idAnuncio: idsAnuncios){
			AnuncioML anuncio = getAnuncioPorId(idAnuncio);
			anuncios.add(anuncio);
		}

		return anuncios;
	}

	public List<String> recuperaIdsAnunciosCadastrados(String idUsuario, StatusAnuncioEnum statusEnum){

		FluentStringsMap params = new FluentStringsMap();
		params.add("status", statusEnum.getValue());

		Set<String> idsAnuncios = null;
		int totalAnuncios = this.totalAnuncios(idUsuario,params);
		idsAnuncios = getListIdsAnuncios(idsAnuncios, totalAnuncios, idUsuario,0,params);

		List<String> listIdsAnuncios = new ArrayList<String>();
		listIdsAnuncios.addAll(idsAnuncios);
		return listIdsAnuncios;
	}


	public void salvarAnuncio(AnuncioML anuncio){

		try {

			FluentStringsMap params = new FluentStringsMap();
			params.add("access_token", apiMl.getMe().getAccessToken());
			JSONObject parametrosJson = parseAnuncio(anuncio);
			Response meliResponse = apiMl.getMe().post("/items/validate", params, parametrosJson.toString());
			if(meliResponse.getStatusCode()== ApiMl.RESPONSE_STATUS_CODE_SUCCESS){
				meliResponse = apiMl.getMe().post("/items", params, parametrosJson.toString());
				JSONObject jsonResponse = new JSONObject(meliResponse.getResponseBody());
			}
			else{
				JSONObject jsonResponse = new JSONObject(meliResponse.getResponseBody());
				String message = (String) jsonResponse.get("message");
				System.out.println(message);
			}

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
	}

	public void atualizarAnuncio(AnuncioML anuncioMl,JSONObject parametrosAlterados){
		try {
			FluentStringsMap params = new FluentStringsMap();
			params.add("access_token", apiMl.getMe().getAccessToken());
			Response meliResponse = apiMl.getMe().put("/items/"+anuncioMl.getId(), params, parametrosAlterados.toString());
			JSONObject jsonResponse = new JSONObject(meliResponse.getResponseBody());
			System.out.println();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		System.out.println();
	}





	public void testeAtualizacao(){

		try {
			AnuncioML anuncio = getAnuncioPorId("MLB846260329");			
			boolean isCustomizavel = true;

			JSONArray listaVariacoes = null;
			
			List<ValorVariacaoML> valoresVariacao = new ArrayList<ValorVariacaoML>();
			
			ValorVariacaoML valorVariacaoML2 = new ValorVariacaoML();
			valorVariacaoML2.setNomeVariacao("Cor");
			valorVariacaoML2.setNome("Gunmetal");
			
			ValorVariacaoML valorVariacaoML3 = new ValorVariacaoML();
			valorVariacaoML3.setNomeVariacao("Cor");
			valorVariacaoML3.setNome("Satin Silver");
			
			ValorVariacaoML valorVariacaoML4 = new ValorVariacaoML();
			valorVariacaoML4.setNomeVariacao("Cor");
			valorVariacaoML4.setNome("Champagne Gold");
			
			ValorVariacaoML valorVariacaoML5 = new ValorVariacaoML();
			valorVariacaoML5.setNomeVariacao("Cor");
			valorVariacaoML5.setNome("Black");
			
			ValorVariacaoML valorVariacaoML6 = new ValorVariacaoML();
			valorVariacaoML6.setNomeVariacao("Cor");
			valorVariacaoML6.setNome("Rose Gold");
			
			valoresVariacao.add(valorVariacaoML2);
			//valoresVariacao.add(valorVariacaoML3);
			//valoresVariacao.add(valorVariacaoML4);
			valoresVariacao.add(valorVariacaoML5);
			valoresVariacao.add(valorVariacaoML6);
			
			List<VariacaoML> variacoes = criarVariacao(anuncio, valoresVariacao);
			
			anuncio.setVariacoes(variacoes);
			
			if(isCustomizavel)
				listaVariacoes = parseVariacoesToJsonArray(anuncio.getVariacoes());
			else
				System.out.println();
				//listaVariacoes = criarVariacoes(variacao.getId(), anuncio, valoresVariacao);

			JSONObject parametrosAlterados = new JSONObject();
			parametrosAlterados.put("variations", listaVariacoes);
			atualizarAnuncio(anuncio,parametrosAlterados);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<VariacaoML> criarVariacao(AnuncioML anuncioML, List<ValorVariacaoML> valoresVariacao){
		
		List<VariacaoML> variacoes = new ArrayList<VariacaoML>();
		for(ValorVariacaoML valorVariacaoML: valoresVariacao){
			VariacaoML variacao = new VariacaoML();
			List<ValorVariacaoML> vv = new ArrayList<ValorVariacaoML>();
			vv.add(valorVariacaoML);
			variacao.setPreco(anuncioML.getValor());
			variacao.setCodRefProduto("A123456");
			variacao.setPictures(anuncioML.getPictures());
			variacao.setQtdDisponivel(1);
			variacao.setValores(vv);
			variacoes.add(variacao);
		}
		
		return variacoes;
	}

	public void adicionaDescricao(String idAnuncio, String html){
		try {
			FluentStringsMap params = new FluentStringsMap();
			params.add("access_token", apiMl.getMe().getAccessToken());

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("description", html);


			Response meliResponse = apiMl.getMe().put("/items/"+idAnuncio+"/descriptions", params, jsonObj.toString());
			JSONObject jsonResponse = new JSONObject(meliResponse.getResponseBody());


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public AnuncioML getAnuncioPorId(String idAnuncio){

		try {

			Response meliResponse = apiMl.getMe().get("/items/"+idAnuncio);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			AnuncioML anuncio = parseJson(jsonObject);
			return anuncio;
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

	public Integer getTotalVisitasPorAnuncio(String idAnuncio,Integer dias){

		try {
			FluentStringsMap params = new FluentStringsMap();
			params.add("last", "1");
			params.add("unit", "day");
			params.add("ending", DateUtils.getDataAnteriorString(dias-1));
			Response meliResponse = apiMl.getMe().get("/items/"+idAnuncio+"/visits/time_window",params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			Integer totalVisitas = jsonObject.getInt("total_visits");
			return totalVisitas;
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

	public JSONObject getDescricaoAnuncio(String idAnuncio){

		try {

			Response meliResponse = apiMl.getMe().get("/items/"+idAnuncio+"/descriptions/MLB704220068-932591798");
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			return jsonObject;
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

	public Set<String> getListIdsAnuncios(Set<String> stringIdsAnuncios,int totalAnuncios ,String idVendedor, Integer offset, FluentStringsMap params){

		try{
			if(params==null)
				params = new FluentStringsMap();

			if(!params.containsKey("access_token"))
				params.add("access_token", apiMl.getMe().getAccessToken());

			if(!params.containsKey("limit"))
				params.add("limit", "50");

			if(!params.containsKey("offset"))
				params.add("offset", offset.toString());

			Response meliResponse = apiMl.getMe().get("/users/"+idVendedor+"/items/search",params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			JSONArray idsAnuncios = jsonObject.getJSONArray("results");
			if(stringIdsAnuncios==null)
				stringIdsAnuncios = new HashSet<String>();
			for (int index=0;index < idsAnuncios.length();index++){
				String idAnuncio = idsAnuncios.getString(index);
				stringIdsAnuncios.add(idAnuncio);
			}

			if(totalAnuncios > stringIdsAnuncios.size()){
				offset = offset+50;
				String[] values = new String[1];
				values[0]=offset.toString();
				params.replace("offset", values);
				stringIdsAnuncios.addAll(getListIdsAnuncios(stringIdsAnuncios, totalAnuncios, idVendedor,offset,params));
			}

			return stringIdsAnuncios;

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

	public Integer totalAnuncios(String idVendedor, FluentStringsMap params){
		try{
			if(params==null)
				params = new FluentStringsMap();
			params.add("access_token", apiMl.getMe().getAccessToken());
			Response meliResponse = apiMl.getMe().get("/users/"+idVendedor+"/items/search",params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			JSONObject jsonObjectPaging = (JSONObject) jsonObject.get("paging");
			Integer total = (Integer) jsonObjectPaging.get("total");
			return total;

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

	public AnuncioML parseJson(JSONObject jsonObj){

		try {
			String idAnuncio = jsonObj.getString("id");
			String nmAnuncio = jsonObj.getString("title");
			String idCategoria = jsonObj.getString("category_id");
			String status = jsonObj.getString("status");
			Double valor = jsonObj.getDouble("price");
			Integer quantidade = jsonObj.getInt("available_quantity");
			String descricao = jsonObj.getString("descriptions");
			TipoAnuncioML tipoAnuncio = new TipoAnuncioML();
			tipoAnuncio.setNome(jsonObj.getString("listing_type_id"));
			tipoAnuncio.setId(jsonObj.getString("listing_type_id"));
			
			MetodoEnvioML metodo = null;
			if(jsonObj.has("shipping")){
				metodo = new MetodoEnvioML();
				JSONObject shippingJsonObj = jsonObj.getJSONObject("shipping");
				String modo = shippingJsonObj.getString("mode");
				boolean retiradaLocal = shippingJsonObj.getBoolean("local_pick_up");
				boolean freteGratis = shippingJsonObj.getBoolean("free_shipping");
				if(shippingJsonObj.has("free_methods")){
					JSONArray metodosFreteGratis = shippingJsonObj.getJSONArray("free_methods");
					for(int index=0; index<metodosFreteGratis.length(); index++){
						JSONObject jo = metodosFreteGratis.getJSONObject(index);
						String id = jo.getString("id");
						if(jo.has("rule")){
							JSONObject jsRule = jo.getJSONObject("rule");
							String metodoGratisModo = jsRule.getString("free_mode");
							if(jsRule.has("value")){
								List<String> metodoGratisValores = new ArrayList<String>();
								JSONArray valueArray = jsRule.getJSONArray("value");
								for(index=0; index<valueArray.length(); index++){
									String value = valueArray.getString(index);
									metodoGratisValores.add(value);
								}
								metodo.setMetodoGratisValores(metodoGratisValores);
							}
							metodo.setMetodoGratisModo(metodoGratisModo);
						}
						metodo.setMetodoGratisId(id);
					}
				}
				metodo.setFreteGratis(freteGratis);
				metodo.setModoEnvio(modo);
				metodo.setRetiradaLocal(retiradaLocal);
			}

			JSONArray picturesJArray=jsonObj.getJSONArray("pictures");
			List<PictureML> pictures = new ArrayList<PictureML>();
			for(int index=0; index<picturesJArray.length(); index++){
				JSONObject jsonObject = picturesJArray.getJSONObject(index);
				PictureML pic = new PictureML();
				String id = jsonObject.getString("id");
				String url = jsonObject.getString("url");
				pic.setId(id);
				pic.setUrl(url);
				pictures.add(pic);
			}

			JSONArray variationsJArray=jsonObj.getJSONArray("variations");
			List<VariacaoML> variacoes = new ArrayList<VariacaoML>();
			for(int index=0; index<variationsJArray.length(); index++){
				JSONObject jsonObject = variationsJArray.getJSONObject(index);
				VariacaoML variacao = parseJsonToVariacaoML(jsonObject);
				variacoes.add(variacao);
			}

			AnuncioML anuncio = new AnuncioML(idAnuncio, nmAnuncio, valor, idCategoria, quantidade,status,descricao);
			anuncio.setTipo(tipoAnuncio);
			anuncio.setPictures(pictures);
			anuncio.setVariacoes(variacoes);
			anuncio.setMetodoEnvio(metodo);
			return anuncio;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject parseAnuncio(AnuncioML anuncio){

		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("title",anuncio.getTitulo());
			jsonObj.put("category_id",anuncio.getIdCategoria());
			jsonObj.put("price",anuncio.getValor().toString());
			jsonObj.put("currency_id","BRL");
			jsonObj.put("available_quantity",anuncio.getQuantidade());
			jsonObj.put("buying_mode","buy_it_now");
			jsonObj.put("listing_type_id",anuncio.getTipo().getId());
			jsonObj.put("condition","new");
			jsonObj.put("description", anuncio.getHtml());
			jsonObj.put("warranty","Contra defeitos de fabricação");

			List<PictureML> pictures = anuncio.getPictures();
			if(pictures!=null && !pictures.isEmpty()){
				List<JSONObject> picturesJo = new ArrayList<JSONObject>();
				for(PictureML pic: pictures){
					JSONObject pictureJsonObj = new JSONObject();
					pictureJsonObj.put("source", pic.getSource());
					picturesJo.add(pictureJsonObj);
				}
				jsonObj.put("pictures", picturesJo);
			}
			
			if(anuncio.getMetodoEnvio()!=null){
				JSONObject shippingJo = parseMetodoEnvioToJson(anuncio.getMetodoEnvio());
				jsonObj.put("shipping", shippingJo);
			}
			
			if(anuncio.getVariacoes()!=null && !anuncio.getVariacoes().isEmpty()){
				JSONArray variacoes = parseVariacoesToJsonArray(anuncio.getVariacoes());
				jsonObj.put("variations", variacoes);
			}
			

			return jsonObj;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public VariacaoML parseJsonToVariacaoMLFromCategoriaML(JSONObject jsonObject){

		try {
			String idStrg = jsonObject.getString("id");
			String nomeVariacao = jsonObject.getString("name");
			String categoria = null;
			if(jsonObject.has("type"))
				categoria = jsonObject.getString("type");

			String tipoValor = jsonObject.getString("value_type");
			List<ValorVariacaoML> valores = null;
			if(jsonObject.has("values")){
				JSONArray jsonArray = jsonObject.getJSONArray("values");
				valores = new ArrayList<ValorVariacaoML>();
				for(int index=0; index<jsonArray.length(); index++){
					JSONObject jo = jsonArray.getJSONObject(index);
					String idValoVariacaoStrg = jo.getString("id");
					String nomeValorVariacao = jo.getString("name");
					ValorVariacaoML vv = new ValorVariacaoML();
					vv.setId(idValoVariacaoStrg);
					vv.setNome(nomeValorVariacao);
					valores.add(vv);
				}
			}
			VariacaoML variacao = new VariacaoML();
			variacao.setId(idStrg);
			variacao.setValores(valores);

			return variacao;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	public VariacaoML parseJsonToVariacaoML(JSONObject jsonObject){

		try {

			String idStrg = jsonObject.getString("id");
			Double preco = jsonObject.getDouble("price");
			JSONArray jsonArray = jsonObject.getJSONArray("attribute_combinations");
			List<ValorVariacaoML> valores = new ArrayList<ValorVariacaoML>();
			for(int index=0; index<jsonArray.length(); index++){
				JSONObject jo = jsonArray.getJSONObject(index);
				String idValorVariacaoStrg = jo.getString("value_id");
				String nomeVariacao = jo.getString("name");
				String nomeValorVariacao = jo.getString("value_name");
				ValorVariacaoML vv = new ValorVariacaoML();
				vv.setId(idValorVariacaoStrg);
				vv.setNome(nomeValorVariacao);
				vv.setNomeVariacao(nomeVariacao);
				valores.add(vv);
			}

			Integer qtdDisp = jsonObject.getInt("available_quantity");
			Integer qtdVendida = jsonObject.getInt("sold_quantity");
			String codRefProd = jsonObject.getString("catalog_product_id");

			List<PictureML> pictures = new ArrayList<PictureML>();
			JSONArray jsonArrayPics = jsonObject.getJSONArray("picture_ids");
			for(int index=0; index<jsonArrayPics.length(); index++){
				String picId = jsonArrayPics.getString(index);
				PictureML pic = new PictureML();
				pic.setId(picId);
				pictures.add(pic);
			}

			VariacaoML variacao = new VariacaoML();
			variacao.setId(idStrg);
			variacao.setPreco(preco);
			variacao.setQtdDisponivel(qtdDisp);
			variacao.setQtdVendida(qtdVendida);
			variacao.setValores(valores);
			if(codRefProd!=null && !codRefProd.equals("null"))
				variacao.setCodRefProduto(codRefProd);
			variacao.setPictures(pictures);

			return variacao;

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	public List<TipoAnuncioML> retornaTipoAnuncios(){
		try {
			List<TipoAnuncioML> tiposAnuncios = new ArrayList<TipoAnuncioML>();
			Response meliResponse = apiMl.getMe().get("/sites/MLB/listing_types");
			JSONArray jsonArray = new JSONArray(meliResponse.getResponseBody());
			for(int index=0; index<jsonArray.length(); index++){
				JSONObject jsonObject = jsonArray.getJSONObject(index);
				TipoAnuncioML tipoAnuncio = new TipoAnuncioML();
				tipoAnuncio.setId(jsonObject.getString("id"));
				tipoAnuncio.setNome(jsonObject.getString("name"));
				tiposAnuncios.add(tipoAnuncio);
			}
			return tiposAnuncios;
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

	public Map<String,VariacaoML> retornaVariacoesPossiveisPorCategoria(String idCategoria){
		try {
			Response meliResponse = apiMl.getMe().get("/categories/"+idCategoria+"/attributes/");
			JSONArray jsonArray = new JSONArray(meliResponse.getResponseBody());
			Map<String,VariacaoML> variacoes = new HashMap<String,VariacaoML>();
			for(int index=0; index<jsonArray.length(); index++){
				JSONObject jsonObject = jsonArray.getJSONObject(index);
				VariacaoML variacao = parseJsonToVariacaoMLFromCategoriaML(jsonObject);
				variacoes.put(variacao.getId(),variacao);
			}
			return variacoes;
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

	public JSONArray criarVariacoes(String idVariacao,AnuncioML anuncio,List<ValorVariacaoML> valoresVariacao){

		try {
			JSONArray variacoes = new JSONArray();
			List<String> links = new ArrayList<String>();
			links.add("12588-MLB20062538738_032014");
			links.add("12569-MLB20062538747_032014");
			links.add("12601-MLB20062539173_032014");
			links.add("12607-MLB20062539226_032014");
			int index=0;
			for(ValorVariacaoML valorVariacaoML: valoresVariacao){
				JSONObject atributos = new JSONObject();
				atributos.put("available_quantity", 1);
				atributos.put("price", anuncio.getValor());
				JSONArray combinacoesAtributos = new JSONArray();
				JSONArray cores = new JSONArray();
				JSONObject combinacaoAtributo = new JSONObject();
				combinacaoAtributo.put("id", idVariacao);
				combinacaoAtributo.put("value_id", valorVariacaoML.getId());
				combinacoesAtributos.put(combinacaoAtributo);
				atributos.put("attribute_combinations", combinacoesAtributos);
				cores.put(links.get(index));
				atributos.put("picture_ids", cores);
				variacoes.put(atributos);
				index++;
			}
			return variacoes;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public JSONObject parseMetodoEnvioToJson(MetodoEnvioML metodo) throws JSONException{
		
		JSONObject shippingJo = new JSONObject();
		shippingJo.put("mode", metodo.getModoEnvio());
		shippingJo.put("local_pick_up", metodo.getRetiradaLocal());
		shippingJo.put("free_shipping", metodo.getFreteGratis());
		JSONArray freeMethods=null;
		if(metodo.getFreteGratis()){
		  freeMethods = new JSONArray();
		  JSONObject freeMethod = new JSONObject();
		  freeMethod.put("id", metodo.getMetodoGratisId());
		  JSONObject rule = new JSONObject();
		  rule.put("free_mode", metodo.getMetodoGratisModo());
		  JSONArray valueJa = new JSONArray();
		  for(String valor : metodo.getMetodoGratisValores()){
			  valueJa.put(valor);
		  }
		  rule.put("value", valueJa);
		  freeMethod.put("rule", rule);
		  freeMethods.put(freeMethod);
		}
		shippingJo.put("free_methods", freeMethods);
		return shippingJo;
	}

	public JSONArray parseVariacoesToJsonArray(List<VariacaoML> variacoesMl){

		try {
			JSONArray variacoes = new JSONArray();
			for(VariacaoML variacaoML: variacoesMl){
				List<ValorVariacaoML> valoresVariacao = variacaoML.getValores();
				JSONObject atributos = new JSONObject();
				atributos.put("available_quantity", variacaoML.getQtdDisponivel());
				atributos.put("price", variacaoML.getPreco());
				JSONArray combinacoesAtributos = new JSONArray();
				for(ValorVariacaoML valorVariacaoML: valoresVariacao){
					JSONObject combinacaoAtributo = new JSONObject();
					combinacaoAtributo.put("name", valorVariacaoML.getNomeVariacao());
					combinacaoAtributo.put("value_name", valorVariacaoML.getNome());
					combinacoesAtributos.put(combinacaoAtributo);					
				}
				atributos.put("attribute_combinations", combinacoesAtributos);
				
				if(variacaoML.getPictures()!=null && !variacaoML.getPictures().isEmpty()){
					JSONArray cores = new JSONArray();
					for(PictureML pic: variacaoML.getPictures()){
						if(pic.getId()!=null)
							cores.put(pic.getId());
						else
							cores.put(pic.getSource());
					}
					atributos.put("picture_ids", cores);
				}
				
				if(variacaoML.getCodRefProduto()!=null && !variacaoML.getCodRefProduto().equals("null"))
					atributos.put("seller_custom_field", variacaoML.getCodRefProduto());
				
				variacoes.put(atributos);
			}
			return variacoes;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}



}
