package Gestion.Clinique.Samake.Controlleur;


import Gestion.Clinique.Samake.Model.Analyse;
import Gestion.Clinique.Samake.Model.CategorieAnalyse;
import Gestion.Clinique.Samake.Model.ResultatExamen;
import Gestion.Clinique.Samake.Model.TypeAnalyse;
import Gestion.Clinique.Samake.Service.AnalyseService;
import Gestion.Clinique.Samake.Service.CategorieAnalyseService;
import Gestion.Clinique.Samake.Service.ResultatExamenService;
import Gestion.Clinique.Samake.Service.TypeAnalyseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/laborantin")
public class LaborantinControlleur {

    @Autowired
    private CategorieAnalyseService categorieAnalyseService;

    @Autowired
    private TypeAnalyseService typeAnalyseService;

    @Autowired
    private AnalyseService analyseService;

    @Autowired
    private ResultatExamenService resultatExamenService;



    // Add a new category
    @PostMapping("/CategorieAnalyse/ajouter")
    public ResponseEntity<CategorieAnalyse> addCategorieAnalyse(@RequestBody CategorieAnalyse categorieAnalyse) {
        return ResponseEntity.ok(categorieAnalyseService.addCategorieAnalyse(categorieAnalyse));
    }

    // Get all categories
    @GetMapping("/CategorieAnalyse/afficherTous")
    public ResponseEntity<List<CategorieAnalyse>> getAllCategories() {
        return ResponseEntity.ok(categorieAnalyseService.getAllCategories());
    }

