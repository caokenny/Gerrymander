package io.redistrict.auth.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "User")
public class User {

    private Integer uid;
    private String username;
    private String password;
    private String passwordConfirm;
    private String email;
    private boolean admin;
//    private String salt;

    public User(){}

    @Id
    @NotNull
    @GeneratedValue
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

    @NotNull
    public boolean getAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

//    @NotNull
//    public String getSalt() {
//        return salt;
//    }
//
//    public void setSalt(String salt) {
//        this.salt = salt;
//    }
}
