package io.rdlab.scylladb.fun.controller;

import io.rdlab.scylladb.fun.model.Property;
import io.rdlab.scylladb.fun.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {
    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public CompletionStage<ResponseEntity<Property>> create(
            @RequestBody Property property
    ) {
        return propertyService
                .save(property)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{group}/{name}/{date}")
    public CompletionStage<ResponseEntity<Property>> findById(
            @PathVariable("group") String group,
            @PathVariable("name") String name,
            @PathVariable("date") Instant date
    ) {
        return propertyService
                .findById(group, name, date)
                .thenApply(
                        p -> p.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build())
                );
    }

    @GetMapping("/find")
    public CompletionStage<Stream<Property>> find(
            @RequestParam(name = "group") String group,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "start") Instant start,
            @RequestParam(name = "end") Instant end,
            @RequestParam(name = "offset") int offset,
            @RequestParam(name = "limit") int limit
    ) {
        return propertyService.findByData(group, name, start, end, offset, limit);
    }

    @GetMapping("/most-common-text")
    public CompletionStage<ResponseEntity<String>> mostCommonText(
            @RequestParam(name = "group") String group,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "start") Instant start,
            @RequestParam(name = "end") Instant end
    ) {
        return propertyService.mostCommonText(group, name, start, end)
                .thenApply(
                        p -> p.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build())
                );
    }

    @GetMapping("/count-all")
    public CompletionStage<ResponseEntity<Long>> countAll(
            @RequestParam(name = "group") String group,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "start") Instant start,
            @RequestParam(name = "end") Instant end
    ) {
        return propertyService.countAllByData(group, name, start, end)
                .thenApply(
                        p -> p.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build())
                );
    }
}
