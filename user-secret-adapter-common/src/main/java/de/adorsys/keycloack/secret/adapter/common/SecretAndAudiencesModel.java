package de.adorsys.keycloack.secret.adapter.common;

import java.util.List;

public class SecretAndAudiencesModel {

    private String userSecret;
    private List<String> audiences;

    public SecretAndAudiencesModel(String userSecret, List<String> audiences) {
        super();
        this.userSecret = userSecret;
        this.audiences = audiences;
    }

    public String getUserSecret() {
        return userSecret;
    }

    public void setUserSecret(String userSecret) {
        this.userSecret = userSecret;
    }

    public List<String> getAudiences() {
        return audiences;
    }

    public void setAudiences(List<String> audiences) {
        this.audiences = audiences;
    }
}
