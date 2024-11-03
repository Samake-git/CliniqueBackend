package Gestion.Clinique.Samake.DTO;

import lombok.Data;

@Data
public class PaymentResponse {

    private boolean success;
    private String message;
    private double montant;

    public PaymentResponse(boolean success, String message, double montant) {
        this.success = success;
        this.message = message;
        this.montant = montant;
    }
}
