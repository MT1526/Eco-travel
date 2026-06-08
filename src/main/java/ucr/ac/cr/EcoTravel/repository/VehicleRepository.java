package ucr.ac.cr.EcoTravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.EcoTravel.model.Vehicle;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> { // Long

    List<Vehicle> findByCategory(String category);

    boolean existsByName(String name);
}