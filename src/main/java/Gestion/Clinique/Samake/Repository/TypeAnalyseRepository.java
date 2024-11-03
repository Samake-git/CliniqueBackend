package Gestion.Clinique.Samake.Repository;

import Gestion.Clinique.Samake.Model.TypeAnalyse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeAnalyseRepository extends JpaRepository<TypeAnalyse, Integer> {
}
