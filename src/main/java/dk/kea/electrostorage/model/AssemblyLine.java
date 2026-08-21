package dk.kea.electrostorage.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class AssemblyLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JsonBackReference
    private Assembly assembly;
    @ManyToOne(optional = false)
    private Component component;
    private int quantity;

    public AssemblyLine() {}
    public AssemblyLine(Component component, int quantity) { this.component = component; this.quantity = quantity; }
    public Long getId() { return id; }
    public Assembly getAssembly() { return assembly; }
    public void setAssembly(Assembly assembly) { this.assembly = assembly; }
    public Component getComponent() { return component; }
    public void setComponent(Component component) { this.component = component; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
