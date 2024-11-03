package Gestion.Clinique.Samake.Service;

import Gestion.Clinique.Samake.Model.TypeAnalyse;
import Gestion.Clinique.Samake.Repository.TypeAnalyseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeAnalyseService {

    @Autowired
    private TypeAnalyseRepository typeAnalyseRepository;

    // Créer un nouveau TypeAnalyse
    public TypeAnalyse createTypeAnalyse(TypeAnalyse typeAnalyse) {
        return typeAnalyseRepository.save(typeAnalyse);
    }

    // Récupérer tous les types d'analyse
    public List<TypeAnalyse> getAllTypeAnalyses() {
        return typeAnalyseRepository.findAll();
    }

    // Récupérer un type d'analyse par ID
    public Optional<TypeAnalyse> getTypeAnalyseById(int id) {
        return typeAnalyseRepository.findById(id);
    }

    // Mettre à jour un type d'analyse
    public TypeAnalyse updateTypeAnalyse(int id, TypeAnalyse updatedTypeAnalyse) {
        Optional<TypeAnalyse> existingTypeAnalyse = typeAnalyseRepository.findById(id);
        if (existingTypeAnalyse.isPresent()) {
            TypeAnalyse typeAnalyse = existingTypeAnalyse.get();
            typeAnalyse.setNom(updatedTypeAnalyse.getNom());
            typeAnalyse.setPrix(updatedTypeAnalyse.getPrix());
            return typeAnalyseRepository.save(typeAnalyse);
        } else {
            throw new RuntimeException("Type d'analyse introuvable avec ID: " + id);
        }
    }

    // Supprimer un type d'analyse par ID
    public void deleteTypeAnalyse(int id) {
        typeAnalyseRepository.deleteById(id);
    }
}
