package cc.viridian.service.statement.config;

import cc.viridian.provider.AdapterConfig;
import cc.viridian.provider.CoreBankProvider;
import cc.viridian.provider.spi.CoreBank;
import java.io.IOException;
import java.net.MalformedURLException;
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

    private String applicationVersion;

    public HashMap<String, AdapterConfig> getLoadedClasses() {
        return loadedClasses;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    /**
     * Initialize corebank adapters.
     *
     * @return list of valid adapters
     */
    @Bean
    public HashMap<String, AdapterConfig> initializeAdapters() {
        try {
            Attributes appAttributes = getAttributesFromManifest(CorebankAdapterConfig.class);
            applicationVersion = "dev";
            if (appAttributes.getValue("Build-Version") != null) {
                applicationVersion = appAttributes.getValue("Build-Version").toString();
            }

            CoreBankProvider coreBankProvider = CoreBankProvider.getInstance();
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
            log.error("fatal error reading config properties from config server");
            log.error(e.getMessage());
            System.exit(1);
        }
        return loadedClasses;
    }

    /**
     * get Corebank Adapter.
     *
     * @param corebankId
     * @return
     */
    public CoreBank getCorebankAdapter(final String corebankId) {
        if (loadedClasses.containsKey(corebankId)) {
            return loadedClasses.get(corebankId).getAdapter();
        } else {
            return null;
        }
    }

    private Attributes getAttributesFromManifest(final Class clazz) {
        Attributes attributes = new Attributes();
        String simpleClassName = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(simpleClassName).toString();
        if (classPath.startsWith("jar")) {
            String manifestPath = classPath.substring(0, classPath.indexOf("!") + 1) + "/META-INF/MANIFEST.MF";
            try {
                Manifest manifest = new Manifest(new URL(manifestPath).openStream());
                attributes = manifest.getMainAttributes();
                for (Object key : attributes.keySet()) {
                    if (attributes.getValue(key.toString()) != null) {
                        log.info(key.toString() + ": " + attributes.getValue(key.toString()));
                    }
                }

            } catch (MalformedURLException e) {
                log.warn("MalformedURLException in: " + manifestPath);
            } catch (IOException e) {
                log.warn("IOException with file: " + manifestPath);
            }
        }
        return attributes;
    }
}
