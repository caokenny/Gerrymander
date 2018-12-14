package io.redistrict.auth.repository;

import io.redistrict.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface RoleDao extends JpaRepository<Role, Integer> {

    Role findByName(String name);

}
