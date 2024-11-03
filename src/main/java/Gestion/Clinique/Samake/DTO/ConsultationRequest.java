package Gestion.Clinique.Samake.DTO;

import Gestion.Clinique.Samake.Model.Patient;
import Gestion.Clinique.Samake.Model.Ticket;
import Gestion.Clinique.Samake.Model.Utilisateur;
import lombok.Data;

import java.util.Date;

@Data
public class ConsultationRequest {
    private Patient patient;
    private String description;
    private String note;
    private Utilisateur medecin;
    private Date dateCreation;


}

