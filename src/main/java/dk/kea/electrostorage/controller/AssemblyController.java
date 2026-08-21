package dk.kea.electrostorage.controller;

import dk.kea.electrostorage.model.Assembly;
import dk.kea.electrostorage.repository.AssemblyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assemblies")
@RequiredArgsConstructor
public class AssemblyController {
    private final AssemblyRepository repository;
    @GetMapping public List<Assembly> getAll() { return repository.findAll(); }
}
