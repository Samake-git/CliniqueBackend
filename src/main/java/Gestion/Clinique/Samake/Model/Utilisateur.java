package Gestion.Clinique.Samake.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Utilisateur implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    @Column(unique = true)
    private String email;
    private String phone;
    private String username;
    private String password;
    private String sexe;
    private  String adresse;
    private String specialite;
    @OneToOne
    private FileInfo photos;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleType roleType;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.getRoleType() != null) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + this.getRoleType().getNom()));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
