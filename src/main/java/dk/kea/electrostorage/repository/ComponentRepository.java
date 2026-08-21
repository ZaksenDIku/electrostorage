package dk.kea.electrostorage.repository;
import dk.kea.electrostorage.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ComponentRepository extends JpaRepository<Component, Long> {}
