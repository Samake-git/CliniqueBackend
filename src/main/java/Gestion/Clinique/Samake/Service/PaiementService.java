package Gestion.Clinique.Samake.Service;

import Gestion.Clinique.Samake.Model.Analyse;
import Gestion.Clinique.Samake.Model.Paiement;
import Gestion.Clinique.Samake.Model.Ticket;
import Gestion.Clinique.Samake.Repository.AnalyseRepository;
import Gestion.Clinique.Samake.Repository.PaiementRepository;
import Gestion.Clinique.Samake.Repository.TicketRepository;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AnalyseRepository analyseRepository;



    public double calculerSommeTotalDesPaiements() {
        List<Paiement> paiements = paiementRepository.findByEstPayeTrue();
        return paiements.stream()
                .mapToDouble(Paiement::getMontant) // Récupérer le montant de chaque paiement
                .sum(); // Calculer la somme
    }

    public String annulerPaiement(Long paiementId) throws Exception {
        Optional<Paiement> paiementOptional = paiementRepository.findById(paiementId);

        if (paiementOptional.isPresent()) {
            Paiement paiement = paiementOptional.get();
            if (!paiement.isEstAnnule()) { // Vérifiez si le paiement n'est pas déjà annulé
                paiement.setEstAnnule(true); // Marquez comme annulé

                // Si le paiement est lié à un ticket
                if (paiement.getTicket() != null) {
                    Ticket ticket = paiement.getTicket();
                    ticket.setEstPaye(false); // Marquez le ticket comme non payé
                    ticketRepository.save(ticket);
                }

                // Si le paiement est lié à une analyse
                if (paiement.getAnalyse() != null) {
                    Analyse analyse = paiement.getAnalyse();
                    analyse.setEstPaye(false); // Marquez l'analyse comme non payée
                    analyseRepository.save(analyse);
                }

                paiementRepository.save(paiement);
                return "Le paiement a été annulé avec succès.";
            } else {
                return "Le paiement a déjà été annulé.";
            }
        } else {
            return "Paiement introuvable.";
        }
    }



}
