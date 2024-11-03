package Gestion.Clinique.Samake.Model;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
public class Medecin extends Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;


}
