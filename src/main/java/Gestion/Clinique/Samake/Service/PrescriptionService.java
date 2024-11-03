package Gestion.Clinique.Samake.Service;

import Gestion.Clinique.Samake.Model.Prescription;
import Gestion.Clinique.Samake.Model.Patient;
import Gestion.Clinique.Samake.Model.PrescriptionDetail;
import Gestion.Clinique.Samake.Model.Utilisateur;
import Gestion.Clinique.Samake.Repository.PrescriptionDetailRepository;
import Gestion.Clinique.Samake.Repository.PrescriptionRepository;
import Gestion.Clinique.Samake.Repository.PatientRepository;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PrescriptionDetailRepository prescriptionDetailRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Méthode pour récupérer l'utilisateur connecté
    private Utilisateur getUtilisateurConnecte() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé"));
    }

    // Créer une nouvelle prescription
    public Prescription createPrescription(Prescription prescription) throws Exception {
        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = getUtilisateurConnecte();

        // Vérifier si le patient existe
        Optional<Patient> patientOpt = patientRepository.findById(prescription.getPatient().getId());
        if (!patientOpt.isPresent()) {
            throw new IllegalArgumentException("Patient introuvable avec l'ID: " + prescription.getPatient().getId());
        }

        // Assigner l'utilisateur connecté comme médecin et initialiser la date
        prescription.setMedecin(utilisateur);
        prescription.setDatePrescription(new Date());

        // Sauvegarder la prescription
        return prescriptionRepository.save(prescription);
    }


    // Récupérer toutes les prescriptions
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    // Récupérer une prescription par ID
    public Optional<Prescription> getPrescriptionById(Integer id) {
        return prescriptionRepository.findById(id);
    }

    // Mettre à jour une prescription existante
    public Prescription updatePrescription(Integer id, Prescription updatedPrescription) {
        Optional<Prescription> existingPrescription = prescriptionRepository.findById(id);
        if (existingPrescription.isPresent()) {
            Prescription prescription = existingPrescription.get();
            prescription.setDatePrescription(updatedPrescription.getDatePrescription());
            prescription.setCommentaire(updatedPrescription.getCommentaire());
            prescription.setPatient(updatedPrescription.getPatient());
            prescription.setMedecin(updatedPrescription.getMedecin());
            return prescriptionRepository.save(prescription);
        } else {
            throw new RuntimeException("Prescription introuvable avec ID: " + id);
        }
    }

    // Supprimer une prescription par ID
    public void deletePrescription(Integer id) {
        prescriptionRepository.deleteById(id);
    }

    // Méthode pour récupérer tous les détails de prescription par ID de prescription
    public List<PrescriptionDetail> getDetailsByPrescriptionId(Integer prescriptionId) {
        return prescriptionDetailRepository.findByPrescriptionId(prescriptionId);
    }


    // Récupérer les prescriptions pour le patient connecté
    public List<Prescription> getPrescriptionsByConnectedPatient() throws Exception {
        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = getUtilisateurConnecte();

        // Vérifier si l'utilisateur est un patient
        if (!(utilisateur instanceof Patient)) {
            throw new Exception("L'utilisateur connecté n'est pas un patient.");
        }

        // Récupérer le patient
        Patient patient = (Patient) utilisateur;

        // Récupérer les prescriptions associées au patient
        return prescriptionRepository.findByPatient(patient);
    }
}