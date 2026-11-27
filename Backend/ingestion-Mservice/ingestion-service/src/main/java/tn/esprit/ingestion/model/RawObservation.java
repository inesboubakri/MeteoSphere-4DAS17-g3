package tn.esprit.ingestion.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Document(collection = "raw_observations")
public class RawObservation implements HasId {
    @Id private String id;
    @NotBlank private String providerId;
    private String zoneId;
    private String typeDonnee;
    private Instant dateObservation = Instant.now();
    private Map<String,Object> contenu = new LinkedHashMap<>();

    public RawObservation() {}
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getProviderId(){return providerId;} public void setProviderId(String providerId){this.providerId=providerId;}
    public String getZoneId(){return zoneId;} public void setZoneId(String zoneId){this.zoneId=zoneId;}
    public String getTypeDonnee(){return typeDonnee;} public void setTypeDonnee(String typeDonnee){this.typeDonnee=typeDonnee;}
    public Instant getDateObservation(){return dateObservation;} public void setDateObservation(Instant dateObservation){this.dateObservation=dateObservation;}
    public Map<String,Object> getContenu(){return contenu;} public void setContenu(Map<String,Object> contenu){this.contenu=contenu;}
}
