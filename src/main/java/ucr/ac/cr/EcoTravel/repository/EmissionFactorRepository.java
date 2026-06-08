package ucr.ac.cr.EcoTravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.EcoTravel.model.EmissionFactor;

@Repository
public interface EmissionFactorRepository extends JpaRepository<EmissionFactor, Long> { // Long

    EmissionFactor findByTransportType(String transportType);
}