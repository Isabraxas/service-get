package cc.viridian.service.statement.config;

import cc.viridian.provider.CoreBankProvider;
import cc.viridian.provider.CorebankConfig;
import cc.viridian.provider.spi.CoreBank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Slf4j
@Service
public class CorebankAdapterConfig {

    private HashMap<String, CorebankConfig> loadedClasses;

    public HashMap<String, CorebankConfig> getLoadedClasses() {
        return loadedClasses;
    }

    @Bean
    public HashMap<String, CorebankConfig>  init() {
        CoreBankProvider coreBank = CoreBankProvider.getInstance();
        loadedClasses = coreBank.init();
        if (loadedClasses.size() == 0){
            log.error("Fatal Error. There are zero Corebank adapters loaded in the system.");
            log.error("Check load class path to include valid Corebank adapters.");
            System.exit(1);
        } else {

        }

        log.info("Corebank Adapters:");
        for (CorebankConfig config : loadedClasses.values()) {
            log.info(config.getName() + " " + config.getClassName());
        }
        return loadedClasses;
    }

    //@Bean
    public CoreBank getCorebankAdapter (String corebankId) {
        if (loadedClasses.containsKey(corebankId)) {
            return loadedClasses.get(corebankId).getAdapter();
        } else {
            return null;
        }
    }

}
