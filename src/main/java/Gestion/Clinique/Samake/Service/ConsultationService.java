package Gestion.Clinique.Samake.Service;


import Gestion.Clinique.Samake.Model.Consultation;
import Gestion.Clinique.Samake.Model.Patient;
import Gestion.Clinique.Samake.Model.Utilisateur;
import Gestion.Clinique.Samake.Repository.ConsultationRepository;
import Gestion.Clinique.Samake.Repository.PatientRepository;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ConsultationService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Utilisateur getUtilisateurConnecte() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé"));

        return utilisateur;
    }

    public Consultation ajouterConsultation(Long patientId, String description, String note) throws Exception {
        // Vérifier si le patient existe
        if (patientId == null || !patientRepository.existsById(patientId)) {
            throw new Exception("Patient non trouvé avec l'ID : " + patientId);
        }

        // Récupérer le patient
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new Exception("Patient non trouvé avec l'ID : " + patientId));

        // Récupérer l'utilisateur connecté (médecin)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("Médecin non trouvé : " + username));

        if ("MEDECIN".equals(utilisateur.getRoleType())) {
            throw new Exception("Seul un médecin peut ajouter une consultation.");
        }

        // Créer la consultation et assigner les valeurs
        Consultation consultation = new Consultation();
        consultation.setDescription(description);
        consultation.setNote(note);
        consultation.setMedecin(utilisateur);
        consultation.setPatient(patient);
        consultation.setDateCreation(new Date());

        return consultationRepository.save(consultation);
    }



    // Modifier une consultation existante
    public Consultation modifierConsultation(int id, Consultation updatedConsultation) throws Exception {
        Consultation ConsultationRequest = consultationRepository.findById(id)
                .orElseThrow(() -> new Exception("Consultation non trouvée avec l'ID : " + id));

        // Mettre à jour les champs modifiables uniquement
        ConsultationRequest.setDescription(updatedConsultation.getDescription());
        ConsultationRequest.setNote(updatedConsultation.getNote());

        return consultationRepository.save(ConsultationRequest);
    }

    // Supprimer une consultation
    public void supprimerConsultation(int id) throws Exception {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new Exception("Consultation non trouvée avec l'ID : " + id));
        consultationRepository.deleteById(id);
    }

    // Afficher toutes les consultations
    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAll();
    }

    // Afficher une consultation par ID
    public Consultation getConsultationById(int id) throws Exception {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new Exception("Consultation non trouvée avec l'ID : " + id));
    }


    public List<Consultation> getConsultationsByPatient() throws Exception {
        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = getUtilisateurConnecte();
        System.out.println("Utilisateur connecté : " + utilisateur.getEmail());

        // Vérifier si l'utilisateur est un patient
        if (!(utilisateur instanceof Patient)) {
            throw new Exception("L'utilisateur connecté n'est pas un patient.");
        }

        // Récupérer le patient
        Patient patient = (Patient) utilisateur;

        // Récupérer les consultations associées au patient
        List<Consultation> consultations = consultationRepository.findByPatient(patient);
        System.out.println("Consultations trouvées pour le patient : " + patient.getUsername());

        if (consultations.isEmpty()) {
            throw new Exception("Aucune consultation trouvée pour le patient : " + patient.getUsername());
        }

        return consultations;
    }


}
