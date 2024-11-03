package Gestion.Clinique.Samake.Repository;


import Gestion.Clinique.Samake.Model.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Integer> {

    List<PrescriptionDetail> findByPrescriptionId(Integer prescriptionId);

}

