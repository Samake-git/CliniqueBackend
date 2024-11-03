package Gestion.Clinique.Samake.Repository;


import Gestion.Clinique.Samake.Model.EmploiDuTemps;
import Gestion.Clinique.Samake.Model.Patient;
import Gestion.Clinique.Samake.Model.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    // Requête pour trouver les rendez-vous qui sont dans les 30 prochaines minutes
    @Query("SELECT r FROM RendezVous r WHERE r.statut = 'Approuvé' AND " +
            "r.heureDebut BETWEEN :now AND :next30Minutes")
    List<RendezVous> findRendezVousInNext30Minutes(@Param("now") LocalTime now, @Param("next30Minutes") LocalTime next30Minutes);

    List<RendezVous> findByPatient(Patient patient);

    List<RendezVous> findByEmploiDuTemps(EmploiDuTemps emploiDuTemps);

}
