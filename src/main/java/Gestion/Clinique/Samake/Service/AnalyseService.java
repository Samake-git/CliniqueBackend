package Gestion.Clinique.Samake.Service;


import Gestion.Clinique.Samake.Model.*;
import Gestion.Clinique.Samake.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyseService {

    @Autowired
    private AnalyseRepository analyseRepository;

    @Autowired
    private PaiementRepository paiementRepository;


    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CategorieAnalyseRepository categorieAnalyseRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Utilisateur getUtilisateurConnecte() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé"));
    }

    // Créer une nouvelle analyse
    public Analyse createAnalyse(Analyse analyse) throws Exception {
        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = getUtilisateurConnecte();

        // Vérifier si le patient existe
        Optional<Patient> patientOpt = patientRepository.findById(analyse.getPatient().getId());
        if (!patientOpt.isPresent()) {
            throw new IllegalArgumentException("Patient introuvable avec l'ID: " + analyse.getPatient().getId());
        }

        // Vérifier si la catégorie d'analyse existe (facultatif, si la catégorie est définie)
        if (analyse.getCategorieAnalyse() != null) {
            Optional<CategorieAnalyse> categorieOpt = categorieAnalyseRepository.findById(analyse.getCategorieAnalyse().getId());
            if (!categorieOpt.isPresent()) {
                throw new IllegalArgumentException("Catégorie d'analyse introuvable avec l'ID: " + analyse.getCategorieAnalyse().getId());
            }
        }

        analyse.setEtat("En Attente");
        analyse.setUser(utilisateur);
        analyse.setDateAnalyse(new Date());
        analyse.setCategorieAnalyse(analyse.getCategorieAnalyse());

        // Sauvegarder l'analyse après les vérifications
        return analyseRepository.save(analyse);

    }

    // Récupérer toutes les analyses
    public List<Analyse> getAllAnalyses() {
        return analyseRepository.findAll();
    }

    // Récupérer une analyse par ID
    public Optional<Analyse> getAnalyseById(int id) {
        return analyseRepository.findById(id);
    }

    // Mettre à jour une analyse existante
    public Analyse updateAnalyse(int id, Analyse updatedAnalyse) {
        Optional<Analyse> existingAnalyse = analyseRepository.findById(id);
        if (existingAnalyse.isPresent()) {
            Analyse analyse = existingAnalyse.get();
            analyse.setAppareilUtilise(updatedAnalyse.getAppareilUtilise());
            analyse.setDateAnalyse(updatedAnalyse.getDateAnalyse());
            analyse.setTypeAnalyse(updatedAnalyse.getTypeAnalyse());
            analyse.setPatient(updatedAnalyse.getPatient());
            analyse.setUser(updatedAnalyse.getUser());
            analyse.setCategorieAnalyse(updatedAnalyse.getCategorieAnalyse());
            return analyseRepository.save(analyse);
        } else {
            throw new RuntimeException("Analyse introuvable avec ID: " + id);
        }
    }

    // Supprimer une analyse par ID
    public void deleteAnalyse(int id) {
        analyseRepository.deleteById(id);
    }

    public long getAnalysesEnAttente() {
        return analyseRepository.countByEtat("En Attente");
    }

    public long getAnalysesTraitees() {
        return analyseRepository.countByEtat("Traiter");
    }

    public long getTotalAnalyses() {
        return analyseRepository.count();
    }


    // Méthode pour payer une analyse
    // Méthode pour payer une analyse
    public String payerAnalyse(Long analyseId) throws Exception {
        Utilisateur utilisateur = getUtilisateurConnecte();
        Optional<Analyse> analyseOptional = analyseRepository.findById(analyseId);

        if (analyseOptional.isPresent()) {
            Analyse analyse = analyseOptional.get();

            if (!analyse.isEstPaye()) {
                // Récupérer le prix de l'analyse à partir de TypeAnalyse
                double prixAnalyse = analyse.getTypeAnalyse().getPrix();

                // Créer et enregistrer le paiement
                Paiement paiement = new Paiement();
                paiement.setAnalyse(analyse);
                paiement.setUtilisateur(utilisateur); // Utilisateur connecté
                paiement.setMontant((float) prixAnalyse);
                paiement.setDatePaiement(new Date());
                paiement.setEstPaye(true);

                paiementRepository.save(paiement); // Enregistrement du paiement
                analyse.setEstPaye(true);
                analyseRepository.save(analyse); // Mise à jour de l'analyse

                return "Le paiement de l'analyse a été effectué avec succès. Prix : " + prixAnalyse;
            } else {
                return "Cette analyse a déjà été payée.";
            }
        } else {
            return "Analyse introuvable.";
        }
    }


    // Statistiques sur les analyses
    public long countAnalysesByPatient(Patient patient) {
        return analyseRepository.countByPatient(patient);
    }

    // Récupérer les analyses par patient
    public List<Analyse> getAnalysesByConnectedPatient() throws Exception {
        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = getUtilisateurConnecte();

        // Vérifier si l'utilisateur est un patient
        if (!(utilisateur instanceof Patient)) {
            throw new Exception("L'utilisateur connecté n'est pas un patient.");
        }

        // Récupérer le patient
        Patient patient = (Patient) utilisateur;

        // Récupérer les analyses associées au patient
        return analyseRepository.findByPatient(patient);
    }


}
