package br.com.mpconnect.holder;

public class MeliConfigurationHolder
{	
	private static MeliConfigurationHolder instance;
	
	private Long clientId;
	private String clientSecret; 
	
	private MeliConfigurationHolder(Long clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}
	
	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
	public static MeliConfigurationHolder getInstance() {
		return instance;
	}

	public static MeliConfigurationHolder setInstance(Long clientId, String clientSecret) {
		MeliConfigurationHolder.instance = new MeliConfigurationHolder(clientId,clientSecret);
		return MeliConfigurationHolder.instance;
	}
}
