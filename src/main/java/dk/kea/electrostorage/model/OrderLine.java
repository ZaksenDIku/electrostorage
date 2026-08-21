package dk.kea.electrostorage.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class OrderLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JsonBackReference
    private PurchaseOrder order;
    @ManyToOne(optional = false)
    private Component component;
    private int quantity;

    public OrderLine() {}
    public OrderLine(Component component, int quantity) { this.component = component; this.quantity = quantity; }
    public Long getId() { return id; }
    public PurchaseOrder getOrder() { return order; }
    public void setOrder(PurchaseOrder order) { this.order = order; }
    public Component getComponent() { return component; }
    public void setComponent(Component component) { this.component = component; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
