package ucr.ac.cr.EcoTravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.EcoTravel.model.Trip;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> { // Long

    List<Trip> findByUserId(Long userId); // Long

    List<Trip> findByUserIdOrderByDateDesc(Long userId); // Long
}