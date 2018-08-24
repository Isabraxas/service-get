package cc.viridian.service.statement.config;

import cc.viridian.provider.CoreBankProvider;
import cc.viridian.provider.CorebankConfig;
import cc.viridian.provider.spi.CoreBank;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CorebankAdapterConfig {

    private HashMap<String, CorebankConfig> loadedClasses;

    public HashMap<String, CorebankConfig> getLoadedClasses() {
        return loadedClasses;
    }

    /**
     * Initialize corebanks.
     * @return
     */
    @Bean
    public HashMap<String, CorebankConfig> init() {
        try {
            CoreBankProvider coreBank = CoreBankProvider.getInstance();
            loadedClasses = coreBank.init();
            if (loadedClasses.size() == 0) {
                log.error("Fatal Error. There are zero Corebank adapters loaded in the system.");
                log.error("Check load class path to include valid Corebank adapters.");
                System.exit(1);
            }

            log.info("Corebank Adapters:");
            for (CorebankConfig config : loadedClasses.values()) {
                log.info(config.getName() + " " + config.getClassName());
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
    public CoreBank getCorebankAdapter(String corebankId) {
        if (loadedClasses.containsKey(corebankId)) {
            return loadedClasses.get(corebankId).getAdapter();
        } else {
            return null;
        }
    }
}
