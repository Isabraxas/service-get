package cc.viridian.service.statement.config;

import cc.viridian.provider.AdapterConfig;
import cc.viridian.provider.CoreBankProvider;
import cc.viridian.provider.spi.CoreBank;
import java.net.URL;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Configuration
public class CorebankAdapterConfig {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${spring.cloud.config.uri}")
    private String springCloudConfigUrl;

    private HashMap<String, AdapterConfig> loadedClasses;

    public HashMap<String, AdapterConfig> getLoadedClasses() {
        return loadedClasses;
    }

    /**
     * Initialize corebank adapters.
     *
     * @return list of valid adapters
     */
    @Bean
    public HashMap<String, AdapterConfig> initializeAdapters() {
        try {
            CoreBankProvider coreBankProvider = CoreBankProvider.getInstance();
            //loadedClasses = coreBankProvider.initializeAdapters();
            loadedClasses = coreBankProvider.getAdapters();
            if (loadedClasses.size() == 0) {
                log.error("Fatal Error. There are zero Corebank adapters loaded in the system.");
                log.error("Check load class path to include valid Corebank adapters.");
                System.exit(1);
            }

            for (AdapterConfig config : loadedClasses.values()) {
                config.loadConfigProperties(activeProfile, springCloudConfigUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadedClasses;
    }

    /**
     * get Corebank Adapter.
     *
     * @param corebankId
     * @return
     */
    //@Bean
    public CoreBank getCorebankAdapter(final String corebankId) {
        if (loadedClasses.containsKey(corebankId)) {
            return loadedClasses.get(corebankId).getAdapter();
        } else {
            return null;
        }
    }
}
