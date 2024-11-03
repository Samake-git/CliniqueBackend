package Gestion.Clinique.Samake.Service;

import Gestion.Clinique.Samake.Model.*;
import Gestion.Clinique.Samake.Repository.PatientRepository;
import Gestion.Clinique.Samake.Repository.RoleRepository;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    private  RoleRepository roleRepository;
    @Autowired
    private  PatientRepository patientRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public AdminService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public String ajouterRoleType(RoleType roleType) {
        Optional<RoleType> existingRole = roleRepository.findByNom(roleType.getNom());
        if (existingRole.isPresent()) {
            return "Le rôle " + roleType.getNom() + " existe déjà.";
        } else {
            roleRepository.save(roleType);
            return "Rôle ajouté avec succès!";
        }
    }

    public List<Patient> listerTousLesPatients() {
        return patientRepository.findAll(); // Récupère tous les patients
    }

    @Transactional
    public String supprimerPatient(Long patientId) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(patientId);

        if (utilisateurOptional.isPresent() && utilisateurOptional.get() instanceof Patient) {
            utilisateurRepository.deleteById(patientId);
            return "Patient supprimé avec succès!";
        } else {
            return "Aucun patient trouvé avec l'ID fourni.";
        }
    }

    // Nouvelle méthode pour vérifier si un rôle existe
    public boolean existeRole(String nom) {
        return roleRepository.findByNom(nom).isPresent();
    }


    public String modifierRoleType(Long id, RoleType roleTypeDetails) {
        return roleRepository.findById(id)
                .map(roleType -> {
                    roleType.setNom(roleTypeDetails.getNom());
                    roleRepository.save(roleType);
                    return "Role modifié avec succès!";
                }).orElseThrow(() -> new RuntimeException("Role n'existe pas"));
    }

    public String supprimerRoleType(Long id) {
        Optional<RoleType> roleTypeOptional = roleRepository.findById(id);

        if (roleTypeOptional.isPresent()) {
            roleRepository.deleteById(id);
            return "Role supprimé avec succès!";
        } else {
            return "Aucun role trouvé avec l'id fourni.";
        }
    }

    public long getTotalPatients() {
        return patientRepository.count();
    }

    public List<RoleType> lireRoleTypes() {
        return roleRepository.findAll();  // Renvoie tous les rôles disponibles
    }

    public List<Utilisateur> listUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // Méthode pour obtenir le total des utilisateurs ayant le rôle PATIENT
    public long countPatients() {
        return utilisateurRepository.countByRole("PATIENT");
    }

    public List<Utilisateur> listUtilisateursPatient() {
        Optional<RoleType> patientRole = roleRepository.findByNom("PATIENT");
        return utilisateurRepository.findAllByRoleType(patientRole);
    }


    @Transactional
    public String supprimerUtilisateur(Long id) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(id);

        if (utilisateurOptional.isPresent()) {
            utilisateurRepository.deleteById(id);
            return "Utilisateur supprimé avec succès!";
        } else {
            return "Aucun utilisateur trouvé avec l'id fourni.";
        }
    }

    public void desactiverComptePatient(Long PatientId) {
        Optional<Patient> patientOptional = patientRepository.findById(PatientId);
        patientOptional.ifPresent(patient -> {
            patient.setStatus(StatusCompte.DESACTIVE);
            patientRepository.save(patient);
        });
    }

    public Optional<Admin> findAdminByUsername(String username) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(username);
        if (utilisateur.isPresent() && utilisateur.get() instanceof Admin) {
            return Optional.of((Admin) utilisateur.get());
        }
        return Optional.empty();
    }

    @Transactional
    public Utilisateur ajouterUtilisateurs(Utilisateur utilisateur) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String adminUsername = authentication.getName();
        Utilisateur admin = utilisateurRepository.findByEmail(adminUsername)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé avec le nom d'utilisateur : " + adminUsername));

        // Vérifier que l'email et le mot de passe sont non nuls et non vides
        if ( utilisateur.getEmail() == null ||  utilisateur.getEmail().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide.");
        }
        if ( utilisateur.getPassword() == null ||  utilisateur.getPassword() .isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }

        String motDePasseClair = utilisateur.getPassword();
        utilisateur.setNom(utilisateur.getNom());
        utilisateur.setPrenom(utilisateur.getPrenom());
        utilisateur.setUsername(utilisateur.getUsername());
        utilisateur.setSexe(utilisateur.getSexe());
        utilisateur.setPhotos(utilisateur.getPhotos());
        utilisateur.setPhone(utilisateur.getPhone());
        utilisateur.setAdresse(utilisateur.getAdresse());
        utilisateur.setEmail(utilisateur.getEmail());
        utilisateur.setSpecialite(utilisateur.getSpecialite());
        utilisateur.setDepartement(utilisateur.getDepartement());
        utilisateur.setRoleType(utilisateur.getRoleType());

        utilisateur.setPassword(passwordEncoder.encode(motDePasseClair));
        utilisateurRepository.save(utilisateur);

        envoyerEmailConfirmation(utilisateur, motDePasseClair);
        return utilisateur;
    }

    @Transactional
    public Utilisateur modifierUtilisateur(Long id, Utilisateur utilisateurModifie) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        utilisateur.setNom(utilisateurModifie.getNom());
        utilisateur.setPrenom(utilisateurModifie.getPrenom());
        utilisateur.setUsername(utilisateurModifie.getUsername());
        utilisateur.setSexe(utilisateurModifie.getSexe());
        utilisateur.setPhotos(utilisateurModifie.getPhotos());
        utilisateur.setPhone(utilisateurModifie.getPhone());
        utilisateur.setAdresse(utilisateurModifie.getAdresse());
        utilisateur.setEmail(utilisateurModifie.getEmail());
        utilisateur.setSpecialite(utilisateurModifie.getSpecialite());
        utilisateur.setDepartement(utilisateurModifie.getDepartement());
        utilisateur.setRoleType(utilisateurModifie.getRoleType());

        return utilisateurRepository.save(utilisateur);
    }



    @Transactional
    public Patient AjouterPatient(Patient patient) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String adminUsername = authentication.getName();
        Utilisateur admin = utilisateurRepository.findByEmail(adminUsername)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé avec le nom d'utilisateur : " + adminUsername));

        // Vérifier que l'email et le mot de passe sont non nuls et non vides
        if (patient.getEmail() == null || patient.getEmail().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide.");
        }
        if (patient.getPassword() == null ||patient.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }

        try {
            RoleType patientRole = roleRepository.findByNom("PATIENT")
                    .orElseGet(() -> roleRepository.save(new RoleType("PATIENT")));

            // Vérification si le RoleType est null
            if (patientRole == null) {
                throw new IllegalStateException("Le rôle 'PATIENT' n'a pas pu être trouvé ou créé.");
            }

            // Assigner le rôle patient avant la sauvegarde
            patient.setRoleType(patientRole);
            String motDePasseClair = patient.getPassword();
            patient.setNom(patient.getNom());
            patient.setPrenom(patient.getPrenom());
            patient.setUsername(patient.getUsername());
            patient.setSexe(patient.getSexe());
            patient.setPhotos(patient.getPhotos());
            patient.setPhone(patient.getPhone());
            patient.setAdresse(patient.getAdresse());
            patient.setEmail(patient.getEmail());
            patient.setAge(patient.getAge());
            patient.setEthenie(patient.getEthenie());
            patient.setPoids(patient.getPoids());
            patient.setStatus(StatusCompte.ACTIVE);

            // Encoder le mot de passe
            patient.setPassword(passwordEncoder.encode(motDePasseClair));
            utilisateurRepository.save(patient);

            // Envoi de l'email de confirmation après la sauvegarde
            envoyerEmailConfirmation(patient, motDePasseClair);

            return patient;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ajout du patient: " + e.getMessage());
        }
    }

    @Transactional
    public Patient modifierPatient(Long patientId, Patient patientModifie) {
        // Vérification de l'authentification
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        // Vérifier si le patient existe

        // Vérifier si le patient existe
        Utilisateur utilisateurExistant = utilisateurRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient introuvable avec l'ID : " + patientId));

        // Vérifiez si l'utilisateur est un Patient
        if (!(utilisateurExistant instanceof Patient)) {
            throw new RuntimeException("L'utilisateur trouvé n'est pas un patient.");
        }

        Patient patientExistant = (Patient) utilisateurExistant;
        // Mettre à jour les informations du patient
        patientExistant.setNom(patientModifie.getNom());
        patientExistant.setPrenom(patientModifie.getPrenom());
        patientExistant.setUsername(patientModifie.getUsername());
        patientExistant.setEmail(patientModifie.getEmail());
        patientExistant.setPhone(patientModifie.getPhone());
        patientExistant.setSexe(patientModifie.getSexe());
        patientExistant.setAdresse(patientModifie.getAdresse());
        patientExistant.setAge(patientModifie.getAge());
        patientExistant.setPoids(patientModifie.getPoids());
        patientExistant.setEthenie(patientModifie.getEthenie());
        patientExistant.setPhotos(patientModifie.getPhotos());

        // Si un nouveau mot de passe est fourni, mettre à jour le mot de passe
        if (patientModifie.getPassword() != null && !patientModifie.getPassword().isEmpty()) {
            patientExistant.setPassword(passwordEncoder.encode(patientModifie.getPassword()));
        }

        // Sauvegarder les modifications
        return utilisateurRepository.save(patientExistant);

    }


    public void envoyerEmailConfirmation(Utilisateur utilisateur, String motDePasseClair) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utilisateur.getEmail());
        message.setSubject("Compte créé avec succès");
        message.setText("Bonjour, votre compte a été créé avec succès. " +
                "Veuillez modifier votre mot de passe pour des raisons de sécurité. Vos identifiants sont:\n" +
                "Username: " + utilisateur.getUsername() + "\nMot de passe: " + motDePasseClair);
        mailSender.send(message);
    }


    public String modifiermotDePasse(String username, String NouveaumotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + username));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getName().equals(username)) {
            throw new IllegalStateException("Vous n'êtes pas autorisé à modifier ce mot de passe");
        }

        utilisateur.setPassword(passwordEncoder.encode(NouveaumotDePasse));
        utilisateurRepository.save(utilisateur);

        return "Mot de passe mis à jour avec succès pour l'utilisateur: " + username;
    }


    public String modifierusername(Long id, String username) {
        return utilisateurRepository.findById(id)
                .map(utilisateur -> {
                    utilisateur.setUsername(username);
                    utilisateurRepository.save(utilisateur);
                    return "Username modifié avec succès!";
                }).orElseThrow(() -> new RuntimeException("Utilisateur n'existe pas"));
    }


    public String modifierAdmin(Long id, Admin adminDetails) {
        Admin admin = (Admin) utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec id : " + id));

        admin.setUsername(adminDetails.getUsername());
        admin.setPassword(passwordEncoder.encode(adminDetails.getPassword()));
        utilisateurRepository.save(admin);
        return "Admin modifié avec succès!";
    }



    @Transactional
    @PostConstruct
    public void initAdmin() {
        try {
            RoleType adminRole = roleRepository.findByNom("ADMIN")
                    .orElseGet(() -> roleRepository.save(new RoleType("ADMIN")));

            List<Utilisateur> admins = utilisateurRepository.findByRoleType(adminRole);

            if (admins.isEmpty()) {
                Admin admin = new Admin();
                admin.setPrenom("Bakary");
                admin.setNom("SAMAKE");
                admin.setUsername("samake");
                admin.setPassword(passwordEncoder.encode("samake"));
                admin.setRoleType(adminRole);
                admin.setEmail("email@example.com");
                Utilisateur savedAdmin = utilisateurRepository.save(admin);
                System.out.println("Admin créé avec succès. ID: " + savedAdmin.getId());
            } else {
                System.out.println("Un administrateur existe déjà.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de l'admin : " + e.getMessage());
        }
    }


}
