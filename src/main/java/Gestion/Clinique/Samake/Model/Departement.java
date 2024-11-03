package Gestion.Clinique.Samake.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String description;

}
