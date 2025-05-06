package io.rdlab.scylladb.fun.service;

import com.datastax.oss.driver.api.core.CqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class CustomApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(CustomApplicationListener.class);

    private final CqlSession session;

    public CustomApplicationListener(CqlSession session) {
        this.session = session;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        session.getContext().getConfig().getProfiles().forEach((n, p) -> {
            StringBuilder buffer = new StringBuilder();
            p.entrySet().forEach(e -> buffer.append(String.format("""
                    %s: %s
                    """, e.getKey(), e.getValue())));
            LOG.info("{}, {}", n, buffer);
        });
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
