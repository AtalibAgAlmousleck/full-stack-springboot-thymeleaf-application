package com.atalibdev.repository;

import com.atalibdev.entitie.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Modifying
    @Query(
            value = "UPDATE User u set u.username =: username," +
                    " u.email =: email WHERE u.id =: id"
    )
    void update(String username, String email, Long id);
}
