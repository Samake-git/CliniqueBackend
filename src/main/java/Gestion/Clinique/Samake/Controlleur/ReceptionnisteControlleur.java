package Gestion.Clinique.Samake.Controlleur;

import Gestion.Clinique.Samake.Model.MotifConsultation;
import Gestion.Clinique.Samake.Model.Ticket;
import Gestion.Clinique.Samake.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/receptionniste")
public class ReceptionnisteControlleur {


    @Autowired
    private TicketService ticketService;

    @Autowired
    private MotifConsultationService motifConsultationService;

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private AnalyseService analyseService;
    @Autowired
    private AdminService adminService;


    // Endpoint pour récupérer tous les tickets
    @GetMapping(path = "/ticket/afficherTous")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    // Endpoint pour récupérer un ticket par ID
    @GetMapping("/ticket/afficher/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        try {
            Ticket ticket = ticketService.getTicketById(id);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint pour créer un nouveau ticket
    @PostMapping("/ticket/Creer")
    public ResponseEntity<?> ajouterTicket(@RequestBody Ticket ticket) {
        try {
            Ticket nouveauTicket = ticketService.ajouterTicket(ticket);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauTicket);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Endpoint pour supprimer un ticket par son ID
    @DeleteMapping("/ticket/supprimer/{id}")
    public ResponseEntity<?> supprimerTicket(@PathVariable Long id) { // Changez int en Long
        try {
            ticketService.supprimerTicket(id);
            return ResponseEntity.ok("Le ticket a été supprimé avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    // Endpoint pour modifier un ticket
    @PutMapping("/ticket/modifier/{id}")
    public ResponseEntity<?> modifierTicket(@PathVariable Long id, @RequestBody Ticket ticketDetails) { // Changez int en Long
        try {
            Ticket ticketModifie = ticketService.modifierTicket(id, ticketDetails);
            return ResponseEntity.ok(ticketModifie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
// Endpoint pour afficher le nombre de ticket en attente
    @GetMapping("/ticket/en-attente")
    public ResponseEntity<Long> getTicketsEnAttente() {
        long nombreTicketsEnAttente = ticketService.getTicketsEnAttente();
        return new ResponseEntity<>(nombreTicketsEnAttente, HttpStatus.OK);
    }

    // Endpoint pour afficher le nombre de ticket en cours
    @GetMapping("/ticket/en-cours")
    public ResponseEntity<Long> getTicketsEnCours() {
        long nombreTicketsEnCours = ticketService.getTicketsEnCours();
        return new ResponseEntity<>(nombreTicketsEnCours, HttpStatus.OK);
    }

    // Endpoint pour afficher le total de ticket
    @GetMapping("/ticket/total")
    public ResponseEntity<Long> getTotalTickets() {
        long totalTickets = ticketService.getTotalTickets();
        return new ResponseEntity<>(totalTickets, HttpStatus.OK);
    }






    // Endpoint pour afficher le nombre de ticket traiter
    @GetMapping("/ticket/traites")
    public ResponseEntity<Long> getTicketsTraites() {
        long nombreTicketsTraites = ticketService.getTicketsTraites();
        return new ResponseEntity<>(nombreTicketsTraites, HttpStatus.OK);
    }

    @GetMapping("/ticket/par-date-creation")
    public ResponseEntity<Map<String, Long>> getTicketsByCreationDate() {
        Map<String, Long> ticketsByCreationDate = ticketService.getTicketsByCreationDate();
        return new ResponseEntity<>(ticketsByCreationDate, HttpStatus.OK);
    }


    // Récupérer tous les motifs de consultation
    @GetMapping(path = "/motifconsultation/afficherTous")
    public List<MotifConsultation> getAllMotifs() {
        return motifConsultationService.getAllMotifs();
    }

    // Récupérer un motif de consultation par ID
    @GetMapping("/motifconsultation/afficherPar/{id}")
    public ResponseEntity<MotifConsultation> getMotifById(@PathVariable int id) {
        try {
            MotifConsultation motif = motifConsultationService.getMotifById(id);
            return new ResponseEntity<>(motif, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Créer un nouveau motif de consultation
    @PostMapping(path = "/motifconsultation/Creer")
    public ResponseEntity<MotifConsultation> createMotif(@RequestBody MotifConsultation motif) {
        try {
            MotifConsultation createdMotif = motifConsultationService.ajouterMotifConsultation(motif);
            return new ResponseEntity<>(createdMotif, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Modifier un motif de consultation
    @PutMapping("/motifconsultation/modifier/{id}")
    public ResponseEntity<MotifConsultation> updateMotif(@PathVariable int id, @RequestBody MotifConsultation motifDetails) {
        try {
            MotifConsultation updatedMotif = motifConsultationService.modifierMotifConsultation(id, motifDetails);
            return new ResponseEntity<>(updatedMotif, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Supprimer un motif de consultation
    @DeleteMapping("/motifconsultation/supprimer/{id}")
    public ResponseEntity<Void> deleteMotif(@PathVariable int id) {
        try {
            motifConsultationService.deleteMotif(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Retourne un statut 204 (No Content) en cas de succès
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Retourne un statut 404 si le motif n'est pas trouvé
        }
    }


    // Endpoint Payement

    @PreAuthorize("hasRole('RECEPTIONNISTE') or hasRole('ADMIN')")
    @PostMapping("/ticket/payer/{ticketId}")
    public ResponseEntity<Map<String, String>> payerTicket(@PathVariable Long ticketId) throws Exception {
        String message = ticketService.payerTicket(ticketId);

        // Créer une réponse JSON
        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyse/payer/{analyseId}")
    public ResponseEntity<Map<String, String>> payerAnalyse(@PathVariable Long analyseId) throws Exception {
        String message= analyseService.payerAnalyse(analyseId);

        // Créer une réponse JSON
        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/payement/annuler/{id}")
    public ResponseEntity<String> annulerPaiement(@PathVariable Long id) {
        try {
            String message = paiementService.annulerPaiement(id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/payement/somme")
    public ResponseEntity<Double> getSommeTotalDesPaiements() {
        double somme = paiementService.calculerSommeTotalDesPaiements();
        return ResponseEntity.ok(somme);
    }


    // Endpoint pour afficher le total de ticket
    @GetMapping("/ticket/Patient_total")
    public ResponseEntity<Long> getTotalPatients() {
        long totalPatients = adminService.getTotalPatients();
        return new ResponseEntity<>(totalPatients, HttpStatus.OK);
    }







}
