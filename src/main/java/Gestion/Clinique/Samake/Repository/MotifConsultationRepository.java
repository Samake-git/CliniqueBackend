package Gestion.Clinique.Samake.Repository;


import Gestion.Clinique.Samake.Model.MotifConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotifConsultationRepository extends JpaRepository<MotifConsultation, Integer> {
}