package Gestion.Clinique.Samake.Repository;

import Gestion.Clinique.Samake.Model.Analyse;
import Gestion.Clinique.Samake.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AnalyseRepository extends JpaRepository<Analyse, Integer> {
    long countByEtat(String enAttente);
    Optional<Analyse> findById(Long analyseId);
    List<Analyse> findByPatient(Patient patient);
    long countByPatient(Patient patient);
}
