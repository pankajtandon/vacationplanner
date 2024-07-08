package com.technochord.ai.vacationplanner.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Set;

public class RagCandidateSpringContext implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Set<String> getRagCandidateServiceBeanNames() {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(RagCandidate.class);
        return beanMap.keySet();
    }
}
