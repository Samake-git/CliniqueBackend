package Gestion.Clinique.Samake.Service;

import Gestion.Clinique.Samake.Model.EmploiDuTemps;
import Gestion.Clinique.Samake.Model.Patient;
import Gestion.Clinique.Samake.Model.RendezVous;
import Gestion.Clinique.Samake.Model.Utilisateur;
import Gestion.Clinique.Samake.Repository.EmploiDuTempsRepository;
import Gestion.Clinique.Samake.Repository.PatientRepository;
import Gestion.Clinique.Samake.Repository.RendezVousRepository;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;


@Service
public class RendezVousService {

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private EmploiDuTempsRepository emploiDuTempsRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UtilisateurRepository utilisateurRepository;


    // Créer une demande de rendez-vous par le patient connecté

    public RendezVous demanderRendezVous(Long emploiDuTempsId, String description, LocalTime heureDebut, LocalTime heureFin) throws Exception {
        // Récupérer l'utilisateur connecté à partir du contexte de sécurité
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Vérifier que l'utilisateur est un patient
        Utilisateur patient = utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("Patient non trouvé"));

        // Récupérer l'emploi du temps
        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(emploiDuTempsId)
                .orElseThrow(() -> new Exception("Emploi du temps non trouvé"));

        // Vérifiez les disponibilités
        verifierDisponibilite(emploiDuTemps, heureDebut, heureFin);

        // Convertir LocalTime en Time pour la base de données
        Time heureDebutDb = Time.valueOf(heureDebut);
        Time heureFinDb = Time.valueOf(heureFin);

        // Logique pour créer le rendez-vous
        RendezVous rendezVous = new RendezVous();
        rendezVous.setDescription(description);
        rendezVous.setHeureDebut(heureDebutDb);
        rendezVous.setHeureFin(heureFinDb);
        rendezVous.setJour(emploiDuTemps.getJour());
        rendezVous.setStatut("En attente");
        rendezVous.setEmploiDuTemps(emploiDuTemps);
        rendezVous.setPatient(patient);


        return rendezVousRepository.save(rendezVous);
    }

    public RendezVous modifierRendezVous(Long rendezVousId, String description, LocalTime heureDebut, LocalTime heureFin) throws Exception {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new Exception("Rendez-vous non trouvé"));

        // Vérification de la description reçue
        System.out.println("Description reçue : " + description);

        // Mettre à jour les informations du rendez-vous
        rendezVous.setDescription(description);
        rendezVous.setHeureDebut(Time.valueOf(heureDebut));
        rendezVous.setHeureFin(Time.valueOf(heureFin));

        return rendezVousRepository.save(rendezVous);
    }


    // Methode pour  Obtenir tous les rendez-vous
    public List<RendezVous> obtenirTousRendezVous() {
        return rendezVousRepository.findAll();
    }

// Methode pour Obtenir un rendez-vous par ID
    public RendezVous obtenirRendezVousParId(Long id) throws Exception {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new Exception("Rendez-vous non trouvé"));
    }

    // Methode pour Annuler un rendez-vous

    public void annulerRendezVous(Long rendezVousId) throws Exception {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new Exception("Rendez-vous non trouvé"));

        // Vérifier si le statut est "En attente"
        if (!"En attente".equals(rendezVous.getStatut())) {
            throw new Exception("Le rendez-vous ne peut pas être annulé car il a déjà été Valider.");
        }

        // Annuler le rendez-vous
        rendezVous.setStatut("Annuler");
        rendezVousRepository.save(rendezVous);
    }

    // Méthode pour qu'un médecin approuve un rendez-vous
    public RendezVous validerRendezVous(Long rendezVousId) throws Exception {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new Exception("Rendez-vous non trouvé"));
        if (!"En attente".equals(rendezVous.getStatut())) {
            throw new Exception("Le rendez-vous  a déjà été Valider");
        }

        rendezVous.setStatut("Valider");

        return rendezVousRepository.save(rendezVous);
    }


    public void supprimerRendezVous(Long rendezVousId) throws Exception {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new Exception("Rendez-vous non trouvé"));
        // Suppression du rendez-vous
        rendezVousRepository.deleteById(rendezVousId);
    }


    // Méthode de vérification de la disponibilité
    private void verifierDisponibilite(EmploiDuTemps emploiDuTemps, LocalTime heureDebut, LocalTime heureFin) throws Exception {
        // Convertir les heures de l'emploi du temps en LocalTime si elles sont stockées en Time
        LocalTime heureDebutEmploiDuTemps = emploiDuTemps.getHeureDebut();
        LocalTime heureFinEmploiDuTemps = emploiDuTemps.getHeureFin();

        // Vérifiez si les heures demandées sont en dehors des heures de disponibilité
        if (heureDebut.isBefore(heureDebutEmploiDuTemps) || heureFin.isAfter(heureFinEmploiDuTemps)) {
            throw new Exception("Les heures demandées ne sont pas disponibles selon l'emploi du temps.");
        }
    }


    public List<RendezVous> obtenirRendezVousParPatientConnecte() throws Exception {
        // Récupérer l'email de l'utilisateur connecté à partir du contexte de sécurité
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Récupérer l'entité du patient connecté
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Patient non trouvé"));

        // Récupérer et retourner les rendez-vous associés au patient connecté
        return rendezVousRepository.findByPatient(patient);
    }

