package com.platinumcoin.observability.infrastructure.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> commonTags(
            @Value("${spring.application.name}") String appName) {
        return registry -> {
            registry.config().commonTags(
                    "application", appName,
                    "env", System.getProperty("spring.profiles.active", "default")
            );
            new ClassLoaderMetrics().bindTo(registry);
            new JvmMemoryMetrics().bindTo(registry);
            new JvmGcMetrics().bindTo(registry);
            new ProcessorMetrics().bindTo(registry);
            new FileDescriptorMetrics().bindTo(registry);
        };
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Este é o Counter “primário” de pedidos criados.
     * Como está marcado @Primary, qualquer injeção de Counter sem @Qualifier
     * vai apontar para este bean.
     */
    @Bean
    @Primary
    public Counter ordersTotalCounter(MeterRegistry registry) {
        return Counter.builder("orders_total")
                .description("Total de pedidos criados")
                .register(registry);
    }

    /**
     * (Opcional) Se você quiser manipular hits/misses manualmente,
     * pode expor dois Counter separados:
     */
    @Bean
    public Counter cacheHitCounter(MeterRegistry registry) {
        return Counter.builder("cache_requests_total")
                .description("Total de acertos no cache")
                .tag("result", "hit")
                .register(registry);
    }

    @Bean
    public Counter cacheMissCounter(MeterRegistry registry) {
        return Counter.builder("cache_requests_total")
                .description("Total de faltas no cache")
                .tag("result", "miss")
                .register(registry);
    }
}