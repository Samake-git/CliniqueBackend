package Gestion.Clinique.Samake.Repository;


import Gestion.Clinique.Samake.Model.EmploiDuTemps;
import Gestion.Clinique.Samake.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmploiDuTempsRepository extends JpaRepository<EmploiDuTemps, Long> {
    List<EmploiDuTemps> findByJour(LocalDate jour); // Methode pour trouver la disponibilité par jour
    // Méthode pour trouver les emplois du temps par statut
    List<EmploiDuTemps> findByStatus(String status);

    // Nouvelle méthode pour trouver les emplois du temps par spécialité du créateur
    List<EmploiDuTemps> findByCreateurSpecialite(String specialite);

    // Méthode pour trouver les emplois du temps par créateur
    List<EmploiDuTemps> findByCreateur(Utilisateur createur);
}
