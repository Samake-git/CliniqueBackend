package Gestion.Clinique.Samake.Controlleur;

import Gestion.Clinique.Samake.DTO.ResponseDTO;
import Gestion.Clinique.Samake.Model.*;
import Gestion.Clinique.Samake.Repository.DepartementRepository;
import Gestion.Clinique.Samake.Repository.RoleRepository;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import Gestion.Clinique.Samake.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/admin")

public class AdminControlleur {


    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private DepartementService departementService;

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
    private AdminService adminService;




    // Endpoint pour obtenir le total des utilisateurs ayant le rôle PATIENT
    @GetMapping("/utilisateurs/patients/count")
    public long getPatientsCount() {
        return adminService.countPatients();
    }


    // Endpoint pour modifier mot de passe

    @PutMapping("/modifierMotDePasse")
    public ResponseEntity<String> modifierMotDePasse(@RequestBody Map<String, String> requestBody) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String nouveauMotDePasse = requestBody.get("nouveauMotDePasse");
            if (nouveauMotDePasse == null || nouveauMotDePasse.isEmpty()) {
                return ResponseEntity.badRequest().body("Le nouveau mot de passe ne peut pas être vide.");
            }
            String message = adminService.modifiermotDePasse(username, nouveauMotDePasse);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Endpoint pour le Departement

    @GetMapping(path = "/departement/Tous_Afficher")
    public List<Departement> getAllDepartements() {
        return departementService.getAllDepartements();
    }

    @GetMapping("/departement/{id}")
    public Departement getDepartementById(@PathVariable Long id) {
        return departementService.getDepartementById(id);
    }

    @PostMapping(path = "/departement/creer")
    public Departement createDepartement(@RequestBody Departement departement) {
        return departementService.saveDepartement(departement);
    }

