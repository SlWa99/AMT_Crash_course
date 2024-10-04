package ch.heigvd.messagings;

import ch.heigvd.services.ProbeService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class ProbeProducer {
    @Inject
    EntityManager entityManager;

    @Inject
    ConnectionFactory connectionFactory;
    @Inject
    ProbeService probeService;

    @Scheduled(every = "1s")
    public void checkProbes() {
        try(var context = connectionFactory.createContext()) {
            var queue = context.createQueue("probes");
            var producer = context.createProducer();

            for(var probe : probeService.listProbes())
                producer.send(queue, probe.getUrl());
        }
    }
}
