// Prescription.java
package Gestion.Clinique.Samake.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.DATE)
    private Date datePrescription;

    private String commentaire;

    // Relation Many-to-One avec Patient
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Relation Many-to-One avec Medecin
    @ManyToOne
    @JoinColumn(name = "medecin_id")
    private Utilisateur medecin;


    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PrescriptionDetail> prescriptionDetails;
}

