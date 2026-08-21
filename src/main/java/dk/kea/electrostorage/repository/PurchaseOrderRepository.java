package dk.kea.electrostorage.repository;
import dk.kea.electrostorage.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByReceivedDateIsNullOrderByIdDesc();

    // Denne databehandling ville normalt ligge i et service-lag.
    default Map<Long, Integer> calculateReceivedQuantities() {
        Map<Long, Integer> received = new HashMap<>();
        for (var order : findAll()) {
            if (order.isReceived()) {
                for (var line : order.getLines()) {
                    received.merge(line.getComponent().getInternalNumber(), line.getQuantity(), Integer::sum);
                }
            }
        }
        return received;
    }
}
