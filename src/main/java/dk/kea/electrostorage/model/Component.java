package dk.kea.electrostorage.model;

import jakarta.persistence.*;

@Entity
public class Component {
    @Id
    private Long internalNumber;
    @Column(nullable = false)
    private String name;
    private String externalNumber;
    private boolean discontinued;
    private boolean orderable;
    @ManyToOne
    private Supplier supplier;

    public Component() {}
    public Component(Long internalNumber, String name, String externalNumber, boolean orderable, Supplier supplier) {
        this.internalNumber = internalNumber; this.name = name; this.externalNumber = externalNumber;
        this.orderable = orderable; this.supplier = supplier;
    }
    public Long getInternalNumber() { return internalNumber; }
    public void setInternalNumber(Long internalNumber) { this.internalNumber = internalNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getExternalNumber() { return externalNumber; }
    public void setExternalNumber(String externalNumber) { this.externalNumber = externalNumber; }
    public boolean isDiscontinued() { return discontinued; }
    public void setDiscontinued(boolean discontinued) { this.discontinued = discontinued; }
    public boolean isOrderable() { return orderable; }
    public void setOrderable(boolean orderable) { this.orderable = orderable; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
}
