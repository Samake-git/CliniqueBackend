package Gestion.Clinique.Samake.DTO;

import lombok.Data;

import java.time.LocalTime;

@Data
public class DemandeRendezVousRequest {
    private Long emploiDuTempsId;
    private String description;
    private LocalTime heureDebut; // Utilisation de LocalTime
    private LocalTime heureFin;
}
