
package Gestion.Clinique.Samake.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;
import java.util.Date;

@Data
@Entity
public class PrescriptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nomMedicament;
    private String dosage;
    private String frequence;
    private String duree;
    private String instructions;
    @Temporal(TemporalType.DATE)
    private Date datePremierDose;
    private LocalTime heurePremierDose;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    @JsonBackReference
    private Prescription prescription;
}

