package io.redistrict.auth.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "Role")
public class Role {

    private int rid;
    private String name;
    private Set<User> users;

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    public int getRid() {
        return this.rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    @NotNull
    @Column
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "roles")
    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}
