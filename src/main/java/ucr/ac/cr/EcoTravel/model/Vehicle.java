package ucr.ac.cr.EcoTravel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_vehicles")
public class Vehicle {

    @Id
    private Long id; // cambiado a Long
    private String name;
    private String category;
    private Double emissionFactor;
    private Long createdBy; // ID del usuario que lo agregó (también Long)

    public Vehicle() {
    }

    public Vehicle(Long id, String name, String category, Double emissionFactor, Long createdBy) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.emissionFactor = emissionFactor;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getEmissionFactor() {
        return emissionFactor;
    }

    public void setEmissionFactor(Double emissionFactor) {
        this.emissionFactor = emissionFactor;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", emissionFactor=" + emissionFactor +
                ", createdBy=" + createdBy +
                '}';
    }
}