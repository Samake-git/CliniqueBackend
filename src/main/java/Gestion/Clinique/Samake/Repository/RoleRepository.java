package Gestion.Clinique.Samake.Repository;


import Gestion.Clinique.Samake.Model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository  extends JpaRepository<RoleType, Long> {

    Optional<RoleType> findByNom(String nom);
}
