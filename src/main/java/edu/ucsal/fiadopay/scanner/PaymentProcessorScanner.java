package edu.ucsal.fiadopay.scanner;

import edu.ucsal.fiadopay.annotation.PaymentMethod;
import edu.ucsal.fiadopay.service.processor.PaymentProcessor;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentProcessorScanner {
    private final Map<String, PaymentProcessor> processors = new HashMap<>();
    private final ApplicationContext applicationContext;

    public PaymentProcessorScanner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void scanAndRegisterProcessors(){
        System.out.println("LOG: Iniciando escaneamento de processadores de pagamento via Reflexão...");

        Map<String, PaymentProcessor> beanMap = applicationContext.getBeansOfType(PaymentProcessor.class);

        for (PaymentProcessor processor : beanMap.values()) {
            Class<?> clazz = processor.getClass();
            if (clazz.isAnnotationPresent(PaymentMethod.class)) {
                PaymentMethod annotation = clazz.getAnnotation(PaymentMethod.class);
                String type = annotation.type().toUpperCase();

                processors.put(type, processor);
                System.out.println("LOG: [REFLEXÃO] Registrado tipo '" + type +
                        "' com a classe " + clazz.getSimpleName());
            }
        }
    }

    public PaymentProcessor getProcessor(String type) {
        PaymentProcessor processor = processors.get(type.toUpperCase());
        if (processor == null) {
            throw new IllegalArgumentException("Método de pagamento '" + type + "' não implementado.");
        }
        return processor;
    }
}
