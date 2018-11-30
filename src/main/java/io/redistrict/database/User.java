package io.redistrict.database;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class User {

    private Integer uid;
    private String username;
    private String password;
    private String email;
    private boolean admin;
    private String salt;

    public User(){}

    public User(String username, String password, String email, String salt, boolean admin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.salt = salt;
        this.admin = admin;
    }

    @Id
    @NotNull
    @GeneratedValue
    @Column(name = "uid", unique = true)
    public Integer getUid() { return this.uid; }

    public void setUid(Integer uid) { this.uid = uid; }

    @NotNull
    @Column(name = "username", unique = true)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NotNull
    @Column(name = "password")
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotNull
    @Column(name = "email")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    @Column(name = "admin")
    public boolean getAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @NotNull
    @Column(name = "salt")
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
