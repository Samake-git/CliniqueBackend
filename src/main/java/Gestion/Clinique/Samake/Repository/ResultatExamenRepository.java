package Gestion.Clinique.Samake.Repository;
import Gestion.Clinique.Samake.Model.Analyse;
import Gestion.Clinique.Samake.Model.ResultatExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ResultatExamenRepository extends JpaRepository<ResultatExamen, Integer> {

    List<ResultatExamen> findByAnalyse(Analyse analyse);

}
