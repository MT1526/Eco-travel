package ucr.ac.cr.EcoTravel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_emission_factor")
public class EmissionFactor {

    @Id
    private Long id;
    private String transportType;
    private Double factorValue;
    private String description;

    public EmissionFactor() {}

    public EmissionFactor(Long id, String transportType, Double factorValue, String description) {
        this.id = id;
        this.transportType = transportType;
        this.factorValue = factorValue;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransportType() { return transportType; }
    public void setTransportType(String transportType) { this.transportType = transportType; }

    public Double getFactorValue() { return factorValue; }
    public void setFactorValue(Double factorValue) { this.factorValue = factorValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "EmissionFactor{" +
                "id=" + id +
                ", transportType='" + transportType + '\'' +
                ", factorValue=" + factorValue +
                ", description='" + description + '\'' +
                '}';
    }
}