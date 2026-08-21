package dk.kea.electrostorage.controller;

import dk.kea.electrostorage.model.Supplier;
import dk.kea.electrostorage.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierRepository repository;
    @GetMapping public List<Supplier> getAll() { return repository.findAll(); }
}
