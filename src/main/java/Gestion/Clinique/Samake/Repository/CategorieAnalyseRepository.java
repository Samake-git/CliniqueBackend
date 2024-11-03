package Gestion.Clinique.Samake.Repository;


import Gestion.Clinique.Samake.Model.CategorieAnalyse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieAnalyseRepository extends JpaRepository<CategorieAnalyse, Integer> {
}