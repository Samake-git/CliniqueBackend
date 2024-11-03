package Gestion.Clinique.Samake.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Patient extends Utilisateur{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int age;
    private String poids;
    private String ethenie;

    @Enumerated(EnumType.STRING)
    private StatusCompte status;

    @ManyToOne
    @JoinColumn(name = "Createur_id")
    private Utilisateur createur;

    @OneToMany(mappedBy = "patient")
    @JsonManagedReference
    private List<Consultation> consultations;

}
