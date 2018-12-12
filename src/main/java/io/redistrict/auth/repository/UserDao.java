package io.redistrict.auth.repository;

import io.redistrict.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    List<User> findAll();

    User findByUsername(String username);

    User deleteByUsername(String username);

}
