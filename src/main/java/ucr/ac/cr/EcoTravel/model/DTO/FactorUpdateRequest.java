package ucr.ac.cr.EcoTravel.model.DTO;

public class FactorUpdateRequest {
    private String transportType;
    private Double newFactor;
    private Long adminUserId; // cambiado a Long

    public FactorUpdateRequest() {}

    public FactorUpdateRequest(String transportType, Double newFactor, Long adminUserId) {
        this.transportType = transportType;
        this.newFactor = newFactor;
        this.adminUserId = adminUserId;
    }

    public String getTransportType() { return transportType; }
    public void setTransportType(String transportType) { this.transportType = transportType; }
    public Double getNewFactor() { return newFactor; }
    public void setNewFactor(Double newFactor) { this.newFactor = newFactor; }
    public Long getAdminUserId() { return adminUserId; }
    public void setAdminUserId(Long adminUserId) { this.adminUserId = adminUserId; }
}