// Récuperer les Rdv EN FONCTION DE L'EMPLOIS DU TEMPS
    public List<RendezVous> obtenirRendezVousParEmploiDuTemps(Long emploiDuTempsId) throws Exception {
        // Vérifiez si l'emploi du temps existe
        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(emploiDuTempsId)
                .orElseThrow(() -> new Exception("Emploi du temps non trouvé"));

        // Récupérer les rendez-vous associés à cet emploi du temps
        return rendezVousRepository.findByEmploiDuTemps(emploiDuTemps);
    }



    // Envoi d'un email de confirmation après l'approbation du rendez-vous
    private void envoyerEmailConfirmation(RendezVous rendezVous) {
        SimpleMailMessage message = new SimpleMailMessage();

        // Envoi à l'email du patient
        message.setTo(rendezVous.getPatient().getEmail());
        message.setSubject("Confirmation de votre rendez-vous");
        message.setText("Votre demande de rendez-vous a été approuvée. Rendez-vous prévu pour le " +
                rendezVous.getJour() + " à " + rendezVous.getHeureDebut() + ".");

        mailSender.send(message);

        // Envoi à l'email du médecin
        SimpleMailMessage messageMedecin = new SimpleMailMessage();
        messageMedecin.setTo(rendezVous.getEmploiDuTemps().getCreateur().getEmail());
        messageMedecin.setSubject("Nouveau rendez-vous approuvé");
        messageMedecin.setText("Vous avez approuvé un rendez-vous avec le patient " +
                rendezVous.getPatient().getNom() + " pour le " + rendezVous.getJour() +
                " à " + rendezVous.getHeureDebut() + ".");

        mailSender.send(messageMedecin);
    }

    // Rappel 30 minutes avant le rendez-vous pour le médecin et le patient
    @Scheduled(fixedRate = 60000)  // Vérification toutes les minutes
    public void envoyerRappelRendezVous() {
        LocalTime now = LocalTime.now();
        LocalTime next30Minutes = now.plusMinutes(30);
        List<RendezVous> rendezVousList = rendezVousRepository.findRendezVousInNext30Minutes(now, next30Minutes);

        for (RendezVous rendezVous : rendezVousList) {
            SimpleMailMessage message = new SimpleMailMessage();

            // Envoi du rappel au patient
            message.setTo(rendezVous.getPatient().getEmail());
            message.setSubject("Rappel de votre rendez-vous");
            message.setText("Rappel : votre rendez-vous avec le Dr. " +
                    rendezVous.getEmploiDuTemps().getCreateur().getNom() + " est prévu dans 30 minutes.");

            mailSender.send(message);

            // Envoi du rappel au médecin
            SimpleMailMessage messageMedecin = new SimpleMailMessage();
            messageMedecin.setTo(rendezVous.getEmploiDuTemps().getCreateur().getEmail());
            messageMedecin.setSubject("Rappel de rendez-vous");
            messageMedecin.setText("Rappel : Vous avez un rendez-vous avec le patient " +
                    rendezVous.getPatient().getNom() + " dans 30 minutes.");

            mailSender.send(messageMedecin);
        }
    }
}