    @PutMapping("/departement/modifier/{id}")
    public ResponseEntity<Departement> modifierDepartement(@PathVariable Long id,
                                                           @RequestBody Departement departementModifie) {
        try {
            // Vérifier si le département existe
            Departement existingDepartement = departementService.getDepartementById(id);
            if (existingDepartement == null) {
                return ResponseEntity.notFound().build();
            }

            // Mettre à jour le département
            existingDepartement.setNom(departementModifie.getNom());
            existingDepartement.setDescription(departementModifie.getDescription());

            // Sauvegarder les modifications
            Departement updatedDepartement = departementService.saveDepartement(existingDepartement);

            return ResponseEntity.ok(updatedDepartement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/departement/supprimer/{id}")
    public ResponseEntity<String> supprimerDepartement(@PathVariable Long id) {
        try {
            departementService.deleteDepartement(id);
            return ResponseEntity.ok("Le département a été supprimé avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // Endpoint pour le Role

    @PostMapping("/roles/creer")
    public ResponseEntity<RoleType> ajouterRoleType(@RequestBody RoleType roleType) {
        Optional<RoleType> existingRole = roleRepository.findByNom(roleType.getNom());
        if (existingRole.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(existingRole.get());
        } else {
            RoleType savedRole = roleRepository.save(roleType);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
        }
    }

    @PutMapping("/roles/modifier/{id}")
    public ResponseEntity<ResponseDTO> modifierRole(@PathVariable Long id, @RequestBody RoleType roleTypeDetails) {
        try {
            String message = adminService.modifierRoleType(id, roleTypeDetails);
            ResponseDTO responseDTO = new ResponseDTO(message);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            ResponseDTO responseDTO = new ResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @DeleteMapping("/roles/supprimer/{id}")
    public ResponseEntity<ResponseDTO> supprimerRole(@PathVariable Long id) {
        try {
            String message = adminService.supprimerRoleType(id);
            ResponseDTO responseDTO = new ResponseDTO(message);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            ResponseDTO responseDTO = new ResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @GetMapping("/roles/afficher")
    public ResponseEntity<List<RoleType>> lireRoles() {
        List<RoleType> roles = adminService.lireRoleTypes();
        return ResponseEntity.ok(roles);
    }



    // Endpoint pour ajouter Un Utilisateur

    @GetMapping("/utilisateurs/AfficherTous")
    public ResponseEntity<List<Utilisateur>> listUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        return new ResponseEntity<>(utilisateurs, HttpStatus.OK);
    }


    @PostMapping(path = "/utilisateurs/ajouter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> ajouterUtilisateurs(
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("username") String username,
            @RequestParam("motDePasse") String motDePasse,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("sexe") String sexe,
            @RequestParam("adresse") String adresse,
            @RequestParam("specialite") String specialite,
            @RequestParam("roletypeId") RoleType roleType,
            @RequestParam("photos") MultipartFile photos,
            @RequestParam("departementId") Long departementId) {

        // Vérifiez si l'email ou le nom d'utilisateur existe déjà
        if (utilisateurRepository.existsByEmail(email) || utilisateurRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email ou nom d'utilisateur déjà utilisé.");
        }
        try {
            // Créer un nouvel utilisateur
            Utilisateur nouvelUtilisateur = new Utilisateur();
            nouvelUtilisateur.setNom(nom);
            nouvelUtilisateur.setPrenom(prenom);
            nouvelUtilisateur.setUsername(username);
            nouvelUtilisateur.setEmail(email);
            nouvelUtilisateur.setPhone(phone);
            nouvelUtilisateur.setSexe(sexe);
            nouvelUtilisateur.setAdresse(adresse);
            nouvelUtilisateur.setSpecialite(specialite);
            nouvelUtilisateur.setRoleType(roleType);

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

            // Assigner le département
            Departement departement = departementRepository.findById(departementId)
                    .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID : " + departementId));
            nouvelUtilisateur.setDepartement(departement);

            // Sauvegarder l'utilisateur
            utilisateurRepository.save(nouvelUtilisateur);

            // Envoyer un email de confirmation
            adminService.envoyerEmailConfirmation(nouvelUtilisateur, motDePasse);

            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de l'utilisateur : " + e.getMessage());
        }
    }


    // Endpoint pour modifier un admin
    @PutMapping(path = "/utilisateurs/modifier/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modifierUtilisateur(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("username") String username,
            @RequestParam("motDePasse") String motDePasse,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("sexe") String sexe,
            @RequestParam("adresse") String adresse,
            @RequestParam("specialite") String specialite,
            @RequestParam("roletypeId") RoleType roleType,
            @RequestParam(value = "photos", required = false) MultipartFile photos,
            @RequestParam("departementId") Long departementId) {

        // Vérifiez si l'utilisateur existe
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        // Vérifiez si l'email ou le nom d'utilisateur existe déjà (exclure l'utilisateur actuel)
        if (utilisateurRepository.existsByEmail(email) && !utilisateur.getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email déjà utilisé par un autre utilisateur.");
        }
        if (utilisateurRepository.existsByUsername(username) && !utilisateur.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nom d'utilisateur déjà utilisé par un autre utilisateur.");
        }

        try {
            // Mettre à jour les propriétés de l'utilisateur
            utilisateur.setNom(nom);
            utilisateur.setPrenom(prenom);
            utilisateur.setUsername(username);
            utilisateur.setEmail(email);
            utilisateur.setPhone(phone);
            utilisateur.setSexe(sexe);
            utilisateur.setAdresse(adresse);
            utilisateur.setSpecialite(specialite);
            utilisateur.setRoleType(roleType);

            // Gestion du mot de passe
            if (motDePasse != null && !motDePasse.isEmpty()) {
                utilisateur.setPassword(passwordEncoder.encode(motDePasse));
            }

            // Gestion de l'image
            if (photos != null && !photos.isEmpty()) {
                FileInfo photoFileInfo = fileInfoService.creerFileInfo(photos);
                utilisateur.setPhotos(photoFileInfo);
            }

            // Assigner le département
            Departement departement = departementRepository.findById(departementId)
                    .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID : " + departementId));
            utilisateur.setDepartement(departement);

            // Sauvegarder les modifications
            utilisateurRepository.save(utilisateur);

            return ResponseEntity.ok(utilisateur);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification de l'utilisateur : " + e.getMessage());
        }
    }
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

    // Endpoint pour supprimer un admin
    @DeleteMapping("/utilisateurs/supprimer/{id}")
    public ResponseEntity<String> supprimerAdmin(@PathVariable Long id) {
        String message = adminService.supprimerUtilisateur(id);
        return ResponseEntity.ok(message);
    }



    // Endpoint pour ajouter Un Patient

    @PostMapping(path = "/ajouter/Patient", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

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


    @PutMapping(path = "/modifier/Patient/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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






}








