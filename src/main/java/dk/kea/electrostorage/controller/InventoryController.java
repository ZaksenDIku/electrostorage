package dk.kea.electrostorage.controller;

import dk.kea.electrostorage.model.*;
import dk.kea.electrostorage.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final PurchaseOrderRepository orders;
    private final ComponentRepository components;
    private final InventoryCountRepository counts;
    public InventoryController(PurchaseOrderRepository orders, ComponentRepository components, InventoryCountRepository counts) {
        this.orders = orders; this.components = components; this.counts = counts;
    }
    @GetMapping
    public List<InventoryItem> getInventory() {
        Map<Long, Integer> received = new HashMap<>();
        for (var order : orders.findAll()) if (order.isReceived())
            for (var line : order.getLines()) received.merge(line.getComponent().getInternalNumber(), line.getQuantity(), Integer::sum);
        return received.entrySet().stream().map(entry -> {
            var component = components.findById(entry.getKey()).orElseThrow();
            var latest = counts.findFirstByComponentInternalNumberOrderByCountedAtDesc(entry.getKey()).orElse(null);
            return new InventoryItem(component, entry.getValue(), latest);
        }).toList();
    }
    @PostMapping("/{componentNumber}/counts") @ResponseStatus(HttpStatus.CREATED)
    public InventoryCount count(@PathVariable Long componentNumber, @RequestBody CountRequest request) {
        var component = components.findById(componentNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Komponenten findes ikke"));
        if (request.countedBy() == null || request.countedBy().isBlank() || request.quantity() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Navn og et positivt antal skal udfyldes");
        return counts.save(new InventoryCount(component, request.countedBy(), request.quantity()));
    }
    public record CountRequest(String countedBy, int quantity) {}
    public record InventoryItem(Component component, int receivedQuantity, InventoryCount latestCount) {}
}
