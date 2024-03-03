package xzfg.ex.jlcsb17.simple.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xzfg.ex.jlcsb17.simple.entity.Storage;
import xzfg.ex.jlcsb17.simple.service.StorageService;

import java.util.List;

@RestController
public class StorageController {
    private StorageService storageService;

    public StorageController(
            StorageService storageService
    ) {
        this.storageService = storageService;
    }

    @GetMapping("/storage")
    public List<Storage> findAll() {
        return storageService.findAll();
    }
}
