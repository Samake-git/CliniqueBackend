package Gestion.Clinique.Samake.Model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class CategorieAnalyse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String description;

}
