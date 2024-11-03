package Gestion.Clinique.Samake.Service;

import Gestion.Clinique.Samake.Model.CategorieAnalyse;
import Gestion.Clinique.Samake.Repository.CategorieAnalyseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieAnalyseService {

    @Autowired
    private CategorieAnalyseRepository categorieAnalyseRepository;

    // Add a new CategorieAnalyse
    public CategorieAnalyse addCategorieAnalyse(CategorieAnalyse categorieAnalyse) {
        return categorieAnalyseRepository.save(categorieAnalyse);
    }

    // Get all CategorieAnalyse
    public List<CategorieAnalyse> getAllCategories() {
        return categorieAnalyseRepository.findAll();
    }

    // Get a CategorieAnalyse by ID
    public Optional<CategorieAnalyse> getCategorieAnalyseById(int id) {
        return categorieAnalyseRepository.findById(id);
    }

    // Update an existing CategorieAnalyse
    public CategorieAnalyse updateCategorieAnalyse(int id, CategorieAnalyse updatedCategorie) throws Exception {
        Optional<CategorieAnalyse> existingCategorie = categorieAnalyseRepository.findById(id);
        if (existingCategorie.isPresent()) {
            CategorieAnalyse categorie = existingCategorie.get();
            categorie.setNom(updatedCategorie.getNom());
            // Set other fields as necessary
            return categorieAnalyseRepository.save(categorie);
        } else {
            throw new Exception("ID " + id + " n'existe pas dans le repository.");
        }
    }

    // Delete a CategorieAnalyse by ID
    public void deleteCategorieAnalyse(int id) throws Exception {
        Optional<CategorieAnalyse> existingCategorie = categorieAnalyseRepository.findById(id);
        if (existingCategorie.isPresent()) {
            categorieAnalyseRepository.deleteById(id);
        } else {
            throw new Exception("ID " + id + " n'existe pas dans le repository.");
        }
    }
}
