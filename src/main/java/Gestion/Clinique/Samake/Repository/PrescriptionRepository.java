package Gestion.Clinique.Samake.Repository;


import Gestion.Clinique.Samake.Model.Patient;
import Gestion.Clinique.Samake.Model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    List<Prescription> findByPatient(Patient patient);
}