package io.redistrict.auth.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "User")
public class User {

    private Integer uid;
    private String username;
    private String password;
    private String passwordConfirm;
    private String email;
    private Set<Role> roles;
    private List<SavedWeights> savedWeights;


    public User(){}

    public User(User user) {
        this.uid = user.getUid();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.passwordConfirm = user.getPasswordConfirm();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    public Integer getUid() { return this.uid; }

    public void setUid(Integer uid) { this.uid = uid; }

    @NotNull
    @Column(unique = true)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NotNull
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public String getPasswordConfirm() {
        return this.passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    @NotNull
    @Column(unique = true)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @ManyToMany
    @JoinTable(name = "user_savedweights", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "savedweights_id"))
    public List<SavedWeights> getSavedWeights() {
        return savedWeights;
    }

    public void setSavedWeights(List<SavedWeights> savedWeights) {
        this.savedWeights = savedWeights;
    }
}
