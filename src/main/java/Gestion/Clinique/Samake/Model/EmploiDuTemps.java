package Gestion.Clinique.Samake.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
public class EmploiDuTemps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate jour;

    private LocalTime heureDebut; // Heure de début de la disponibilité
    private LocalTime heureFin; // Heure de fin de la disponibilité
    private String status; // Si le médecin est libre maintenant ou pas

    // Ajout de la relation vers l'utilisateur (médecin ou admin)
    @ManyToOne
    @JoinColumn(name = "createur_id")
    private Utilisateur createur;

    public boolean isValidDisponibility() {
        return heureDebut.isBefore(heureFin);
    }

}
