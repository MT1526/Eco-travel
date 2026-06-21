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

    // Factores de emisión para BUS según ocupación (kg/km)
    private static final double BUS_FACTOR_LOW = 0.05;
    private static final double BUS_FACTOR_MEDIUM = 0.03;
    private static final double BUS_FACTOR_HIGH = 0.008; // Ajustado para que bus lleno sea mejor que tren

    public StatsDTO getStatsByUserId(Long userId) {
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
        if ("BUS".equals(mode)) {
            String occupancy = request.getBusOccupancy();
            if (occupancy == null || !Arrays.asList("LOW", "MEDIUM", "HIGH").contains(occupancy)) {
                throw new IllegalArgumentException("Valid busOccupancy required for BUS: LOW, MEDIUM, HIGH");
            }
        }

        Double usedFactor = getFactorForMode(mode, request.getVehicleId(), request.getBusOccupancy());
        double generated = request.getDistance() * usedFactor;

        double bestAlternativeFactor = getBestAlternativeFactor(mode, request.getVehicleId(), request.getBusOccupancy());
        double alternative = request.getDistance() * bestAlternativeFactor;
        double avoided = generated - alternative;

        double bestOverall = getBestOverallFactor(mode, request.getVehicleId(), request.getBusOccupancy());
        int score = (int) Math.round((bestOverall / usedFactor) * 100);
        score = Math.max(0, Math.min(100, score));

        // Determinar recomendación solo si hay ahorro (el modo actual no es el más eficiente)
        String recommendedMode = null;
        String recommendedVehicleName = null;
        if (avoided > 0) {
            recommendedMode = getRecommendedMode(mode, request.getVehicleId());
            if ("CAR".equals(recommendedMode)) {
                Vehicle bestCar = vehicleService.findMostEcoFriendlyCarExcluding(request.getVehicleId());
                if (bestCar != null) {
                    recommendedVehicleName = bestCar.getName();
                }
            }
        }

        return new ComparisonResultDTO(generated, alternative, avoided, score, recommendedMode, recommendedVehicleName);
    }

    private Double getFactorForMode(String mode, Long vehicleId, String busOccupancy) {
        if ("CAR".equals(mode)) {
            Vehicle v = vehicleRepository.findById(vehicleId).orElseThrow();
            return v.getEmissionFactor();
        } else if ("BUS".equals(mode)) {
            return getBusFactor(busOccupancy);
        } else if ("TRAIN".equals(mode)) {
            EmissionFactor ef = emissionFactorRepository.findByTransportType("TRAIN");
            if (ef == null) throw new IllegalStateException("TRAIN factor not set");
            return ef.getFactorValue();
        }
        throw new IllegalArgumentException("Unknown mode");
    }

    private double getBusFactor(String occupancy) {
        if ("HIGH".equals(occupancy)) return BUS_FACTOR_HIGH;
        else if ("MEDIUM".equals(occupancy)) return BUS_FACTOR_MEDIUM;
        else return BUS_FACTOR_LOW;
    }

    private double getBestAlternativeFactor(String currentMode, Long currentVehicleId, String currentBusOccupancy) {
        List<Double> factors = new ArrayList<>();
        if (!"CAR".equals(currentMode)) {
            Vehicle bestCar = vehicleService.findMostEcoFriendlyCar();
            if (bestCar != null) factors.add(bestCar.getEmissionFactor());
        } else {
            Vehicle bestCar = vehicleService.findMostEcoFriendlyCarExcluding(currentVehicleId);
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
        factors.add(getFactorForMode(currentMode, vehicleId, busOccupancy));
        if (!"CAR".equals(currentMode)) {
            Vehicle bestCar = vehicleService.findMostEcoFriendlyCar();
            if (bestCar != null) factors.add(bestCar.getEmissionFactor());
        } else {
            Vehicle bestCar = vehicleService.findMostEcoFriendlyCarExcluding(vehicleId);
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

    private String getRecommendedMode(String currentMode, Long currentVehicleId) {
        // Obtener el mejor carro excluyendo el actual (si es CAR)
        Vehicle bestCar;
        if ("CAR".equals(currentMode)) {
            bestCar = vehicleService.findMostEcoFriendlyCarExcluding(currentVehicleId);
        } else {
            bestCar = vehicleService.findMostEcoFriendlyCar();
        }

        Double carFactor = bestCar != null ? bestCar.getEmissionFactor() : null;
        Double busFactor = BUS_FACTOR_HIGH;
        Double trainFactor = null;
        EmissionFactor train = emissionFactorRepository.findByTransportType("TRAIN");
        if (train != null) {
            trainFactor = train.getFactorValue();
        }

        // Determinar el modo con el factor mínimo (con prioridad CAR > BUS > TRAIN en empates)
        String bestMode = null;
        double bestValue = Double.MAX_VALUE;

        if (carFactor != null) {
            bestMode = "CAR";
            bestValue = carFactor;
        }
        if (busFactor != null && busFactor < bestValue) {
            bestMode = "BUS";
            bestValue = busFactor;
        } else if (busFactor != null && busFactor == bestValue && bestMode != null && !bestMode.equals("CAR")) {
            if (!"CAR".equals(bestMode)) {
                bestMode = "BUS";
            }
        }
        if (trainFactor != null && trainFactor < bestValue) {
            bestMode = "TRAIN";
            bestValue = trainFactor;
        } else if (trainFactor != null && trainFactor == bestValue && bestMode != null && !bestMode.equals("CAR") && !bestMode.equals("BUS")) {
            bestMode = "TRAIN";
        }

        // Si el modo actual no es CAR, eliminamos ese modo de la consideración si es el mejor
        if (!"CAR".equals(currentMode)) {
            if (currentMode.equals(bestMode)) {
                Map<String, Double> modeToFactor = new LinkedHashMap<>();
                if (carFactor != null && !"CAR".equals(currentMode)) modeToFactor.put("CAR", carFactor);
                if (!"BUS".equals(currentMode)) modeToFactor.put("BUS", busFactor);
                if (trainFactor != null && !"TRAIN".equals(currentMode)) modeToFactor.put("TRAIN", trainFactor);

                if (modeToFactor.isEmpty()) return "No alternative";

                bestMode = null;
                bestValue = Double.MAX_VALUE;
                if (modeToFactor.containsKey("CAR")) {
                    bestMode = "CAR";
                    bestValue = modeToFactor.get("CAR");
                }
                if (modeToFactor.containsKey("BUS") && modeToFactor.get("BUS") < bestValue) {
                    bestMode = "BUS";
                    bestValue = modeToFactor.get("BUS");
                } else if (modeToFactor.containsKey("BUS") && modeToFactor.get("BUS") == bestValue && !"CAR".equals(bestMode)) {
                    bestMode = "BUS";
                }
                if (modeToFactor.containsKey("TRAIN") && modeToFactor.get("TRAIN") < bestValue) {
                    bestMode = "TRAIN";
                    bestValue = modeToFactor.get("TRAIN");
                } else if (modeToFactor.containsKey("TRAIN") && modeToFactor.get("TRAIN") == bestValue && !"CAR".equals(bestMode) && !"BUS".equals(bestMode)) {
                    bestMode = "TRAIN";
                }
                return bestMode != null ? bestMode : "Unknown";
            }
        }

        return bestMode != null ? bestMode : "No alternative";
    }
}