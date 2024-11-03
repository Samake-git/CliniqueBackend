package Gestion.Clinique.Samake.Service;

import Gestion.Clinique.Samake.Model.Analyse;
import Gestion.Clinique.Samake.Model.Patient;
import Gestion.Clinique.Samake.Model.ResultatExamen;
import Gestion.Clinique.Samake.Model.Utilisateur;
import Gestion.Clinique.Samake.Repository.AnalyseRepository;
import Gestion.Clinique.Samake.Repository.ResultatExamenRepository;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ResultatExamenService {

    @Autowired
    private ResultatExamenRepository resultatExamenRepository;
    @Autowired
    private AnalyseRepository analyseRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;


    private Utilisateur getUtilisateurConnecte() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé"));
    }

    // Créer un nouveau résultat d'examen
    public ResultatExamen createResultatExamen(ResultatExamen resultatExamen) throws Exception {
        // Vérifier si l'analyse existe
        Optional<Analyse> analyseOpt = analyseRepository.findById(resultatExamen.getAnalyse().getId());

        if (!analyseOpt.isPresent()) {
            // Lever une exception si l'analyse n'existe pas
            throw new IllegalArgumentException("Analyse introuvable avec l'ID: " + resultatExamen.getAnalyse().getId());
        }

        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = getUtilisateurConnecte();
        resultatExamen.setUser(utilisateur);
        resultatExamen.setDateExamen(new Date());
        analyseOpt.get().setEtat("Traiter");

        // Si l'analyse existe, sauvegarder le résultat de l'examen
        return resultatExamenRepository.save(resultatExamen);
    }


    // Récupérer tous les résultats d'examen
    public List<ResultatExamen> getAllResultats() {
        return resultatExamenRepository.findAll();
    }

    // Récupérer un résultat d'examen par ID
    public Optional<ResultatExamen> getResultatById(int id) {
        return resultatExamenRepository.findById(id);
    }

    // Mettre à jour un résultat d'examen existant
    public ResultatExamen updateResultatExamen(int id, ResultatExamen updatedResultat) {
        Optional<ResultatExamen> existingResultatOpt = resultatExamenRepository.findById(id);
        if (existingResultatOpt.isPresent()) {
            ResultatExamen existingResultat = existingResultatOpt.get();
            existingResultat.setDateExamen(updatedResultat.getDateExamen());
            existingResultat.setNomExamen(updatedResultat.getNomExamen());
            existingResultat.setResultat(updatedResultat.getResultat());
            existingResultat.setUnite(updatedResultat.getUnite());
            existingResultat.setNorme(updatedResultat.getNorme());
            existingResultat.setCommentaire(updatedResultat.getCommentaire());
            existingResultat.setAnalyse(updatedResultat.getAnalyse());
            return resultatExamenRepository.save(existingResultat);
        } else {
            throw new RuntimeException("Résultat d'examen introuvable avec ID: " + id);
        }
    }


    // Supprimer un résultat d'examen par ID
    public void deleteResultatExamen(int id) {
        resultatExamenRepository.deleteById(id);
    }

    // Récupérer les résultats d'examen pour le patient connecté
    public List<ResultatExamen> getResultatsByConnectedPatient() throws Exception {
        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = getUtilisateurConnecte();

        // Vérifier si l'utilisateur est un patient
        if (!(utilisateur instanceof Patient)) {
            throw new Exception("L'utilisateur connecté n'est pas un patient.");
        }

        // Récupérer le patient
        Patient patient = (Patient) utilisateur;

        // Récupérer les analyses du patient
        List<Analyse> analyses = analyseRepository.findByPatient(patient);

        // Récupérer tous les résultats d'examen en fonction des analyses
        List<ResultatExamen> resultats = new ArrayList<>();
        for (Analyse analyse : analyses) {
            List<ResultatExamen> resultatsPourAnalyse = resultatExamenRepository.findByAnalyse(analyse);
            resultats.addAll(resultatsPourAnalyse);
        }

        return resultats;
    }
}