    // Get category by ID
    @GetMapping("/CategorieAnalyse/afficher/{id}")
    public ResponseEntity<?> getCategorieAnalyseById(@PathVariable int id) {
        Optional<CategorieAnalyse> categorie = categorieAnalyseService.getCategorieAnalyseById(id);
        if (categorie.isPresent()) {
            return ResponseEntity.ok(categorie.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Le categorie d' analyse avec 'ID " + id + " n'existe pas dans la base de données.");
        }
    }

    // Update a category
    @PutMapping("/CategorieAnalyse/modifier/{id}")
    public ResponseEntity<?> updateCategorieAnalyse(@PathVariable int id,
                                                    @RequestBody CategorieAnalyse updatedCategorieAnalyse) {
        Optional<CategorieAnalyse> existingCategorie = categorieAnalyseService.getCategorieAnalyseById(id);
        if (existingCategorie.isPresent()) {
            try {
                return ResponseEntity.ok(categorieAnalyseService.updateCategorieAnalyse(id, updatedCategorieAnalyse));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Le categorie d' analyse avec 'ID " + id + " n'existe pas dans la base de données.");
        }
    }

    // Delete a category
    @DeleteMapping("/CategorieAnalyse/supprimer/{id}")
    public ResponseEntity<?> deleteCategorieAnalyse(@PathVariable int id) {
        Optional<CategorieAnalyse> existingCategorie = categorieAnalyseService.getCategorieAnalyseById(id);
        if (existingCategorie.isPresent()) {
            try {
                categorieAnalyseService.deleteCategorieAnalyse(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok("La catégorie a été supprimée avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Le categorie d' analyse avec 'ID " + id + " n'existe pas dans la base de données.");
        }
    }


    // Endpoint pour créer un nouveau type d'analyse
    @PostMapping("/type-analyses/ajouter")
    public ResponseEntity<TypeAnalyse> createTypeAnalyse(@RequestBody TypeAnalyse typeAnalyse) {
        TypeAnalyse createdTypeAnalyse = typeAnalyseService.createTypeAnalyse(typeAnalyse);
        return ResponseEntity.ok(createdTypeAnalyse);
    }

    // Endpoint pour récupérer tous les types d'analyse
    @GetMapping("/type-analyses/AfficherTous")
    public ResponseEntity<List<TypeAnalyse>> getAllTypeAnalyses() {
        List<TypeAnalyse> typeAnalyses = typeAnalyseService.getAllTypeAnalyses();
        return ResponseEntity.ok(typeAnalyses);
    }

    // Endpoint pour récupérer un type d'analyse par ID
    @GetMapping("/type-analyses/afficher/{id}")
    public ResponseEntity<TypeAnalyse> getTypeAnalyseById(@PathVariable int id) {
        Optional<TypeAnalyse> typeAnalyse = typeAnalyseService.getTypeAnalyseById(id);
        return typeAnalyse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint pour mettre à jour un type d'analyse par ID
    @PutMapping("/type-analyses/modifier/{id}")
    public ResponseEntity<TypeAnalyse> updateTypeAnalyse(@PathVariable int id, @RequestBody TypeAnalyse updatedTypeAnalyse) {
        try {
            TypeAnalyse updated = typeAnalyseService.updateTypeAnalyse(id, updatedTypeAnalyse);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint pour supprimer un type d'analyse par ID
    @DeleteMapping("/type-analyses/supprimer/{id}")
    public ResponseEntity<Void> deleteTypeAnalyse(@PathVariable int id) {
        typeAnalyseService.deleteTypeAnalyse(id);
        return ResponseEntity.noContent().build();
    }




    // Endpoint pour afficher les analyse qui ont l'etat en attente
    @GetMapping("/analyses/en-attente")
    public ResponseEntity<Long> getAnalysesEnAttente() {
        long nombreAnalysesEnAttente = analyseService.getAnalysesEnAttente();
        return new ResponseEntity<>(nombreAnalysesEnAttente, HttpStatus.OK);
    }

    // Endpoint pour afficher les analyse qui ont l'etat traiter
    @GetMapping("/analyses/traiter")
    public ResponseEntity<Long> getAnalysesTraitees() {
        long nombreAnalysesTraitees = analyseService.getAnalysesTraitees();
        return new ResponseEntity<>(nombreAnalysesTraitees, HttpStatus.OK);
    }

    // Endpoint pour afficher le total d'analyse
    @GetMapping("/analyses/total")
    public ResponseEntity<Long> getTotalAnalyses() {
        long totalAnalyses = analyseService.getTotalAnalyses();
        return new ResponseEntity<>(totalAnalyses, HttpStatus.OK);
    }


    // Endpoint pour créer un nouveau résultat d'examen
    @PostMapping("/resultats-examens/ajouter")
    public ResponseEntity<ResultatExamen> createResultatExamen(@RequestBody ResultatExamen resultatExamen) throws Exception {
        ResultatExamen createdResultat = resultatExamenService.createResultatExamen(resultatExamen);
        return ResponseEntity.ok(createdResultat);
    }

    // Endpoint pour récupérer tous les résultats d'examen
    @GetMapping("/resultats-examens/afficherTous")
    public ResponseEntity<List<ResultatExamen>> getAllResultats() {
        List<ResultatExamen> resultats = resultatExamenService.getAllResultats();
        return ResponseEntity.ok(resultats);
    }

    // Endpoint pour récupérer un résultat d'examen par ID
    @GetMapping("/resultats-examens/afficher/{id}")
    public ResponseEntity<ResultatExamen> getResultatById(@PathVariable int id) {
        Optional<ResultatExamen> resultat = resultatExamenService.getResultatById(id);
        return resultat.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint pour mettre à jour un résultat d'examen
    @PutMapping("/resultats-examens/modifier/{id}")
    public ResponseEntity<ResultatExamen> updateResultatExamen(@PathVariable int id, @RequestBody ResultatExamen updatedResultat) {
        try {
            ResultatExamen updated = resultatExamenService.updateResultatExamen(id, updatedResultat);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint pour supprimer un résultat d'examen
    @DeleteMapping("/resultats-examens/supprimer/{id}")
    public ResponseEntity<Void> deleteResultatExamen(@PathVariable int id) {
        resultatExamenService.deleteResultatExamen(id);
        return ResponseEntity.noContent().build();
    }








}
