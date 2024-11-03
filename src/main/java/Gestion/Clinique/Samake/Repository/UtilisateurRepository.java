package Gestion.Clinique.Samake.Repository;



import Gestion.Clinique.Samake.Model.RoleType;
import Gestion.Clinique.Samake.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findById(Long id);
    Optional<Utilisateur> findByEmail(String Email);
    List<Utilisateur> findByRoleType(RoleType roleType);

    Optional<Utilisateur> findByUsername(String Username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<Utilisateur> findAllByRoleType(Optional<RoleType> patientRole);

    // Requête pour récupérer les utilisateurs avec le rôle PATIENT
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.roleType = :roleType")
    long countByRole(String roleType);
}
