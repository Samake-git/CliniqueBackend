package Gestion.Clinique.Samake.Repository;


import Gestion.Clinique.Samake.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String Email);
}