package dk.kea.electrostorage.controller;

import dk.kea.electrostorage.model.*;
import dk.kea.electrostorage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final PurchaseOrderRepository orders;
    private final SupplierRepository suppliers;
    private final ComponentRepository components;
    @GetMapping public List<PurchaseOrder> getOpenOrders() { return orders.findByReceivedDateIsNullOrderByIdDesc(); }
    @GetMapping("/{id}") public PurchaseOrder getOne(@PathVariable Long id) { return findOrder(id); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrder create(@RequestBody CreateOrderRequest request) {
        var supplier = suppliers.findById(request.supplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leverandøren findes ikke"));
        return orders.save(new PurchaseOrder(supplier));
    }

    @PostMapping("/{id}/lines")
    public PurchaseOrder addLine(@PathVariable Long id, @RequestBody OrderLineRequest request) {
        var order = findOrder(id);
        if (order.isSent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Der kan ikke tilføjes til en sendt ordre");
        if (request.quantity() < 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Antallet skal være mindst 1");
        var component = components.findById(request.componentNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Komponenten findes ikke"));
        if (component.isDiscontinued() || !component.isOrderable())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Komponenten kan ikke bestilles");
        if (!component.getSupplier().getId().equals(order.getSupplier().getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Komponenten kommer fra en anden leverandør");
        order.addLine(new OrderLine(component, request.quantity()));
        return orders.save(order);
    }

    @PatchMapping("/{id}/send")
    public PurchaseOrder send(@PathVariable Long id, @RequestBody SendOrderRequest request) {
        var order = findOrder(id);
        if (order.isSent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ordren er allerede sendt");
        if (order.getLines().isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT, "En tom ordre kan ikke sendes");
        order.setSentDate(LocalDate.now());
        order.setExpectedDate(request.expectedDate());
        order.setTrackingCode(request.trackingCode());
        return orders.save(order);
    }

    @PatchMapping("/{id}/receive")
    public PurchaseOrder receive(@PathVariable Long id) {
        var order = findOrder(id);
        if (!order.isSent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ordren skal sendes før den kan modtages");
        order.setReceivedDate(LocalDate.now());
        return orders.save(order);
    }
    private PurchaseOrder findOrder(Long id) {
        return orders.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordren findes ikke"));
    }
    public record CreateOrderRequest(Long supplierId) {}
    public record OrderLineRequest(Long componentNumber, int quantity) {}
    public record SendOrderRequest(String trackingCode, LocalDate expectedDate) {}
}
