package tn.esprit.ingestion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "ingestion_jobs")
public class IngestionJob implements HasId {
    @Id private String id;
    private String providerId;
    private String zoneId;
    private StatutJob statut = StatutJob.EN_ATTENTE;
    private Instant dateDebut;
    private Instant dateFin;
    private int nombreObservations;
    private String message;

    public IngestionJob() {}
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getProviderId(){return providerId;} public void setProviderId(String providerId){this.providerId=providerId;}
    public String getZoneId(){return zoneId;} public void setZoneId(String zoneId){this.zoneId=zoneId;}
    public StatutJob getStatut(){return statut;} public void setStatut(StatutJob statut){this.statut=statut;}
    public Instant getDateDebut(){return dateDebut;} public void setDateDebut(Instant dateDebut){this.dateDebut=dateDebut;}
    public Instant getDateFin(){return dateFin;} public void setDateFin(Instant dateFin){this.dateFin=dateFin;}
    public int getNombreObservations(){return nombreObservations;} public void setNombreObservations(int nombreObservations){this.nombreObservations=nombreObservations;}
    public String getMessage(){return message;} public void setMessage(String message){this.message=message;}
}
