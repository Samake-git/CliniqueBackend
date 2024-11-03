package Gestion.Clinique.Samake.Service;


import Gestion.Clinique.Samake.Model.Departement;
import Gestion.Clinique.Samake.Repository.DepartementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartementService {

    @Autowired
    private DepartementRepository departementRepository;

    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }

    public Departement getDepartementById(Long id) {
        return departementRepository.findById(id).orElse(null);
    }

    public Departement saveDepartement(Departement departement) {
        return departementRepository.save(departement);
    }

    public void modifierDepartement(Long id, Departement departementModifie) {
        // Vérifier si le département existe dans la base de données
        if (!departementRepository.existsById(id)) {
            throw new RuntimeException("Le département avec l'ID " + id + " n'existe pas.");
        }

        // Modifier le département
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Le département n'a pas été trouvé."));

        // Copier les propriétés du nouveau département vers le département existant
        departement.setNom(departementModifie.getNom());
        departement.setDescription(departementModifie.getDescription());

        // Sauvegarder les modifications
        departementRepository.save(departement);
    }


    public void deleteDepartement(Long id) throws Exception {
        // Vérifier si le département existe dans la base de données
        if (departementRepository.existsById(id)) {
            // Si le département existe, on le supprime
            departementRepository.deleteById(id);
        } else {
            // Si le département n'existe pas, on lance une exception ou on renvoie un message
            throw new Exception("Le département avec l'ID " + id + " n'existe pas.");
        }
    }

}
