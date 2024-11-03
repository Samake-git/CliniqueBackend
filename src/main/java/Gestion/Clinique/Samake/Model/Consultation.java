package Gestion.Clinique.Samake.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;
    private String note;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation; // Date de création automatique

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "medecin_id", nullable = false)
    private Utilisateur medecin;

//    @ManyToOne
//    @JoinColumn(name = "ticket_id")
//    private Ticket ticket;

    // Constructeur pour définir la date automatiquement à "now"
    public Consultation() {
        this.dateCreation = new Date(); // Date et heure actuelles
    }
}
