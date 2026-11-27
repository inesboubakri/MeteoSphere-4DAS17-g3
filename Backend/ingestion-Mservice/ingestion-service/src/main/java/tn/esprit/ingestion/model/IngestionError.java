package tn.esprit.ingestion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "ingestion_errors")
public class IngestionError implements HasId {
    @Id private String id;
    private String jobId;
    private String providerId;
    private String code;
    private String message;
    private String details;
    private Instant dateErreur = Instant.now();

    public IngestionError() {}
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getJobId(){return jobId;} public void setJobId(String jobId){this.jobId=jobId;}
    public String getProviderId(){return providerId;} public void setProviderId(String providerId){this.providerId=providerId;}
    public String getCode(){return code;} public void setCode(String code){this.code=code;}
    public String getMessage(){return message;} public void setMessage(String message){this.message=message;}
    public String getDetails(){return details;} public void setDetails(String details){this.details=details;}
    public Instant getDateErreur(){return dateErreur;} public void setDateErreur(Instant dateErreur){this.dateErreur=dateErreur;}
}
