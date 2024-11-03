package Gestion.Clinique.Samake.Model;


import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;



@Data
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation; // Date de création automatique

    private String description;
    private String tel;
    private String etat; // "En attente", "Pris en charge", "Traité"
    private boolean estPaye;
    private Date datePaiement;

    @ManyToOne
    @JoinColumn(name = "motif_consultation_id", nullable = false)
    private MotifConsultation motifConsultation;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private Utilisateur user; // Créé par le patient


    @ManyToOne
    @JoinColumn(name = "prisencharge_user_id")
    private Utilisateur medecin; // Pris en charge par le médecin


    // Constructeur pour définir la date automatiquement à "now"
    public Ticket() {
        this.dateCreation = new Date(); // Date et heure actuelles
    }

}
