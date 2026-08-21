package dk.kea.electrostorage.controller;

import dk.kea.electrostorage.model.Assembly;
import dk.kea.electrostorage.repository.AssemblyRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assemblies")
public class AssemblyController {
    private final AssemblyRepository repository;
    public AssemblyController(AssemblyRepository repository) { this.repository = repository; }
    @GetMapping public List<Assembly> getAll() { return repository.findAll(); }
}
