package ucr.ac.cr.EcoTravel.model.DTO;

public class ComparisonResultDTO {
    private double generatedEmissions;
    private double alternativeEmissions;
    private double avoidedEmissions;
    private int sustainabilityScore;
    private String recommendedMode;

    public ComparisonResultDTO() {}

    public ComparisonResultDTO(double generatedEmissions, double alternativeEmissions,
                               double avoidedEmissions, int sustainabilityScore, String recommendedMode) {
        this.generatedEmissions = generatedEmissions;
        this.alternativeEmissions = alternativeEmissions;
        this.avoidedEmissions = avoidedEmissions;
        this.sustainabilityScore = sustainabilityScore;
        this.recommendedMode = recommendedMode;
    }

    // Getters y Setters
    public double getGeneratedEmissions() { return generatedEmissions; }
    public void setGeneratedEmissions(double generatedEmissions) { this.generatedEmissions = generatedEmissions; }
    public double getAlternativeEmissions() { return alternativeEmissions; }
    public void setAlternativeEmissions(double alternativeEmissions) { this.alternativeEmissions = alternativeEmissions; }
    public double getAvoidedEmissions() { return avoidedEmissions; }
    public void setAvoidedEmissions(double avoidedEmissions) { this.avoidedEmissions = avoidedEmissions; }
    public int getSustainabilityScore() { return sustainabilityScore; }
    public void setSustainabilityScore(int sustainabilityScore) { this.sustainabilityScore = sustainabilityScore; }
    public String getRecommendedMode() { return recommendedMode; }
    public void setRecommendedMode(String recommendedMode) { this.recommendedMode = recommendedMode; }
}