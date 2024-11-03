package Gestion.Clinique.Samake.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;

@Data
@Entity
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate jour;


    private Time heureDebut;

    private Time heureFin;

    private String statut; // "En attente", "Approuvé", "Rejeté", etc.

    private String description; // Nouvelle colonne pour que le patient explique son besoin

    @ManyToOne
    @JoinColumn(name = "emplois_du_temps_id", nullable = false)
    private EmploiDuTemps emploiDuTemps;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Utilisateur patient;  // Relation avec le patient
}
