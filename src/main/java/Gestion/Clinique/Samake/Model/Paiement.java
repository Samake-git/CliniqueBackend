package Gestion.Clinique.Samake.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date datePaiement;
    private boolean estPaye = false;
    private boolean estAnnule;

    private float montant;

    @ManyToOne
    @JoinColumn(name = "ticket_id") // Permettre à ticket_id d'être nullable
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "analyse_id") // Permettre à analyse_id d'être nullable
    private Analyse analyse;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    public Paiement() {
        this.datePaiement = new Date(); // Date de paiement automatique à la création
        this.estAnnule = false;
    }
}