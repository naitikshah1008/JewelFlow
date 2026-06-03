package com.jewelflow.backend.inventory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.jewelflow.backend.common.PageResponse;

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
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean includeArchived
    ) {
        return jewelleryItemService.getAllItems(status, category, metalType, purity, keyword, includeArchived);
    }

    @GetMapping("/page")
    public PageResponse<JewelleryItem> getItemsPage(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String metalType,
            @RequestParam(required = false) String purity,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            @RequestParam(defaultValue = "false") boolean includeArchived
    ) {
        return jewelleryItemService.getItemsPage(
                status,
                category,
                metalType,
                purity,
                keyword,
                includeArchived,
                page,
                size,
                sortBy,
                direction
        );
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

    @PostMapping("/{id}/restore")
    public JewelleryItem restoreItem(@PathVariable Long id) {
        return jewelleryItemService.restoreItem(id);
    }
}
