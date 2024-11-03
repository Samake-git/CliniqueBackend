package Gestion.Clinique.Samake.Repository;



import Gestion.Clinique.Samake.Model.Consultation;
import Gestion.Clinique.Samake.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {
    // Méthode pour récupérer les consultations par patient
    List<Consultation> findByPatient(Patient patient);
}
