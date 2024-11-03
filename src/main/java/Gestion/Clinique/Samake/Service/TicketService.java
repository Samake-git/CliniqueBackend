package Gestion.Clinique.Samake.Service;

import Gestion.Clinique.Samake.Model.MotifConsultation;
import Gestion.Clinique.Samake.Model.Paiement;
import Gestion.Clinique.Samake.Model.Ticket;
import Gestion.Clinique.Samake.Model.Utilisateur;
import Gestion.Clinique.Samake.Repository.MotifConsultationRepository;
import Gestion.Clinique.Samake.Repository.PaiementRepository;
import Gestion.Clinique.Samake.Repository.TicketRepository;
import Gestion.Clinique.Samake.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private MotifConsultationRepository motifConsultationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Méthode pour récupérer l'utilisateur connecté
    private Utilisateur getUtilisateurConnecte() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé"));
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé"));
    }

    public Ticket ajouterTicket(Ticket ticket) throws Exception {
        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = getUtilisateurConnecte(); // Utiliser la méthode déjà décrite

        // Assigner l'utilisateur authentifié au ticket
        ticket.setUser(utilisateur);
        ticket.setDateCreation(new Date());
        ticket.setEtat("En Attente");

        // Si le motif de consultation est fourni dans le ticket, le lier
        if (ticket.getMotifConsultation() != null) {
            MotifConsultation motif = ticket.getMotifConsultation();
            // Assurez-vous que le motif est récupéré de la base de données, sinon il peut être nul
            MotifConsultation motifDB = motifConsultationRepository.findById(motif.getId())
                    .orElseThrow(() -> new Exception("Motif de consultation non trouvé"));
            ticket.setMotifConsultation(motifDB);
        } else {
            throw new Exception("Motif de consultation est requis");
        }

        // Sauvegarder le ticket dans le repository
        return ticketRepository.save(ticket);
    }


    public Ticket prendreEnChargeTicket(Long id) throws Exception {
        Ticket ticket = getTicketById(id);
        Utilisateur medecin = getUtilisateurConnecte();  // Récupérer le médecin connecté

        ticket.setMedecin(medecin);  // Assigner le médecin au ticket
        ticket.setEtat("En cours");
        return ticketRepository.save(ticket);
    }

    public Ticket traiterTicket(Long id) throws Exception {
        Ticket ticket = getTicketById(id);
        Utilisateur medecin = getUtilisateurConnecte();  // Récupérer le médecin connecté

        ticket.setMedecin(medecin);  // Assigner le médecin au ticket
        ticket.setEtat("Traiter");
        return ticketRepository.save(ticket);
    }

    public Ticket modifierTicket(Long id, Ticket ticketDetails) {
        Ticket ticket = getTicketById(id);
        ticket.setEtat(ticketDetails.getEtat());
        return ticketRepository.save(ticket);
    }

    public void supprimerTicket(Long id) {
        Ticket ticket = getTicketById(id);
        ticketRepository.delete(ticket);
    }

    public long getTicketsEnAttente() {
        return ticketRepository.countByEtat("En Attente");
    }

    public long getTicketsEnCours() {
        return ticketRepository.countByEtat("En cours");
    }

    public long getTicketsTraites() {
        return ticketRepository.countByEtat("Traiter");
    }

    public long getTotalTickets() {
        return ticketRepository.count();
    }

    public Map<String, Long> getTicketsByCreationDate() {
        List<Ticket> tickets = ticketRepository.findAll();
        Map<String, Long> ticketsByCreationDate = new HashMap<>();

        for (Ticket ticket : tickets) {
            String creationDate = new SimpleDateFormat("yyyy-MM-dd").format(ticket.getDateCreation());
            if (ticketsByCreationDate.containsKey(creationDate)) {
                ticketsByCreationDate.put(creationDate, ticketsByCreationDate.get(creationDate) + 1);
            } else {
                ticketsByCreationDate.put(creationDate, 1L);
            }
        }

        return ticketsByCreationDate;
    }


    // Méthode pour payer un ticket

    public String payerTicket(Long ticketId) throws Exception {
        Utilisateur utilisateur = getUtilisateurConnecte();
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);

        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();

            if (!ticket.isEstPaye()) {
                double montantAPayer = ticket.getMotifConsultation().getPrix();

                Paiement paiement = new Paiement();
                paiement.setTicket(ticket);
                paiement.setUtilisateur(utilisateur);
                paiement.setMontant((float) montantAPayer);
                paiement.setDatePaiement(new Date());
                paiement.setEstPaye(true);
                paiementRepository.save(paiement);
                ticket.setEstPaye(true);
                ticket.setDatePaiement(new Date());
                ticketRepository.save(ticket);

                return "Le paiement du ticket a été effectué avec succès. Montant : " + montantAPayer;
            } else {
                return "Ce ticket a déjà été payé.";
            }
        } else {
            return "Ticket introuvable.";
        }
    }
}
