package Gestion.Clinique.Samake.Repository;


import Gestion.Clinique.Samake.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    long countByEtat(String enAttente);
}