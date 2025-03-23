package io.rdlab.scylladb.fun.controller;

import io.rdlab.scylladb.fun.ContainersRunner;
import io.rdlab.scylladb.fun.model.Property;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PropertyControllerTests extends ContainersRunner {
    private static final int PORT = 8093;

    private static final ParameterizedTypeReference<List<Property>> LIST_OF_PROPERTIES =
            new ParameterizedTypeReference<>() {
            };

    private static final Instant DATE_1 = Instant.parse("2025-01-01T00:00:00Z");

    private static final Property PROPERTY_1 = new Property("g", "a", DATE_1, "data_1");
    private static final Property PROPERTY_2 = new Property("g", "a", DATE_1.plusSeconds(1), "data_2");
    private static final Property PROPERTY_3 = new Property("g", "a", DATE_1.plusSeconds(2), "data_2");
    private static final Property PROPERTY_4 = new Property("g", "a", DATE_1.plusSeconds(3), "data_3");
    private static final Property PROPERTY_5 = new Property("g", "a", DATE_1.plusSeconds(5), "data_5");

    @Autowired
    private TestRestTemplate template;

    private URI baseUri;

    @BeforeAll
    void beforeAll() {
        baseUri =
                UriComponentsBuilder.newInstance()
                        .scheme("http")
                        .host("localhost")
                        .port(PORT)
                        .path("/api/v1/property")
                        .build()
                        .toUri();
    }

    @Test
    void shouldReturnCreatedProperty() {
        ResponseEntity<Property> response = template.postForEntity(baseUri, PROPERTY_1, Property.class);

        assertThat(response.getBody()).isEqualTo(PROPERTY_1);
    }

    @Test
    void shouldReturnProperty() {
        template.postForEntity(baseUri, PROPERTY_1, Property.class);

        URI uri = UriComponentsBuilder.fromUri(baseUri).path("/g/a/20250101000000000").build().toUri();
        Property property = template.getForObject(uri, Property.class);

        assertThat(property).isEqualTo(PROPERTY_1);
    }

    @Test
    void shouldReturnCreatedProperties() {
        template.postForEntity(baseUri, PROPERTY_1, Property.class);
        template.postForEntity(baseUri, PROPERTY_2, Property.class);

        URI uri = UriComponentsBuilder.fromUri(baseUri)
                .path("/find")
                .queryParam("group", "g")
                .queryParam("name", "a")
                .queryParam("start", "2024")
                .queryParam("end", "2026")
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .build()
                .toUri();

        RequestEntity<Void> request = RequestEntity.get(uri).build();
        ResponseEntity<List<Property>> response = template.exchange(request, LIST_OF_PROPERTIES);

        List<Property> properties = response.getBody();
        assertNotNull(properties);
        assertFalse(properties.isEmpty());
    }

    @Test
    void shouldCalculateMaxFrequencyText() {
        template.postForEntity(baseUri, PROPERTY_1, Property.class);
        template.postForEntity(baseUri, PROPERTY_2, Property.class);
        template.postForEntity(baseUri, PROPERTY_3, Property.class);
        template.postForEntity(baseUri, PROPERTY_4, Property.class);
        template.postForEntity(baseUri, PROPERTY_5, Property.class);

        URI uri = UriComponentsBuilder.fromUri(baseUri)
                .path("/max-frequency-text")
                .queryParam("group", "g")
                .queryParam("name", "a")
                .queryParam("start", "2024")
                .queryParam("end", "2026")
                .build().toUri();

        String maxFrequencyText = template.getForObject(uri, String.class);
        assertEquals("data_2", maxFrequencyText);
    }
}
