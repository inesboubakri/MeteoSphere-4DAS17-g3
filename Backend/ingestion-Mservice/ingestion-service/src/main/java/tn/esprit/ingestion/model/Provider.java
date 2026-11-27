package tn.esprit.ingestion.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "providers")
public class Provider implements HasId {
    @Id private String id;
    @NotBlank private String nom;
    @NotBlank private String urlBase;
    private String cle;
    @Min(1) private int frequenceSecondes = 60;
    @Min(1) private Integer quotaJour;
    @Min(1) private int priorite = 1;
    private boolean actif = true;

    public Provider() {}
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getNom(){return nom;} public void setNom(String nom){this.nom=nom;}
    public String getUrlBase(){return urlBase;} public void setUrlBase(String urlBase){this.urlBase=urlBase;}
    public String getCle(){return cle;} public void setCle(String cle){this.cle=cle;}
    public int getFrequenceSecondes(){return frequenceSecondes;} public void setFrequenceSecondes(int v){this.frequenceSecondes=v;}
    public Integer getQuotaJour(){return quotaJour;} public void setQuotaJour(Integer quotaJour){this.quotaJour=quotaJour;}
    public int getPriorite(){return priorite;} public void setPriorite(int priorite){this.priorite=priorite;}
    public boolean isActif(){return actif;} public void setActif(boolean actif){this.actif=actif;}
}
