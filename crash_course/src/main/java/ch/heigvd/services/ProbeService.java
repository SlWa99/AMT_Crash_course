package ch.heigvd.services;

import ch.heigvd.entities.Probe;
import ch.heigvd.entities.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.net.URI;
import java.net.http.HttpClient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class ProbeService {

    @Inject
    EntityManager entityManager;

    @Transactional
    public List<Probe> listProbes() {
        return entityManager.createQuery("SELECT p FROM Probe p", Probe.class).getResultList();
    }

    @Transactional
    public Probe getOrCreateProbe(String url) {
        List<Probe> probes = entityManager.createQuery("SELECT p FROM Probe p WHERE p.url = :url", Probe.class)
                .setParameter("url", url)
                .getResultList();
        if (probes.isEmpty()) {
            Probe probe = new Probe();
            probe.setUrl(url);
            entityManager.persist(probe);
            return probe;
        }
        return probes.get(0);
    }

    @Transactional
    public void executeProbe(String url) {
        var start = Instant.now();
        try (var client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(1)).build()) {
            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .header("User-Agent", "Uptime/0.0.1")
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Expires", "0")
                    .build();

            var responseCode = client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
            var end = Instant.now();
            var duration = Duration.between(start, end).toMillis();

            var probe = getOrCreateProbe(url);
            var status = new Status(probe, start, responseCode, (int) duration);

            entityManager.persist(status);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}