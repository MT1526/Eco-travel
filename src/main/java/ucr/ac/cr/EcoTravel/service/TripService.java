package ucr.ac.cr.EcoTravel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.EcoTravel.model.*;
import ucr.ac.cr.EcoTravel.repository.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private EmissionFactorRepository emissionFactorRepository;

    @Autowired
    private VehicleService vehicleService;

    // Factores de emisión para BUS según ocupación (kg/km)
    private static final double BUS_FACTOR_LOW = 0.05;
    private static final double BUS_FACTOR_MEDIUM = 0.03;
    private static final double BUS_FACTOR_HIGH = 0.008; // Ajustado para que bus lleno sea mejor que tren

    public Trip saveTrip(Trip trip) {
        Optional<Trip> opt = this.tripRepository.findById(trip.getId());
        if (opt.isPresent()) {
            return null;
        }

        if (!this.userRepository.existsById(trip.getUserId())) {
            throw new IllegalArgumentException("User not found with id: " + trip.getUserId());
        }
        if (trip.getDistance() == null || trip.getDistance() <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0");
        }
        String mode = trip.getTransportMode();
        if (!Arrays.asList("CAR", "BUS", "TRAIN").contains(mode)) {
            throw new IllegalArgumentException("Transport mode must be CAR, BUS or TRAIN");
        }
        if ("CAR".equals(mode) && (trip.getVehicleId() == null || !this.vehicleRepository.existsById(trip.getVehicleId()))) {
            throw new IllegalArgumentException("Valid vehicleId required for CAR mode");
        }
        if ("BUS".equals(mode)) {
            String occupancy = trip.getBusOccupancy();
            if (occupancy == null || !Arrays.asList("LOW", "MEDIUM", "HIGH").contains(occupancy)) {
                throw new IllegalArgumentException("Valid busOccupancy required for BUS: LOW, MEDIUM, HIGH");
            }
        }

        Double usedFactor = getEmissionFactorForMode(mode, trip.getVehicleId(), trip.getBusOccupancy());
        double generated = trip.getDistance() * usedFactor;

        double bestAlternativeFactor = getBestAlternativeFactor(mode);
        double alternative = trip.getDistance() * bestAlternativeFactor;

        double avoided = generated - alternative;

        double bestOverallFactor = getBestOverallFactor(mode, trip.getVehicleId(), trip.getBusOccupancy());
        int score = (int) Math.round((bestOverallFactor / usedFactor) * 100);
        score = Math.max(0, Math.min(100, score));

        trip.setGeneratedEmissions(generated);
        trip.setAlternativeEmissions(alternative);
        trip.setAvoidedEmissions(avoided);
        trip.setSustainabilityScore(score);
        trip.setDate(LocalDateTime.now());

        return this.tripRepository.save(trip);
    }

    public List<Trip> findAll() {
        return this.tripRepository.findAll();
    }

    public Trip findById(Long id) {
        Optional<Trip> opt = this.tripRepository.findById(id);
        return opt.orElse(null);
    }

    public void deleteTrip(Long id) {
        this.tripRepository.deleteById(id);
    }

    public Trip editTrip(Long id, Trip tripEdit) {
        Optional<Trip> tripOp = this.tripRepository.findById(id);
        if (tripOp.isPresent()) {
            Trip trip = tripOp.get();
            trip.setOrigin(tripEdit.getOrigin());
            trip.setDestination(tripEdit.getDestination());
            trip.setTransportMode(tripEdit.getTransportMode());
            trip.setVehicleId(tripEdit.getVehicleId());
            trip.setDistance(tripEdit.getDistance());
            trip.setTravelTime(tripEdit.getTravelTime());
            trip.setCost(tripEdit.getCost());
            trip.setBusOccupancy(tripEdit.getBusOccupancy());

            String mode = trip.getTransportMode();
            Double usedFactor = getEmissionFactorForMode(mode, trip.getVehicleId(), trip.getBusOccupancy());
            double generated = trip.getDistance() * usedFactor;
            double bestAlternativeFactor = getBestAlternativeFactor(mode);
            double alternative = trip.getDistance() * bestAlternativeFactor;
            double avoided = generated - alternative;
            double bestOverallFactor = getBestOverallFactor(mode, trip.getVehicleId(), trip.getBusOccupancy());
            int score = (int) Math.round((bestOverallFactor / usedFactor) * 100);
            score = Math.max(0, Math.min(100, score));

            trip.setGeneratedEmissions(generated);
            trip.setAlternativeEmissions(alternative);
            trip.setAvoidedEmissions(avoided);
            trip.setSustainabilityScore(score);

            return this.tripRepository.save(trip);
        }
        return null;
    }

    public List<Trip> findByUserId(Long userId) {
        return this.tripRepository.findByUserId(userId);
    }

    private Double getEmissionFactorForMode(String mode, Long vehicleId, String busOccupancy) {
        if ("CAR".equals(mode)) {
            Vehicle v = vehicleRepository.findById(vehicleId).orElseThrow();
            return v.getEmissionFactor();
        } else if ("BUS".equals(mode)) {
            return getBusFactor(busOccupancy);
        } else if ("TRAIN".equals(mode)) {
            EmissionFactor ef = emissionFactorRepository.findByTransportType("TRAIN");
            if (ef == null) throw new IllegalStateException("TRAIN emission factor not configured");
            return ef.getFactorValue();
        }
        throw new IllegalArgumentException("Unknown mode: " + mode);
    }

    private double getBusFactor(String occupancy) {
        if ("HIGH".equals(occupancy)) return BUS_FACTOR_HIGH;
        else if ("MEDIUM".equals(occupancy)) return BUS_FACTOR_MEDIUM;
        else return BUS_FACTOR_LOW;
    }

    private double getBestAlternativeFactor(String currentMode) {
        List<Double> factors = new ArrayList<>();
        if (!"CAR".equals(currentMode)) {
            Vehicle bestCar = vehicleService.findMostEcoFriendlyCar();
            if (bestCar != null) factors.add(bestCar.getEmissionFactor());
        }
        if (!"BUS".equals(currentMode)) {
            factors.add(BUS_FACTOR_HIGH);
        }
        if (!"TRAIN".equals(currentMode)) {
            EmissionFactor train = emissionFactorRepository.findByTransportType("TRAIN");
            if (train != null) factors.add(train.getFactorValue());
        }
        if (factors.isEmpty()) return 0;
        return factors.stream().min(Double::compare).orElse(0.0);
    }

    private double getBestOverallFactor(String currentMode, Long vehicleId, String busOccupancy) {
        List<Double> factors = new ArrayList<>();
        factors.add(getEmissionFactorForMode(currentMode, vehicleId, busOccupancy));
        if (!"CAR".equals(currentMode)) {
            Vehicle bestCar = vehicleService.findMostEcoFriendlyCar();
            if (bestCar != null) factors.add(bestCar.getEmissionFactor());
        }
        if (!"BUS".equals(currentMode)) {
            factors.add(BUS_FACTOR_HIGH);
        }
        if (!"TRAIN".equals(currentMode)) {
            EmissionFactor train = emissionFactorRepository.findByTransportType("TRAIN");
            if (train != null) factors.add(train.getFactorValue());
        }
        return factors.stream().min(Double::compare).orElse(0.0);
    }
}