package Gestion.Clinique.Samake.DTO;

import lombok.Data;

import java.time.LocalTime;

@Data
public class RendezVousModifierDTO {

    private String description;
    private LocalTime heureDebut;
    private LocalTime heureFin;
}
