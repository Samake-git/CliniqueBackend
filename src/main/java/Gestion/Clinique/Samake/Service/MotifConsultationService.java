package Gestion.Clinique.Samake.Service;



import Gestion.Clinique.Samake.Model.MotifConsultation;
import Gestion.Clinique.Samake.Repository.MotifConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MotifConsultationService {

    @Autowired
    private MotifConsultationRepository motifConsultationRepository;

    public MotifConsultation ajouterMotifConsultation(MotifConsultation motif) {
        return motifConsultationRepository.save(motif);
    }

    public MotifConsultation modifierMotifConsultation(int id, MotifConsultation updatedMotif) {
        MotifConsultation motif = motifConsultationRepository.findById(id).orElseThrow(() -> new RuntimeException("Motif not found"));
        motif.setNom(updatedMotif.getNom());
        motif.setDescription(updatedMotif.getDescription());
        motif.setPrix(updatedMotif.getPrix());
        return motifConsultationRepository.save(motif);
    }

    // Supprimer un motif de consultation par ID
    public void deleteMotif(int id) throws Exception {
        MotifConsultation motif = motifConsultationRepository.findById(id)
                .orElseThrow(() -> new Exception("Motif non trouvé avec l'ID : " + id));
        motifConsultationRepository.delete(motif);
    }

    // Méthode pour récupérer tous les motifs
    public List<MotifConsultation> getAllMotifs() {
        return motifConsultationRepository.findAll();
    }

    // Méthode pour récupérer un motif par ID
    public MotifConsultation getMotifById(int id) throws Exception {
        return motifConsultationRepository.findById(id)
                .orElseThrow(() -> new Exception("Motif non trouvé avec l'ID : " + id));
    }

}
