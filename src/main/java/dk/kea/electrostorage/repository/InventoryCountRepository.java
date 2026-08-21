package dk.kea.electrostorage.repository;
import dk.kea.electrostorage.model.InventoryCount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface InventoryCountRepository extends JpaRepository<InventoryCount, Long> {
    Optional<InventoryCount> findFirstByComponentInternalNumberOrderByCountedAtDesc(Long componentNumber);
}
