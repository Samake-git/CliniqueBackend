package Gestion.Clinique.Samake.Model;


import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class Admin extends Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



}

