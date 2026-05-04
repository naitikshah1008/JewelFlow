package com.jewelflow.backend.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JewelleryItemController {

    private final JewelleryItemRepository repository;

    @PostMapping
    public JewelleryItem createItem(@RequestBody JewelleryItem item) {
        return repository.save(item);
    }

    @GetMapping
    public List<JewelleryItem> getAllItems() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public JewelleryItem getItemById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @PutMapping("/{id}")
    public JewelleryItem updateItem(@PathVariable Long id, @RequestBody JewelleryItem updatedItem) {
        JewelleryItem item = repository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        item.setItemName(updatedItem.getItemName());
        item.setCategory(updatedItem.getCategory());
        item.setMetalType(updatedItem.getMetalType());
        item.setPurity(updatedItem.getPurity());
        item.setGrossWeight(updatedItem.getGrossWeight());
        item.setNetWeight(updatedItem.getNetWeight());
        item.setStoneWeight(updatedItem.getStoneWeight());
        item.setStonePrice(updatedItem.getStonePrice());
        item.setMakingCharges(updatedItem.getMakingCharges());
        item.setPurchaseCost(updatedItem.getPurchaseCost());
        item.setSellingPrice(updatedItem.getSellingPrice());
        item.setStatus(updatedItem.getStatus());
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        repository.deleteById(id);
    }
}