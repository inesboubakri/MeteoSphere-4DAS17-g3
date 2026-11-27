package tn.esprit.ingestion.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "monitored_zones")
public class MonitoredZone implements HasId {
    @Id private String id;
    @NotBlank private String nom;
    private String description;
    private double latitude;
    private double longitude;
    private double rayonKm;
    private boolean active = true;

    public MonitoredZone() {}
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getNom(){return nom;} public void setNom(String nom){this.nom=nom;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
    public double getLatitude(){return latitude;} public void setLatitude(double latitude){this.latitude=latitude;}
    public double getLongitude(){return longitude;} public void setLongitude(double longitude){this.longitude=longitude;}
    public double getRayonKm(){return rayonKm;} public void setRayonKm(double rayonKm){this.rayonKm=rayonKm;}
    public boolean isActive(){return active;} public void setActive(boolean active){this.active=active;}
}
