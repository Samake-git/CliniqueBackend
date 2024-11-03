package Gestion.Clinique.Samake.DTO;


import Gestion.Clinique.Samake.Model.Prescription;
import Gestion.Clinique.Samake.Model.PrescriptionDetail;
import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequest {
    private Prescription prescription;
    private Long patientId;
    private List<PrescriptionDetail> details;

}