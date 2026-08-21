package dk.kea.electrostorage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class InventoryCount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Component component;
    @Column(nullable = false)
    private String countedBy;
    private int quantity;
    private LocalDateTime countedAt;

    public InventoryCount() {}
    public InventoryCount(Component component, String countedBy, int quantity) {
        this.component = component; this.countedBy = countedBy; this.quantity = quantity;
        this.countedAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public Component getComponent() { return component; }
    public String getCountedBy() { return countedBy; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getCountedAt() { return countedAt; }
}
