package com.jewelflow.backend.inventory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JewelleryItemController {

    private final JewelleryItemService jewelleryItemService;

    @PostMapping
    public JewelleryItem createItem(@Valid @RequestBody JewelleryItemRequest request) {
        return jewelleryItemService.createItem(request);
    }

    @GetMapping
    public List<JewelleryItem> getAllItems(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String metalType,
            @RequestParam(required = false) String purity,
            @RequestParam(required = false) String keyword
    ) {
        return jewelleryItemService.getAllItems(status, category, metalType, purity, keyword);
    }

    @GetMapping("/{id}")
    public JewelleryItem getItemById(@PathVariable Long id) {
        return jewelleryItemService.getItemById(id);
    }

    @PutMapping("/{id}")
    public JewelleryItem updateItem(
            @PathVariable Long id,
            @Valid @RequestBody JewelleryItemRequest request
    ) {
        return jewelleryItemService.updateItem(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        jewelleryItemService.deleteItem(id);
    }
}
