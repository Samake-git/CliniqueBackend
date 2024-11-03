package Gestion.Clinique.Samake.DTO;


import Gestion.Clinique.Samake.Model.FileInfo;
import Gestion.Clinique.Samake.Model.RoleType;
import Gestion.Clinique.Samake.Model.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRep {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String nom;
    private String prenom;
    private String email;
    private String phone;
    private String adresse;
    private Long id;
    private String password;
    private RoleType roleType;
    private Utilisateur utilisateur;
    private List<Utilisateur> utilisateursList;
    private FileInfo photos;
}


