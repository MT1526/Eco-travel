package ucr.ac.cr.EcoTravel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.EcoTravel.model.Vehicle;
import ucr.ac.cr.EcoTravel.repository.VehicleRepository;

import java.util.*;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    private static final Map<String, Double> DEFAULT_FACTORS = new HashMap<>();
    static {
        DEFAULT_FACTORS.put("Pequeño", 0.12);
        DEFAULT_FACTORS.put("Mediano", 0.15);
        DEFAULT_FACTORS.put("Grande", 0.18);
        DEFAULT_FACTORS.put("SUV", 0.22);
        DEFAULT_FACTORS.put("Pickup", 0.25);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        Optional<Vehicle> opt = this.vehicleRepository.findById(vehicle.getId());
        if (opt.isPresent()) {
            return null;
        }

        if (vehicle.getName() == null || vehicle.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle name cannot be empty");
        }
        if (vehicle.getCategory() == null || !DEFAULT_FACTORS.containsKey(vehicle.getCategory())) {
            throw new IllegalArgumentException("Invalid category. Allowed: Pequeño, Mediano, Grande, SUV, Pickup");
        }

        if (vehicle.getEmissionFactor() == null) {
            vehicle.setEmissionFactor(DEFAULT_FACTORS.get(vehicle.getCategory()));
        }

        return this.vehicleRepository.save(vehicle);
    }

    public List<Vehicle> findAll() {
        return this.vehicleRepository.findAll();
    }

    public Vehicle findById(Long id) { // Long
        Optional<Vehicle> opt = this.vehicleRepository.findById(id);
        return opt.orElse(null);
    }

    public void deleteVehicle(Long id) { // Long
        this.vehicleRepository.deleteById(id);
    }

    public Vehicle editVehicle(Long id, Vehicle vehicleEdit) { // Long
        Optional<Vehicle> vehicleOp = this.vehicleRepository.findById(id);
        if (vehicleOp.isPresent()) {
            Vehicle vehicle = vehicleOp.get();
            vehicle.setName(vehicleEdit.getName());
            vehicle.setCategory(vehicleEdit.getCategory());
            if (vehicleEdit.getEmissionFactor() != null) {
                vehicle.setEmissionFactor(vehicleEdit.getEmissionFactor());
            } else {
                vehicle.setEmissionFactor(DEFAULT_FACTORS.getOrDefault(vehicleEdit.getCategory(), 0.15));
            }
            vehicle.setCreatedBy(vehicleEdit.getCreatedBy());
            return this.vehicleRepository.save(vehicle);
        }
        return null;
    }

    public Vehicle findMostEcoFriendlyCar() {
        List<Vehicle> all = this.vehicleRepository.findAll();
        if (all.isEmpty()) return null;
        return all.stream().min(Comparator.comparing(Vehicle::getEmissionFactor)).orElse(null);
    }
}