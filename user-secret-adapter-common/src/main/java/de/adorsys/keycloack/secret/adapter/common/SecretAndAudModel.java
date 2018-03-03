package de.adorsys.keycloack.secret.adapter.common;

import java.util.List;

public class SecretAndAudModel {
	private String userSecret;
	private List<String> audiances;
	public SecretAndAudModel(String userSecret, List<String> audiances) {
		super();
		this.userSecret = userSecret;
		this.audiances = audiances;
	}
	public String getUserSecret() {
		return userSecret;
	}
	public void setUserSecret(String userSecret) {
		this.userSecret = userSecret;
	}
	public List<String> getAudiances() {
		return audiances;
	}
	public void setAudiances(List<String> audiances) {
		this.audiances = audiances;
	}
}
