package br.com.mpconnect.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="ACESSO_ML")
public class AcessoMl implements Persistente{
	
	@Transient
	private String idMl;
	
	@Id
	@Column(name="CLIENT_ID")
	private Long clientId;

	@Column(name="CLIENT_SECRET")
	private String clientSecret;
	
	@Column(name="ACCESS_TOKEN")
	private String accessToken;
	
	@Column(name="REFRESH_TOKEN")
	private String refreshToken;

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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public String getIdMl() {
		return idMl;
	}

	public void setIdMl(String idMl) {
		this.idMl = idMl;
	}

	public Long getId() {
		return this.getClientId();
	}

}
