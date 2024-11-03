package Gestion.Clinique.Samake.Controlleur;


import Gestion.Clinique.Samake.DTO.ConsultationRequest;
import Gestion.Clinique.Samake.Model.*;
import Gestion.Clinique.Samake.Repository.RoleRepository;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import Gestion.Clinique.Samake.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medecin")
public class MedecinControlleur {


    @Autowired
    private AnalyseService analyseService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private RendezVousService rendezVousService;

    @Autowired
    private PrescriptionDetailService prescriptionDetailService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    FilesStorageService storageService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MotifConsultationService motifConsultationService;

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private EmploiDuTempsService emploiDuTempsService;



    @GetMapping("/patients/afficherTous")
      public ResponseEntity<List<Utilisateur>> getAllPatientUsers() {
        List<Utilisateur> patients = adminService.listUtilisateursPatient();
        return ResponseEntity.ok(patients);
    }


    // Endpoint pour prendre en charge un ticket (par un médecin)
    @PutMapping("/ticket/prise-en-charge/{id}")
    public ResponseEntity<?> prendreEnChargeTicket(@PathVariable Long id) { // Changez int en Long
        try {
            Ticket ticket = ticketService.prendreEnChargeTicket(id);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Endpoint pour marquer un ticket comme traité
    @PutMapping("/ticket/traiter/{id}")
    public ResponseEntity<?> traiterTicket(@PathVariable Long id) { // Changez int en Long
        try {
            Ticket ticket = ticketService.traiterTicket(id);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // Récupérer tous les motifs de consultation
    @GetMapping(path = "/MotifConsultation/afficherTous")
    public List<MotifConsultation> getAllMotifs() {
        return motifConsultationService.getAllMotifs();
    }

    // Récupérer un motif de consultation par ID
    @GetMapping("/MotifConsultation/afficherPar/{id}")
    public ResponseEntity<MotifConsultation> getMotifById(@PathVariable int id) {
        try {
            MotifConsultation motif = motifConsultationService.getMotifById(id);
            return new ResponseEntity<>(motif, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Créer un nouveau motif de consultation
    @PostMapping(path = "/motifconsultation/Creer")
    public ResponseEntity<MotifConsultation> createMotif(@RequestBody MotifConsultation motif) {
        try {
            MotifConsultation createdMotif = motifConsultationService.ajouterMotifConsultation(motif);
            return new ResponseEntity<>(createdMotif, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Modifier un motif de consultation
    @PutMapping("/motifconsultation/modifier/{id}")
    public ResponseEntity<MotifConsultation> updateMotif(@PathVariable int id, @RequestBody MotifConsultation motifDetails) {
        try {
            MotifConsultation updatedMotif = motifConsultationService.modifierMotifConsultation(id, motifDetails);
            return new ResponseEntity<>(updatedMotif, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Supprimer un motif de consultation
    @DeleteMapping("/motifconsultation/supprimer/{id}")
    public ResponseEntity<Void> deleteMotif(@PathVariable int id) {
        try {
            motifConsultationService.deleteMotif(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Retourne un statut 204 (No Content) en cas de succès
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Retourne un statut 404 si le motif n'est pas trouvé
        }
    }


    // Ajouter une consultation
    @PostMapping("/Consultation/ajouter")
    public ResponseEntity<Consultation> addConsultation(@RequestBody ConsultationRequest request) throws Exception {
        Long patientId = request.getPatient().getId();  // Récupérer l'ID du patient

        // Ajouter la consultation avec les valeurs fournies dans la requête
        Consultation consultation = consultationService.ajouterConsultation(
                patientId,
                request.getDescription(),
                request.getNote()
        );

        return ResponseEntity.status(201).body(consultation);
    }


    // Modifier une consultation
    @PutMapping("/Consultation/modifier/{id}")
    public ResponseEntity<?> modifierConsultation(@PathVariable int id, @RequestBody Consultation updatedConsultation) {
        try {
            Consultation consultationModifiee = consultationService.modifierConsultation(id, updatedConsultation);
            return ResponseEntity.ok(consultationModifiee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Supprimer une consultation
    @DeleteMapping("/Consultation/supprimer/{id}")
    public ResponseEntity<?> supprimerConsultation(@PathVariable int id) {
        try {
            consultationService.supprimerConsultation(id);
            return ResponseEntity.ok("Consultation supprimée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Afficher toutes les consultations
    @GetMapping("/Consultation/affichertous")
    public ResponseEntity<List<Consultation>> getAllConsultations() {
        List<Consultation> consultations = consultationService.getAllConsultations();
        return ResponseEntity.ok(consultations);
    }

    // Afficher une consultation par ID
    @GetMapping("/Consultation/afficher/{id}")
    public ResponseEntity<?> getConsultationById(@PathVariable int id) {
        try {
            Consultation consultation = consultationService.getConsultationById(id);
            return ResponseEntity.ok(consultation);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }




    // Créer une nouvelle prescription
    @PostMapping(path = "/prescriptions/ajouter")
    public ResponseEntity<?> createPrescription(@RequestBody Prescription prescription) {
        try {
            Prescription createdPrescription = prescriptionService.createPrescription(prescription);
            return ResponseEntity.ok(createdPrescription);
        } catch (IllegalArgumentException e) {
            // Si le patient n'existe pas, renvoyer un message clair
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Pour toute autre exception, renvoyer un message générique
            return ResponseEntity.badRequest().body("Erreur lors de la création de la prescription.");
        }
    }


    // Récupérer toutes les prescriptions
    @GetMapping("/prescriptions/afficherTous")
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    // Récupérer une prescription par ID
    @GetMapping("/prescriptions/afficher/{id}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable Integer id) {
        return prescriptionService.getPrescriptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Mettre à jour une prescription
    @PutMapping("/prescriptions/modifier/{id}")
    public ResponseEntity<Prescription> updatePrescription(@PathVariable Integer id, @RequestBody Prescription updatedPrescription) {
        try {
            Prescription prescription = prescriptionService.updatePrescription(id, updatedPrescription);
            return ResponseEntity.ok(prescription);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }



    // Supprimer une prescription par ID
    @DeleteMapping("/prescriptions/supprimer/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable int id) {
        try {
            prescriptionService.deletePrescription(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/prescriptionDetail/prescription/{prescriptionId}")
    public ResponseEntity<List<PrescriptionDetail>> getPrescriptionDetailsByPrescriptionId(@PathVariable Integer prescriptionId) {
        List<PrescriptionDetail> details = prescriptionDetailService.getPrescriptionDetailsByPrescriptionId(prescriptionId);
        return ResponseEntity.ok(details);
    }


    // Créer un nouveau détail de prescription
    @PostMapping(path = "/prescriptionDetail/ajouter")
    public ResponseEntity<PrescriptionDetail> createPrescriptionDetail(@RequestBody PrescriptionDetail prescriptionDetail) {
        try {
            PrescriptionDetail createdDetail = prescriptionDetailService.createPrescriptionDetail(prescriptionDetail);
            return ResponseEntity.ok(createdDetail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Pour des erreurs spécifiques
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // Récupérer tous les détails de prescription
    @GetMapping(path = "/prescriptionDetail/afficherTous")
    public ResponseEntity<List<PrescriptionDetail>> getAllPrescriptionDetails() {
        List<PrescriptionDetail> details = prescriptionDetailService.getAllPrescriptionDetails();
        return ResponseEntity.ok(details);
    }



    // Mettre à jour un détail de prescription
    @PutMapping("/prescriptionDetail/modifier/{id}")
    public ResponseEntity<PrescriptionDetail> updatePrescriptionDetail(@PathVariable Integer id, @RequestBody PrescriptionDetail updatedDetail) {
        try {
            PrescriptionDetail detail = prescriptionDetailService.updatePrescriptionDetail(id, updatedDetail);
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un détail de prescription
    @DeleteMapping("/prescriptionDetail/supprimer/{id}")
    public ResponseEntity<Void> deletePrescriptionDetail(@PathVariable Integer id) {
        prescriptionDetailService.deletePrescriptionDetail(id);
        return ResponseEntity.ok().build();
    }




    // Endpoint pour créer une nouvelle analyse
    @PostMapping("/analyses/ajouter")
    public ResponseEntity<Analyse> createAnalyse(@RequestBody Analyse analyse) throws Exception {
        Analyse createdAnalyse = analyseService.createAnalyse(analyse);
        return ResponseEntity.ok(createdAnalyse);
    }

    // Endpoint pour récupérer toutes les analyses
    @GetMapping("/analyses/afficherTous")
    public ResponseEntity<List<Analyse>> getAllAnalyses() {
        List<Analyse> analyses = analyseService.getAllAnalyses();
        return ResponseEntity.ok(analyses);
    }

    // Endpoint pour récupérer une analyse par ID
    @GetMapping("/analyses/afficher/{id}")
    public ResponseEntity<Analyse> getAnalyseById(@PathVariable int id) {
        Optional<Analyse> analyse = analyseService.getAnalyseById(id);
        return analyse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Endpoint pour mettre à jour une analyse
    @PutMapping("/analyses/modifier/{id}")
    public ResponseEntity<Analyse> updateAnalyse(@PathVariable int id, @RequestBody Analyse updatedAnalyse) {
        try {
            Analyse updated = analyseService.updateAnalyse(id, updatedAnalyse);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // Endpoint pour supprimer une analyse par ID
    @DeleteMapping("/analyses/supprimer/{id}")
    public ResponseEntity<Void> deleteAnalyse(@PathVariable int id) {
        analyseService.deleteAnalyse(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint pour emplois du temps

    @PostMapping("/EmploisDuTemps/ajouter")
    public EmploiDuTemps ajouterDisponibilite(@RequestBody EmploiDuTemps emploiDuTemps) throws Exception {

        if (emploiDuTemps.getJour().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Le jour ne peut pas être dans le passé");
        }

        if (!emploiDuTemps.isValidDisponibility()) {
            throw new IllegalArgumentException("L'heure de début doit être avant l'heure de fin");
        }

        return emploiDuTempsService.ajouterDisponibilite(emploiDuTemps);
    }

    @PutMapping("/EmploisDuTemps/modifier/{id}")
    public EmploiDuTemps modifierDisponibilite(@PathVariable Long id, @RequestBody EmploiDuTemps emploiDuTempsDetails) {
        return emploiDuTempsService.modifierDisponibilite(id, emploiDuTempsDetails);
    }

    @DeleteMapping("/EmploisDuTemps/supprimer/{id}")
    public void supprimerDisponibilite(@PathVariable Long id) {
        emploiDuTempsService.supprimerDisponibilite(id);
    }

    @GetMapping("/EmploisDuTemps/afficher")
    public List<EmploiDuTemps> afficherDisponibilites() {
        return emploiDuTempsService.afficherDisponibilites();
    }

    @GetMapping("/EmploisDuTemps/afficher/jour")
    public List<EmploiDuTemps> afficherDisponibilitesParJour(@RequestParam LocalDate jour) {
        return emploiDuTempsService.afficherDisponibilitesParJour(jour);
    }

    @GetMapping("/EmploisDuTemps/mes-disponibilites")
    public List<EmploiDuTemps> getMesDisponibilites() throws Exception {
        return emploiDuTempsService.afficherDisponibilitesParCreateur();
    }

    // Reservez une heures de disponibilite
    @PostMapping("/EmploisDuTemps/reserver/{id}")
    public EmploiDuTemps reserverCreneau(@PathVariable Long id, @RequestParam String rendezVous) {
        return emploiDuTempsService.reserverCreneau(id, rendezVous);
    }
    // LIberer une heures de disponibilite
    @PostMapping("/EmploisDuTemps/liberer/{id}")
    public EmploiDuTemps libererCreneau(@PathVariable Long id) {
        return emploiDuTempsService.libererCreneau(id);
    }



    @DeleteMapping("/rendezvous/annuler/{id}")
    public ResponseEntity<Void> annulerRendezVous(@PathVariable Long id) {
        try {
            rendezVousService.annulerRendezVous(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/rendezvous/valider/{id}")
    public ResponseEntity<RendezVous> validerRendezVous(@PathVariable Long id) {
        try {
            RendezVous rendezVous = rendezVousService.validerRendezVous(id);
            return ResponseEntity.ok(rendezVous);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(path = "/rendezvous/afficherTous")
    public ResponseEntity<List<RendezVous>> obtenirTousRendezVous() {
        List<RendezVous> rendezVousList = rendezVousService.obtenirTousRendezVous();
        return ResponseEntity.ok(rendezVousList);
    }

    @GetMapping("/rendezvous/emploiDuTemps/{emploiDuTempsId}")
    public ResponseEntity<List<RendezVous>> obtenirRendezVousParEmploiDuTemps(@PathVariable Long emploiDuTempsId) {
        try {
            List<RendezVous> rendezVous = rendezVousService.obtenirRendezVousParEmploiDuTemps(emploiDuTempsId);
            return ResponseEntity.ok(rendezVous);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null); // ou un autre code d'erreur approprié
        }
    }


    // Endpoint pour ajouter Un Patient

    @PostMapping(path = "/patients/ajouter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<?> AjouterPatient(
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("username") String username,
            @RequestParam("motDePasse") String motDePasse,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("sexe") String sexe,
            @RequestParam("adresse") String adresse,
            @RequestParam("age") int age,
            @RequestParam("poids") String poids,
            @RequestParam("photos") MultipartFile photos,
            @RequestParam("ethenie") String ethenie) {

        // Vérifiez si l'email ou le nom d'utilisateur existe déjà
        if (utilisateurRepository.existsByEmail(email) || utilisateurRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email ou nom d'utilisateur déjà utilisé.");
        }
        try {
            // Créer un nouvel utilisateur
            Patient nouvelUtilisateur = new Patient();
            nouvelUtilisateur.setNom(nom);
            nouvelUtilisateur.setPrenom(prenom);
            nouvelUtilisateur.setUsername(username);
            nouvelUtilisateur.setEmail(email);
            nouvelUtilisateur.setPhone(phone);
            nouvelUtilisateur.setSexe(sexe);
            nouvelUtilisateur.setAdresse(adresse);
            nouvelUtilisateur.setAge(age);
            nouvelUtilisateur.setPoids(poids);
            // Gérer le mot de passe
            if (motDePasse == null || motDePasse.isEmpty()) {
                return ResponseEntity.badRequest().body("Le mot de passe ne peut pas être vide.");
            }
            nouvelUtilisateur.setPassword(passwordEncoder.encode(motDePasse));

            // Gestion de l'image
            if (photos != null && !photos.isEmpty()) {
                FileInfo photoFileInfo = fileInfoService.creerFileInfo(photos);
                nouvelUtilisateur.setPhotos(photoFileInfo);
            }

            nouvelUtilisateur.setEthenie(ethenie);
            nouvelUtilisateur.setStatus(StatusCompte.ACTIVE);
            // Assigner le rôle "PATIENT"
            RoleType patientRole = roleRepository.findByNom("PATIENT")
                    .orElseGet(() -> roleRepository.save(new RoleType("PATIENT")));
            if (patientRole == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : le rôle 'PATIENT' n'a pas pu être assigné.");
            }
            nouvelUtilisateur.setRoleType(patientRole);

            // Sauvegarder l'utilisateur
            utilisateurRepository.save(nouvelUtilisateur);

            // Envoyer un email de confirmation
            adminService.envoyerEmailConfirmation(nouvelUtilisateur, motDePasse);

            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de l'utilisateur : " + e.getMessage());
        }
    }


    @PutMapping(path = "/patients/modifier/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modifierPatient(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("username") String username,
            @RequestParam(value = "motDePasse", required = false) String motDePasse,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("sexe") String sexe,
            @RequestParam("adresse") String adresse,
            @RequestParam("age") int age,
            @RequestParam("poids") String poids,
            @RequestParam(value = "photos", required = false) MultipartFile photos,
            @RequestParam("ethenie") String ethenie) {

        // Créer un objet Patient pour mettre à jour
        Patient patientModifie = new Patient();
        patientModifie.setNom(nom);
        patientModifie.setPrenom(prenom);
        patientModifie.setUsername(username);
        patientModifie.setEmail(email);
        patientModifie.setPhone(phone);
        patientModifie.setSexe(sexe);
        patientModifie.setAdresse(adresse);
        patientModifie.setAge(age);
        patientModifie.setPoids(poids);
        patientModifie.setEthenie(ethenie);

        // Mettre à jour le mot de passe si fourni
        if (motDePasse != null && !motDePasse.isEmpty()) {
            patientModifie.setPassword(motDePasse);
        }

        // Gérer l'image si fournie
        if (photos != null && !photos.isEmpty()) {
            FileInfo photoFileInfo = fileInfoService.creerFileInfo(photos);
            patientModifie.setPhotos(photoFileInfo);
        }

        try {
            Patient patientResultat = adminService.modifierPatient(id, patientModifie);
            return ResponseEntity.ok(patientResultat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification du patient : " + e.getMessage());
        }
    }



    @DeleteMapping("/patients/supprimer/{id}")
    public String supprimerPatient(@PathVariable("id") Long id) {
        return adminService.supprimerPatient(id); // Appelle le service pour supprimer le patient
    }





}
