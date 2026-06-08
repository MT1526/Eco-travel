package ucr.ac.cr.EcoTravel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.EcoTravel.model.*;
import ucr.ac.cr.EcoTravel.model.DTO.*;
import ucr.ac.cr.EcoTravel.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImpactService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private EmissionFactorRepository emissionFactorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleService vehicleService;

    public StatsDTO getStatsByUserId(Long userId) { // Long
        List<Trip> trips = tripRepository.findByUserId(userId);
        if (trips.isEmpty()) {
            return new StatsDTO(0, 0.0, 0.0, 0.0, 0.0, new HashMap<>());
        }

        int totalTrips = trips.size();
        double totalDistance = trips.stream().mapToDouble(Trip::getDistance).sum();
        double totalGenerated = trips.stream().mapToDouble(Trip::getGeneratedEmissions).sum();
        double totalAvoided = trips.stream().mapToDouble(Trip::getAvoidedEmissions).sum();
        double avgScore = trips.stream().mapToInt(Trip::getSustainabilityScore).average().orElse(0.0);

        Map<String, Integer> breakdown = trips.stream()
                .collect(Collectors.groupingBy(Trip::getTransportMode, Collectors.summingInt(e -> 1)));

        return new StatsDTO(totalTrips, totalDistance, totalGenerated, totalAvoided, avgScore, breakdown);
    }

    public List<EmissionFactor> getAllFactors() {
        return emissionFactorRepository.findAll();
    }

    public EmissionFactor updateFactor(FactorUpdateRequest request) {
        Optional<User> adminOpt = userRepository.findById(request.getAdminUserId());
        if (adminOpt.isEmpty() || !"ADMIN".equals(adminOpt.get().getRole())) {
            throw new IllegalArgumentException("Only administrators can modify emission factors");
        }

        EmissionFactor factor = emissionFactorRepository.findByTransportType(request.getTransportType());
        if (factor == null) {
            List<EmissionFactor> all = emissionFactorRepository.findAll();
            long newId = all.stream().mapToLong(EmissionFactor::getId).max().orElse(0L) + 1;
            factor = new EmissionFactor(newId, request.getTransportType(), request.getNewFactor(), "");
        } else {
            factor.setFactorValue(request.getNewFactor());
        }
        return emissionFactorRepository.save(factor);
    }

    public ComparisonResultDTO compare(TripComparisonRequest request) {
        if (request.getDistance() == null || request.getDistance() <= 0) {
            throw new IllegalArgumentException("Distance must be > 0");
        }
        String mode = request.getTransportMode();
        if (!Arrays.asList("CAR", "BUS", "TRAIN").contains(mode)) {
            throw new IllegalArgumentException("Invalid mode");
        }
        if ("CAR".equals(mode) && (request.getVehicleId() == null || !vehicleRepository.existsById(request.getVehicleId()))) {
            throw new IllegalArgumentException("Valid vehicleId required for CAR");
        }

        Double usedFactor = getFactorForMode(mode, request.getVehicleId());
        double generated = request.getDistance() * usedFactor;

        double bestAlternativeFactor = getBestAlternativeFactor(mode);
        double alternative = request.getDistance() * bestAlternativeFactor;
        double avoided = alternative - generated;

        double bestOverall = getBestOverallFactor(mode, request.getVehicleId());
        int score = (int) Math.round((bestOverall / usedFactor) * 100);
        score = Math.max(0, Math.min(100, score));

        String recommended = getRecommendedMode(mode, request.getDistance());

        return new ComparisonResultDTO(generated, alternative, avoided, score, recommended);
    }

    private Double getFactorForMode(String mode, Long vehicleId) {
        if ("CAR".equals(mode)) {
            Vehicle v = vehicleRepository.findById(vehicleId).orElseThrow();
            return v.getEmissionFactor();
        } else if ("BUS".equals(mode)) {
            EmissionFactor ef = emissionFactorRepository.findByTransportType("BUS");
            if (ef == null) throw new IllegalStateException("BUS factor not set");
            return ef.getFactorValue();
        } else if ("TRAIN".equals(mode)) {
            EmissionFactor ef = emissionFactorRepository.findByTransportType("TRAIN");
            if (ef == null) throw new IllegalStateException("TRAIN factor not set");
            return ef.getFactorValue();
        }
        throw new IllegalArgumentException("Unknown mode");
    }

    private double getBestAlternativeFactor(String currentMode) {
        List<Double> factors = new ArrayList<>();
        if (!"CAR".equals(currentMode)) {
            Vehicle bestCar = vehicleService.findMostEcoFriendlyCar();
            if (bestCar != null) factors.add(bestCar.getEmissionFactor());
        }
        if (!"BUS".equals(currentMode)) {
            EmissionFactor bus = emissionFactorRepository.findByTransportType("BUS");
            if (bus != null) factors.add(bus.getFactorValue());
        }
        if (!"TRAIN".equals(currentMode)) {
            EmissionFactor train = emissionFactorRepository.findByTransportType("TRAIN");
            if (train != null) factors.add(train.getFactorValue());
        }
        if (factors.isEmpty()) return 0;
        return factors.stream().min(Double::compare).orElse(0.0);
    }

    private double getBestOverallFactor(String currentMode, Long vehicleId) {
        List<Double> factors = new ArrayList<>();
        factors.add(getFactorForMode(currentMode, vehicleId));
        if (!"CAR".equals(currentMode)) {
            Vehicle bestCar = vehicleService.findMostEcoFriendlyCar();
            if (bestCar != null) factors.add(bestCar.getEmissionFactor());
        }
        if (!"BUS".equals(currentMode)) {
            EmissionFactor bus = emissionFactorRepository.findByTransportType("BUS");
            if (bus != null) factors.add(bus.getFactorValue());
        }
        if (!"TRAIN".equals(currentMode)) {
            EmissionFactor train = emissionFactorRepository.findByTransportType("TRAIN");
            if (train != null) factors.add(train.getFactorValue());
        }
        return factors.stream().min(Double::compare).orElse(0.0);
    }

    private String getRecommendedMode(String currentMode, double distance) {
        Map<String, Double> modeToFactor = new HashMap<>();
        Vehicle bestCar = vehicleService.findMostEcoFriendlyCar();
        if (bestCar != null) modeToFactor.put("CAR", bestCar.getEmissionFactor());
        EmissionFactor bus = emissionFactorRepository.findByTransportType("BUS");
        if (bus != null) modeToFactor.put("BUS", bus.getFactorValue());
        EmissionFactor train = emissionFactorRepository.findByTransportType("TRAIN");
        if (train != null) modeToFactor.put("TRAIN", train.getFactorValue());

        modeToFactor.remove(currentMode);
        if (modeToFactor.isEmpty()) return "No alternative";
        return modeToFactor.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }
}