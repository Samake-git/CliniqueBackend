package Gestion.Clinique.Samake.Service;

import Gestion.Clinique.Samake.Model.PrescriptionDetail;
import Gestion.Clinique.Samake.Model.Prescription;
import Gestion.Clinique.Samake.Repository.PrescriptionDetailRepository;
import Gestion.Clinique.Samake.Repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionDetailService {

    @Autowired
    private PrescriptionDetailRepository prescriptionDetailRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    // Créer un nouveau détail de prescription
    public PrescriptionDetail createPrescriptionDetail(PrescriptionDetail prescriptionDetail) throws Exception {
        // Vérifier si la prescription existe
        Optional<Prescription> prescriptionOpt = prescriptionRepository.findById(prescriptionDetail.getPrescription().getId());
        if (!prescriptionOpt.isPresent()) {
            throw new IllegalArgumentException("Prescription introuvable avec l'ID: " + prescriptionDetail.getPrescription().getId());
        }

        // Associer le détail à la prescription
        prescriptionDetail.setPrescription(prescriptionOpt.get());
        return prescriptionDetailRepository.save(prescriptionDetail);
    }


    // Récupérer tous les détails de prescription
    public List<PrescriptionDetail> getAllPrescriptionDetails() {
        return prescriptionDetailRepository.findAll();
    }

    // Récupérer un détail de prescription par ID
    // Récupérer tous les détails pour une prescription donnée
    public List<PrescriptionDetail> getPrescriptionDetailsByPrescriptionId(Integer prescriptionId) {
        return prescriptionDetailRepository.findByPrescriptionId(prescriptionId);
    }

    // Mettre à jour un détail de prescription existant
    public PrescriptionDetail updatePrescriptionDetail(Integer id, PrescriptionDetail updatedPrescriptionDetail) {
        Optional<PrescriptionDetail> existingDetailOpt = prescriptionDetailRepository.findById(id);
        if (existingDetailOpt.isPresent()) {
            PrescriptionDetail detail = existingDetailOpt.get();
            detail.setNomMedicament(updatedPrescriptionDetail.getNomMedicament());
            detail.setDosage(updatedPrescriptionDetail.getDosage());
            detail.setFrequence(updatedPrescriptionDetail.getFrequence());
            detail.setDuree(updatedPrescriptionDetail.getDuree());
            detail.setInstructions(updatedPrescriptionDetail.getInstructions());
            detail.setDatePremierDose(updatedPrescriptionDetail.getDatePremierDose());
            detail.setHeurePremierDose(updatedPrescriptionDetail.getHeurePremierDose());
            return prescriptionDetailRepository.save(detail);
        } else {
            throw new RuntimeException("Détail de prescription introuvable avec ID: " + id);
        }
    }

    // Supprimer un détail de prescription par ID
    public void deletePrescriptionDetail(Integer id) {
        prescriptionDetailRepository.deleteById(id);
    }
}