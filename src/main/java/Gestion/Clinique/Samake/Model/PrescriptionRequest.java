package Gestion.Clinique.Samake.Model;

import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequest {
    private Long patientId;  // ID du patient
    private Prescription prescription;  // Prescription elle-même
    private List<PrescriptionDetail> details;
}
