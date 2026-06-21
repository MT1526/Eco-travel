package ucr.ac.cr.EcoTravel.model.DTO;

public class TripComparisonRequest {
    private String transportMode; // CAR, BUS, TRAIN
    private Double distance;
    private Long vehicleId;
    private String busOccupancy; // NUEVO: "LOW", "MEDIUM", "HIGH" (solo para BUS)

    public TripComparisonRequest() {}

    public TripComparisonRequest(String transportMode, Double distance, Long vehicleId, String busOccupancy) {
        this.transportMode = transportMode;
        this.distance = distance;
        this.vehicleId = vehicleId;
        this.busOccupancy = busOccupancy;
    }

    public String getTransportMode() { return transportMode; }
    public void setTransportMode(String transportMode) { this.transportMode = transportMode; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getBusOccupancy() { return busOccupancy; }
    public void setBusOccupancy(String busOccupancy) { this.busOccupancy = busOccupancy; }
}