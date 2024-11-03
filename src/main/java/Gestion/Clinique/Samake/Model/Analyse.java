package Gestion.Clinique.Samake.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
@Entity
public class Analyse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String appareilUtilise;
    @Temporal(TemporalType.DATE)
    private Date dateAnalyse;
    private boolean estPaye;
    private String etat;

    @ManyToOne
    @JoinColumn(name = "type_analyse_id", nullable = false)
    private TypeAnalyse typeAnalyse;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "laborantin_id", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Utilisateur user;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private CategorieAnalyse categorieAnalyse;


}
