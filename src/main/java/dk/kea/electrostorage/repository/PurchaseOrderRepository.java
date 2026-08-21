package dk.kea.electrostorage.repository;
import dk.kea.electrostorage.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByReceivedDateIsNullOrderByIdDesc();
}
