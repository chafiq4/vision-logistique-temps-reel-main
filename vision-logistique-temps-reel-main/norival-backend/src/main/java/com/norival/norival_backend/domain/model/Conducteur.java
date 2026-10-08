package com.norival.norival_backend.domain.model;

public class Conducteur {
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private Boolean actif;
    private String email;
    private String password;
    private String role;
    private Long chantierId;

    public Conducteur() {}

    public Conducteur(Long id, String nom, String prenom, String telephone, Boolean actif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.actif = actif;
    }

    public Conducteur(Long id, String nom, String prenom, String telephone, Boolean actif, String email, String password) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.actif = actif;
        this.email = email;
        this.password = password;
    }

    public Conducteur(Long id, String nom, String prenom, String telephone, Boolean actif, String email, String password, String role, Long chantierId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.actif = actif;
        this.email = email;
        this.password = password;
        this.role = role;
        this.chantierId = chantierId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getChantierId() { return chantierId; }
    public void setChantierId(Long chantierId) { this.chantierId = chantierId; }
}
