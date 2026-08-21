package dk.kea.electrostorage.repository;
import dk.kea.electrostorage.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SupplierRepository extends JpaRepository<Supplier, Long> {}
