package Gestion.Clinique.Samake.Controlleur;


import Gestion.Clinique.Samake.DTO.DemandeRendezVousRequest;
import Gestion.Clinique.Samake.DTO.RendezVousModifierDTO;
import Gestion.Clinique.Samake.Model.*;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import Gestion.Clinique.Samake.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;


@RestController
@RequestMapping("/api/patient")
public class PatientControlleur {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private AnalyseService analyseService;

    @Autowired
    private RendezVousService rendezVousService;

    @Autowired
    private ResultatExamenService resultatExamenService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EmploiDuTempsService emploiDuTempsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Endpoint pour afficher un admin par ID
    @GetMapping("/utilisateurs/afficher/{id}")
    public ResponseEntity<?> afficherAdmin(@PathVariable Long id) {
        Optional<Utilisateur> adminOptional = utilisateurRepository.findById(id);

        if (adminOptional.isPresent()) {
            return ResponseEntity.ok(adminOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Admin non trouvé avec l'id : " + id);
        }
    }

    // Endpoint pour récupérer les prescriptions du patient connecté
    @GetMapping("/Prescription/afficher")
    public ResponseEntity<List<Prescription>> getPrescriptionsByConnectedPatient() {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByConnectedPatient();
            return new ResponseEntity<>(prescriptions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden si ce n'est pas un patient
        }
    }

    // Endpoint pour récupérer les résultats d'examen du patient connecté
    @GetMapping("/Resultat_Analyse/afficher")
    public ResponseEntity<List<ResultatExamen>> getResultatsByConnectedPatient() {
        try {
            List<ResultatExamen> resultats = resultatExamenService.getResultatsByConnectedPatient();
            return new ResponseEntity<>(resultats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden si ce n'est pas un patient
        }
    }




    // Endpoint pour récupérer les analyses d'un patient
    @GetMapping("/Analyses/afficher")
    public ResponseEntity<List<Analyse>> getAnalysesByConnectedPatient() {
        try {
            List<Analyse> analyses = analyseService.getAnalysesByConnectedPatient();
            return new ResponseEntity<>(analyses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden si ce n'est pas un patient
        }
    }




    // Endpoint pour récupérer les consultations du patient connecté
    @GetMapping("/consultations/afficher")
    public ResponseEntity<List<Consultation>> getConsultationsByPatient() {
        try {
            List<Consultation> consultations = consultationService.getConsultationsByPatient();
            return new ResponseEntity<>(consultations, HttpStatus.OK); // 200 OK
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (Exception e) {
            // Log l'erreur ici si nécessaire
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    @PutMapping("/modifierMotDePasse")
    public ResponseEntity<String> modifierMotDePasse(@RequestBody Map<String, String> requestBody) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String ancienMotDePasse = requestBody.get("ancienMotDePasse");
            String nouveauMotDePasse = requestBody.get("nouveauMotDePasse");

            if (ancienMotDePasse == null || ancienMotDePasse.isEmpty() ||
                    nouveauMotDePasse == null || nouveauMotDePasse.isEmpty()) {
                return ResponseEntity.badRequest().body("Les mots de passe ne peuvent pas être vides.");
            }

            // Vérifiez que l'ancien mot de passe est correct
            Utilisateur utilisateur = utilisateurRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + username));

            if (!passwordEncoder.matches(ancienMotDePasse, utilisateur.getPassword())) {
                return ResponseEntity.badRequest().body("L'ancien mot de passe est incorrect.");
            }

            utilisateur.setPassword(passwordEncoder.encode(nouveauMotDePasse));
            utilisateurRepository.save(utilisateur);

            return ResponseEntity.ok("Mot de passe mis à jour avec succès pour l'utilisateur: " + username);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

// Endpoint pour rendezVOUS

    @PostMapping("/rendezvous/demander")
    public ResponseEntity<?> demanderRendezVous(@RequestBody DemandeRendezVousRequest request) {
        try {
            RendezVous rendezVous = rendezVousService.demanderRendezVous(
                    request.getEmploiDuTempsId(),
                    request.getDescription(),
                    request.getHeureDebut(),
                    request.getHeureFin()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(rendezVous);
        } catch (Exception e) {
            // Log de l'exception pour en savoir plus
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
        }
    }


    // Endpoint pour obtenir les rendez-vous du patient connecté
    @GetMapping("/rendezvous/mes-rendezvous")
    public ResponseEntity<List<RendezVous>> getRendezVousParPatientConnecte() {
        try {
            List<RendezVous> rendezVousList = rendezVousService.obtenirRendezVousParPatientConnecte();
            return ResponseEntity.ok(rendezVousList);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // Endpoint pour modifier un rendez-vous
    @PutMapping("/rendezvous/modifier/{id}")
    public ResponseEntity<RendezVous> modifierRendezVous(
            @PathVariable Long id,
            @RequestBody RendezVousModifierDTO dto) {
        try {
            RendezVous rendezVous = rendezVousService.modifierRendezVous(id, dto.getDescription(), dto.getHeureDebut(), dto.getHeureFin());
            return ResponseEntity.ok(rendezVous);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Endpoint pour supprimer un rendez-vous
    @DeleteMapping("/rendezvous/supprimer/{id}")
    public ResponseEntity<String> supprimerRendezVous(@PathVariable Long id) {
        try {
            rendezVousService.supprimerRendezVous(id);
            return ResponseEntity.ok("Rendez-vous supprimé avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erreur lors de la suppression du rendez-vous.");
        }
    }


    @GetMapping("/rendezvous/afficher/patient")
    public ResponseEntity<List<RendezVous>> obtenirTousRendezVousPatient() {
        List<RendezVous> rendezVousList = rendezVousService.obtenirTousRendezVous();
        return ResponseEntity.ok(rendezVousList);
    }

    @GetMapping("/rendezvous/afficher/{id}")
    public ResponseEntity<RendezVous> obtenirRendezVousParId(@PathVariable Long id) {
        try {
            RendezVous rendezVous = rendezVousService.obtenirRendezVousParId(id);
            return ResponseEntity.ok(rendezVous);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

 // Endpoint Emplois du temps

    @GetMapping("/EmploisDuTemps/afficher")
    public List<EmploiDuTemps> afficherDisponibilites() {
        return emploiDuTempsService.afficherDisponibilites();
    }

    @GetMapping("/EmploisDuTemps/filter")
    public ResponseEntity<List<EmploiDuTemps>> filterDisponibilites(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String specialite,
            @RequestParam(required = false) LocalDate jour) {
        List<EmploiDuTemps> disponibilites = emploiDuTempsService.afficherDisponibilites(statut, specialite, jour);
        return ResponseEntity.ok(disponibilites);
    }




}




