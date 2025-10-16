package com.example.inventory.controller;

import com.example.inventory.entity.Store;
import com.example.inventory.service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public List<Store> getAll() {
        return storeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Store> getById(@PathVariable Long id) {
        Optional<Store> store = storeService.findById(id);
        return store.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Store create(@RequestBody Store store) {
        return storeService.save(store);
    }

    @PutMapping("/{id}")
    public Store update(@PathVariable Long id, @RequestBody Store store) {
        store.setId(id);
        return storeService.save(store);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        storeService.delete(id);
    }

    // ===== カスタム検索機能 =====

    @GetMapping("/search")
    public List<Store> searchByName(@RequestParam String keyword) {
        return storeService.searchByName(keyword);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Store> getByName(@PathVariable String name) {
        Optional<Store> store = storeService.findByName(name);
        return store.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/{name}")
    public boolean existsByName(@PathVariable String name) {
        return storeService.existsByName(name);
    }

    @GetMapping("/location")
    public List<Store> getByLocation(@RequestParam String location) {
        return storeService.findByLocationContainingIgnoreCase(location);
    }
}
