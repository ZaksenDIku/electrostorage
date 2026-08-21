package dk.kea.electrostorage.controller;

import dk.kea.electrostorage.model.Component;
import dk.kea.electrostorage.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/components")
public class ComponentController {
    private final ComponentRepository components;
    private final SupplierRepository suppliers;
    public ComponentController(ComponentRepository components, SupplierRepository suppliers) {
        this.components = components; this.suppliers = suppliers;
    }
    @GetMapping public List<Component> getAll() { return components.findAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Component create(@RequestBody ComponentRequest request) {
        if (request.internalNumber() == null || request.name() == null || request.name().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Internt nummer og navn skal udfyldes");
        if (components.existsById(request.internalNumber()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Det interne nummer findes allerede");
        var supplier = suppliers.findById(request.supplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leverandøren findes ikke"));
        return components.save(new Component(request.internalNumber(), request.name(), request.externalNumber(), true, supplier));
    }

    @PatchMapping("/{number}/discontinue")
    public Component discontinue(@PathVariable Long number) {
        var component = components.findById(number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Komponenten findes ikke"));
        component.setDiscontinued(true);
        return components.save(component);
    }

    public record ComponentRequest(Long internalNumber, String name, String externalNumber, Long supplierId) {}
}
