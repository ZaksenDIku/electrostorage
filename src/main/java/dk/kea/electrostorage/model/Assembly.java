package dk.kea.electrostorage.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Assembly {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false)
    private Component resultComponent;
    @OneToMany(mappedBy = "assembly", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<AssemblyLine> lines = new ArrayList<>();

    public Assembly() {}
    public Assembly(Component resultComponent) { this.resultComponent = resultComponent; }
    public Long getId() { return id; }
    public Component getResultComponent() { return resultComponent; }
    public void setResultComponent(Component resultComponent) { this.resultComponent = resultComponent; }
    public List<AssemblyLine> getLines() { return lines; }
    public void addLine(AssemblyLine line) { lines.add(line); line.setAssembly(this); }
}
