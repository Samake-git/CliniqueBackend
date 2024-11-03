package Gestion.Clinique.Samake.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class ResultatExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date dateExamen;
    private String nomExamen;
    private String resultat;
    private String unite;
    private String norme;
    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "laborantin_id", nullable = false)
    private Utilisateur user;

    @ManyToOne
    @JoinColumn(name = "analyse_id")
    private Analyse analyse;

}
