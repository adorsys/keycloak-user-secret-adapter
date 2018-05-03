package de.adorsys.sts.keycloack.secret.adapter.embedded;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.adorsys.envutils.EnvProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.service.ResourceServerManagementProperties;

/**
 * Reads resource server configuration from environment.
 * 
 * - First tries to find a properties file, with resource servers - Then tries
 * to parse it directly fron environment properties.
 * 
 * @author fpo
 *
 */
public class EnvPropsResourceServerManagementProperties implements ResourceServerManagementProperties {

	public static final String ENV_STS_RESOURCE_SERVER_PREFIX = "STS_RESOURCE_SERVERS";
	public static final String PROPS_STS_RESOURCE_SERVER_PREFIX = "sts.resourceServers";

    private List<ResourceServer> resourceServers;
    private ResourceRetrieverProperties resourceRetrieverProperties;

	@Override
	public List<ResourceServer> getResourceServers() {
		return resourceServers;
	}

	@Override
	public ResourceRetrieverProperties getResourceRetriever() {
		return resourceRetrieverProperties;
	}
	
	public EnvPropsResourceServerManagementProperties() {
		discoverConfig();
	}

	private void discoverConfig() {
		String configFile = EnvProperties.getEnvOrSysProp("STS_RESOURCE_SERVERS_CONFIG_FILE", true);

		if (configFile != null) {
			File file = new File(configFile);
			if (file.exists()) {
				Properties properties = new Properties();
				try {
					properties.load(new FileInputStream(file));
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
				readFromPropertiesFile(properties);
			}
		}

		if (resourceServers == null) {
			readFromPropertiesEnv();
		}
	}

	private void readFromPropertiesFile(Properties properties) {
		String res_servers_prop = properties.getProperty("sts.resourceServer.list", "");
		String[] split = StringUtils.split(res_servers_prop, ",");
		resourceServers = new ArrayList<>();
		for (String resourceServerName : split) {
			if (StringUtils.isBlank(resourceServerName))
				continue;
			ResourceServer resServer = retrieveResourceServerFromProperties(properties, resourceServerName);
			resourceServers.add(resServer);
		}
		
		String httpConnectTimeout = properties.getProperty("sts.resourceServerManagement.resourceRetriever.httpConnectTimeout", "10000");
		String httpReadTimeout = properties.getProperty("sts.resourceServerManagement.resourceRetriever.httpReadTimeout", "60000");
		String httpSizeLimit = properties.getProperty("sts.resourceServerManagement.resourceRetriever.httpSizeLimit", "512000");
		resourceRetrieverProperties = createResourceRetrieverProperties(httpConnectTimeout, httpReadTimeout, httpSizeLimit);
	}

	private void readFromPropertiesEnv() {
		String res_servers_prop = EnvProperties.getEnvOrSysProp("STS_RESOURCE_SERVER_LIST", "");
		String[] split = StringUtils.split(res_servers_prop, ",");
		resourceServers = new ArrayList<>();
		for (String resourceServerName : split) {
			if (StringUtils.isBlank(resourceServerName))
				continue;
			ResourceServer resServer = retrieveResourceServerFromEnv(resourceServerName);
			resourceServers.add(resServer);
		}
		
		String httpConnectTimeout = EnvProperties.getEnvOrSysProp("STS_RESOURCE_SERVER_MANAGEMENT_RESOURCE_RETRIEVER_HTTP_CONNECT_TIMEOUT", "10000");
		String httpReadTimeout = EnvProperties.getEnvOrSysProp("STS_RESOURCE_SERVER_MANAGEMENT_RESOURCE_RETRIEVER_HTTP_READ_TIMEOUT", "60000");
		String httpSizeLimit = EnvProperties.getEnvOrSysProp("STS_RESOURCE_SERVER_MANAGEMENT_RESOURCE_RETRIEVER_HTTP_SIZE_LIMIT", "512000");
		resourceRetrieverProperties = createResourceRetrieverProperties(httpConnectTimeout, httpReadTimeout, httpSizeLimit);
	}

	private ResourceRetrieverEnvProperties createResourceRetrieverProperties(String httpConnectTimeout, String httpReadTimeout,
			String httpSizeLimit) {
		ResourceRetrieverEnvProperties rr = new ResourceRetrieverEnvProperties();
		if(StringUtils.isNumeric(httpConnectTimeout)){
			rr.setHttpConnectTimeout(Integer.parseInt(httpConnectTimeout));
		}
		if(StringUtils.isNumeric(httpReadTimeout)){
			rr.setHttpReadTimeout(Integer.parseInt(httpReadTimeout));
		}
		if(StringUtils.isNumeric(httpSizeLimit)){
			rr.setHttpSizeLimit(Integer.parseInt(httpSizeLimit));
		}
		return rr;
	}
	
	private ResourceServer retrieveResourceServerFromEnv(String resourceServerName) {
		String prefix = ENV_STS_RESOURCE_SERVER_PREFIX + "_" + resourceServerName + "_";
		String jwks_url_key = prefix + "JWKS_URL";
		String audience_key = prefix + "AUDIENCE";

		ResourceServer resourceServer = new ResourceServer();
		resourceServer.setAudience(EnvProperties.getEnvOrSysProp(audience_key, false));

		resourceServer.setJwksUrl(EnvProperties.getEnvOrSysProp(jwks_url_key, false));

		String clientId = EnvProperties.getEnvOrSysProp(prefix + "CLIENT_ID", true);
		if (StringUtils.isNotBlank(clientId))
			resourceServer.setClientId(clientId);

		String idpServer = EnvProperties.getEnvOrSysProp(prefix + "IDP_SERVER", true);
		resourceServer.setIdpServer(BooleanUtils.toBoolean(idpServer));

		String userSecretClaimName = EnvProperties.getEnvOrSysProp(prefix + "USER_SECRET_CLAIM_NAME", false);
		if (StringUtils.isNotBlank(userSecretClaimName))
			resourceServer.setUserSecretClaimName(userSecretClaimName);
		return resourceServer;
	}

	private ResourceServer retrieveResourceServerFromProperties(Properties prop, String resourceServerName) {
		String prefix = PROPS_STS_RESOURCE_SERVER_PREFIX + "." + resourceServerName + ".";
		String jwks_url_key = prefix + "jwksUrl";
		String audience_key = prefix + "audience";

		ResourceServer resourceServer = new ResourceServer();

		String audience = prop.getProperty(audience_key);
		if (StringUtils.isBlank(audience))
			throw new IllegalStateException("Missing property: " + audience_key);
		resourceServer.setAudience(audience);

		String jwksUrl = prop.getProperty(jwks_url_key);
		if (StringUtils.isBlank(jwksUrl))
			throw new IllegalStateException("Missing property: " + jwks_url_key);
		resourceServer.setJwksUrl(jwksUrl);

		String clientId = prop.getProperty(prefix + "clientId");
		if (StringUtils.isNotBlank(clientId))
			resourceServer.setClientId(clientId);

		String idpServer = prop.getProperty(prefix + "idpServer");
		resourceServer.setIdpServer(BooleanUtils.toBoolean(idpServer));

		String userSecretClaimName = prop.getProperty(prefix + "userSecretClaimName");
		if (StringUtils.isNotBlank(userSecretClaimName))
			resourceServer.setUserSecretClaimName(userSecretClaimName);

		return resourceServer;
	}

	
    public static class ResourceRetrieverEnvProperties implements ResourceRetrieverProperties {

        private Integer httpConnectTimeout;

        private Integer httpReadTimeout;

        private Integer httpSizeLimit;

        @Override
        public Integer getHttpConnectTimeout() {
            return httpConnectTimeout;
        }

        @Override
        public Integer getHttpReadTimeout() {
            return httpReadTimeout;
        }

        @Override
        public Integer getHttpSizeLimit() {
            return httpSizeLimit;
        }

        public void setHttpConnectTimeout(Integer httpConnectTimeout) {
            this.httpConnectTimeout = httpConnectTimeout;
        }

        public void setHttpReadTimeout(Integer httpReadTimeout) {
            this.httpReadTimeout = httpReadTimeout;
        }

        public void setHttpSizeLimit(Integer httpSizeLimit) {
            this.httpSizeLimit = httpSizeLimit;
        }
    }
}
