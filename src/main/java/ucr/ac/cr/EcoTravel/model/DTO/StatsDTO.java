package ucr.ac.cr.EcoTravel.model.DTO;

import java.util.Map;

public class StatsDTO {
    private int totalTrips;
    private double totalDistance;
    private double totalGeneratedCO2;
    private double totalAvoidedCO2;
    private double averageScore;
    private Map<String, Integer> breakdownByMode;

    public StatsDTO() {}

    public StatsDTO(int totalTrips, double totalDistance, double totalGeneratedCO2,
                    double totalAvoidedCO2, double averageScore, Map<String, Integer> breakdownByMode) {
        this.totalTrips = totalTrips;
        this.totalDistance = totalDistance;
        this.totalGeneratedCO2 = totalGeneratedCO2;
        this.totalAvoidedCO2 = totalAvoidedCO2;
        this.averageScore = averageScore;
        this.breakdownByMode = breakdownByMode;
    }

    // Getters y Setters
    public int getTotalTrips() { return totalTrips; }
    public void setTotalTrips(int totalTrips) { this.totalTrips = totalTrips; }
    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }
    public double getTotalGeneratedCO2() { return totalGeneratedCO2; }
    public void setTotalGeneratedCO2(double totalGeneratedCO2) { this.totalGeneratedCO2 = totalGeneratedCO2; }
    public double getTotalAvoidedCO2() { return totalAvoidedCO2; }
    public void setTotalAvoidedCO2(double totalAvoidedCO2) { this.totalAvoidedCO2 = totalAvoidedCO2; }
    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public Map<String, Integer> getBreakdownByMode() { return breakdownByMode; }
    public void setBreakdownByMode(Map<String, Integer> breakdownByMode) { this.breakdownByMode = breakdownByMode; }
}