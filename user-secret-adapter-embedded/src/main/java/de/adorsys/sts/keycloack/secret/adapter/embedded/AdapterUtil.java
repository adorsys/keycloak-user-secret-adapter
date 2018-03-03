package de.adorsys.sts.keycloack.secret.adapter.embedded;

import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.KeyRetrieverService;
import de.adorsys.sts.resourceserver.service.ResourceServerManagementProperties;
import de.adorsys.sts.resourceserver.service.ResourceServerService;

public class AdapterUtil {
	public static final UserSecretAdapterEmbedded init(){
		ResourceServerManagementProperties resourceServerManagementProperties = new EnvPropsResourceServerManagementProperties();
		ResourceServerRepository resourceServerRepository = new InMemoryResourceServerRepository();
		resourceServerRepository.addAll(resourceServerManagementProperties.getResourceServers());

		ResourceServerService resourceServerService = new ResourceServerService(resourceServerRepository);
		KeyRetrieverService keyRetrieverService = new KeyRetrieverService(resourceServerService, resourceServerManagementProperties);
		EncryptionService encryptionService = new EncryptionService(keyRetrieverService);
		
		return new UserSecretAdapterEmbedded(resourceServerService, encryptionService);

	}
}
