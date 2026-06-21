package ucr.ac.cr.EcoTravel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_trips")
public class Trip {

    @Id
    private Long id;
    private Long userId;
    private String origin;
    private String destination;
    private String transportMode;
    private Long vehicleId;
    private Double distance;
    private Double travelTime;
    private Double cost;
    private Double generatedEmissions;
    private Double alternativeEmissions;
    private Double avoidedEmissions;
    private Integer sustainabilityScore;
    private LocalDateTime date;
    private String busOccupancy; // NUEVO: "LOW", "MEDIUM", "HIGH" (solo para BUS)

    public Trip() {}

    public Trip(Long id, Long userId, String origin, String destination,
                String transportMode, Long vehicleId, Double distance,
                Double travelTime, Double cost, Double generatedEmissions,
                Double alternativeEmissions, Double avoidedEmissions,
                Integer sustainabilityScore, LocalDateTime date, String busOccupancy) {
        this.id = id;
        this.userId = userId;
        this.origin = origin;
        this.destination = destination;
        this.transportMode = transportMode;
        this.vehicleId = vehicleId;
        this.distance = distance;
        this.travelTime = travelTime;
        this.cost = cost;
        this.generatedEmissions = generatedEmissions;
        this.alternativeEmissions = alternativeEmissions;
        this.avoidedEmissions = avoidedEmissions;
        this.sustainabilityScore = sustainabilityScore;
        this.date = date;
        this.busOccupancy = busOccupancy;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getTransportMode() { return transportMode; }
    public void setTransportMode(String transportMode) { this.transportMode = transportMode; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Double getTravelTime() { return travelTime; }
    public void setTravelTime(Double travelTime) { this.travelTime = travelTime; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public Double getGeneratedEmissions() { return generatedEmissions; }
    public void setGeneratedEmissions(Double generatedEmissions) { this.generatedEmissions = generatedEmissions; }

    public Double getAlternativeEmissions() { return alternativeEmissions; }
    public void setAlternativeEmissions(Double alternativeEmissions) { this.alternativeEmissions = alternativeEmissions; }

    public Double getAvoidedEmissions() { return avoidedEmissions; }
    public void setAvoidedEmissions(Double avoidedEmissions) { this.avoidedEmissions = avoidedEmissions; }

    public Integer getSustainabilityScore() { return sustainabilityScore; }
    public void setSustainabilityScore(Integer sustainabilityScore) { this.sustainabilityScore = sustainabilityScore; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getBusOccupancy() { return busOccupancy; }
    public void setBusOccupancy(String busOccupancy) { this.busOccupancy = busOccupancy; }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", userId=" + userId +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", transportMode='" + transportMode + '\'' +
                ", vehicleId=" + vehicleId +
                ", distance=" + distance +
                ", travelTime=" + travelTime +
                ", cost=" + cost +
                ", generatedEmissions=" + generatedEmissions +
                ", alternativeEmissions=" + alternativeEmissions +
                ", avoidedEmissions=" + avoidedEmissions +
                ", sustainabilityScore=" + sustainabilityScore +
                ", date=" + date +
                ", busOccupancy='" + busOccupancy + '\'' +
                '}';
    }
}