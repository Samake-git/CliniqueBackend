package Gestion.Clinique.Samake.Service;

import Gestion.Clinique.Samake.Model.Admin;
import Gestion.Clinique.Samake.Model.EmploiDuTemps;
import Gestion.Clinique.Samake.Model.Medecin;
import Gestion.Clinique.Samake.Model.Utilisateur;
import Gestion.Clinique.Samake.Repository.EmploiDuTempsRepository;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmploiDuTempsService {

    @Autowired
    private EmploiDuTempsRepository emploiDuTempsRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;



    // Méthode pour récupérer l'utilisateur connecté
    private Utilisateur getUtilisateurConnecte() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé"));
    }

    public EmploiDuTemps ajouterDisponibilite(EmploiDuTemps emploiDuTemps) throws Exception  {

        Utilisateur utilisateur = getUtilisateurConnecte(); // Utiliser la méthode déjà décrite

            // Associer l'utilisateur connecté comme créateur de l'emploi du temps
            emploiDuTemps.setCreateur(utilisateur);

            // Définir le status automatiquement à "Disponible"
            emploiDuTemps.setStatus("Disponible");

            // Validation de la date et de la disponibilité
            if (emploiDuTemps.getJour().isBefore(LocalDate.now())) {
                throw new Exception("Le jour du rendez-vous ne peut pas être dans le passé");
            }

            if (!emploiDuTemps.isValidDisponibility()) {
                throw new Exception("L'heure de début doit être avant l'heure de fin");
            }

            // Sauvegarder l'emploi du temps avec l'utilisateur comme créateur
            return emploiDuTempsRepository.save(emploiDuTemps);
        }

        // Afficher emplois du temps par createur
    public List<EmploiDuTemps> afficherDisponibilitesParCreateur() throws Exception {
        Utilisateur utilisateur = getUtilisateurConnecte(); // Récupère l'utilisateur connecté
        return emploiDuTempsRepository.findByCreateur(utilisateur); // Filtre par créateur
    }

    // Modifier disponibilite
    public EmploiDuTemps modifierDisponibilite(Long id, EmploiDuTemps emploiDuTempsDetails) {
        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(id).orElseThrow();
        emploiDuTemps.setJour(emploiDuTempsDetails.getJour());
        emploiDuTemps.setHeureDebut(emploiDuTempsDetails.getHeureDebut());
        emploiDuTemps.setHeureFin(emploiDuTempsDetails.getHeureFin());
        emploiDuTemps.setStatus(emploiDuTempsDetails.getStatus());
        return emploiDuTempsRepository.save(emploiDuTemps);
    }

    public void supprimerDisponibilite(Long id) {
        emploiDuTempsRepository.deleteById(id);
    }

    public List<EmploiDuTemps> afficherDisponibilites() {
        return emploiDuTempsRepository.findAll();
    }

    public List<EmploiDuTemps> afficherDisponibilitesParJour(LocalDate jour) {
        return emploiDuTempsRepository.findByJour(jour);
    }

    public EmploiDuTemps reserverCreneau(Long id, String rendezVous) {
        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(id).orElseThrow();
        emploiDuTemps.setStatus("Reserved for: " + rendezVous);
        return emploiDuTempsRepository.save(emploiDuTemps);
    }

    public EmploiDuTemps libererCreneau(Long id) {
        EmploiDuTemps emploiDuTemps = emploiDuTempsRepository.findById(id).orElseThrow();
        emploiDuTemps.setStatus("Disponible maintenant");
        return emploiDuTempsRepository.save(emploiDuTemps);
    }

    // Méthode pour récupérer les emplois du temps par statut
    public List<EmploiDuTemps> afficherDisponibilitesParStatut(String statut) {
        return emploiDuTempsRepository.findByStatus(statut);
    }

    // Méthode pour récupérer les emplois du temps par spécialité
    public List<EmploiDuTemps> afficherDisponibilitesParSpecialite(String specialite) throws Exception {
        return emploiDuTempsRepository.findByCreateurSpecialite(specialite);
    }

    // Méthode pour récupérer toutes les disponibilités d'emploi du temps
    public List<EmploiDuTemps> afficherToutesDisponibilites() {
        return emploiDuTempsRepository.findAll(); // Assurez-vous que cette méthode existe dans votre repository
    }

    public List<EmploiDuTemps> afficherDisponibilites (String statut, String specialite, LocalDate jour) {
        // Si le statut est fourni, récupérer par statut
        List<EmploiDuTemps> result = statut != null ? afficherDisponibilitesParStatut(statut) : afficherToutesDisponibilites();

        // Si la spécialité est fournie, filtrer par spécialité
        if (specialite != null) {
            result = result.stream()
                    .filter(emploi -> emploi.getCreateur().getSpecialite().equals(specialite))
                    .collect(Collectors.toList());
        }

        // Si le jour est fourni, filtrer par jour
        if (jour != null) {
            result = result.stream()
                    .filter(emploi -> emploi.getJour().equals(jour))
                    .collect(Collectors.toList());
        }

        return result;
    }


}
