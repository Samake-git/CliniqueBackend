package Gestion.Clinique.Samake.Repository;

import Gestion.Clinique.Samake.Model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    // Méthode pour obtenir la somme totale des paiements
     List<Paiement> findByEstPayeTrue();
}
