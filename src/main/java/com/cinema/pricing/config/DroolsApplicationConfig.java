package com.cinema.pricing.config;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DroolsApplicationConfig {
 
 private static final KieServices kieServices = KieServices.Factory.get();
 private static final String RULES_DRL = "rules/pricing-rules.drl";
 
   @Bean
     public KieContainer kieContainer() {
       log.info("Initializing Drools KieContainer...");
         KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

         log.debug("Loading rules from: {}", RULES_DRL);
         kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_DRL));
         KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
         kb.buildAll();

         if( kb.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)){
             log.error("Errors building Drools rules:");
             kb.getResults().getMessages(Message.Level.ERROR)
                     .forEach(msg -> log.error("  - {}", msg.getText()));

             throw new RuntimeException("Error building Drools rules - check DRL syntax");
         }

       KieModule kieModule = kb.getKieModule();
       KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

       log.info("Drools KieContainer initialized successfully");
         return kieContainer;
     }
}