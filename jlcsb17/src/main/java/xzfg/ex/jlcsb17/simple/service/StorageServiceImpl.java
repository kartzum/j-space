package xzfg.ex.jlcsb17.simple.service;

import org.springframework.stereotype.Service;
import xzfg.ex.jlcsb17.simple.entity.Storage;
import xzfg.ex.jlcsb17.simple.repository.StorageRepository;

import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {
    private StorageRepository storageRepository;

    public StorageServiceImpl(
            StorageRepository storageRepository
    ) {
        this.storageRepository = storageRepository;
    }

    @Override
    public List<Storage> findAll() {
        return storageRepository.findAll();
    }
}
