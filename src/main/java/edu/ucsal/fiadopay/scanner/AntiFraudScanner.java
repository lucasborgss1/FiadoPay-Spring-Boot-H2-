package edu.ucsal.fiadopay.scanner;

import edu.ucsal.fiadopay.annotation.AntiFraudRule;
import edu.ucsal.fiadopay.antifraud.FraudRule;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AntiFraudScanner {
    private final List<FraudRule> fraudRules = new ArrayList<>();
    private final ApplicationContext applicationContext;

    public AntiFraudScanner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void scanRules() {
        System.out.println("LOG: Iniciando escaneamento de processadores Anti-Fraude via Reflexão...");
        Map<String, FraudRule> beanMap = applicationContext.getBeansOfType(FraudRule.class);

        for (FraudRule rule : beanMap.values()) {
            Class<?> clazz = rule.getClass();
            if (clazz.isAnnotationPresent(AntiFraudRule.class)) {
                fraudRules.add(rule);
                System.out.println("LOG: [REFLEXÃO] Anti-Fraude Registrado: " + clazz.getSimpleName());

            }
        }
    }

    public List<FraudRule> getRules() {
        return fraudRules;
    }
}
