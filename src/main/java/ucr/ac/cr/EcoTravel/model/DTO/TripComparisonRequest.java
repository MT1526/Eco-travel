package ucr.ac.cr.EcoTravel.model.DTO;

public class TripComparisonRequest {
    private String transportMode; // CAR, BUS, TRAIN
    private Double distance;
    private Long vehicleId; // cambiado a Long

    public TripComparisonRequest() {}

    public TripComparisonRequest(String transportMode, Double distance, Long vehicleId) {
        this.transportMode = transportMode;
        this.distance = distance;
        this.vehicleId = vehicleId;
    }

    public String getTransportMode() { return transportMode; }
    public void setTransportMode(String transportMode) { this.transportMode = transportMode; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
}