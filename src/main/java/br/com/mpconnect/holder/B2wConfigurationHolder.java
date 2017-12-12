package br.com.mpconnect.holder;

public class B2wConfigurationHolder
{	
	private static B2wConfigurationHolder instance;
	
    private String userEmail;
    private String apiKey;
    private String accountManagerKey;
	
	private B2wConfigurationHolder(String userEmail, String apiKey,String accountManagerKey) {
		this.userEmail = userEmail;
		this.apiKey = apiKey;
		this.accountManagerKey = accountManagerKey;
	}
		
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getAccountManagerKey() {
		return accountManagerKey;
	}

	public void setAccountManagerKey(String accountManagerKey) {
		this.accountManagerKey = accountManagerKey;
	}

	public static B2wConfigurationHolder getInstance() {
		return instance;
	}

	public static B2wConfigurationHolder setInstance(String userEmail, String apiKey,String accountManagerKey) {
		B2wConfigurationHolder.instance = new B2wConfigurationHolder(userEmail,apiKey,accountManagerKey);
		return B2wConfigurationHolder.instance;
	}
}
