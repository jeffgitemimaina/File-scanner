package com.velocistech.filescanner01.Repository;

import com.velocistech.filescanner01.Entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

        Boolean existsByEmail(String Email);
       Optional<UserAuth> findByEmail(String Email);
    @Query("select max(u.id) from UserAuth u")
        public Long findMaxId();

    @Transactional
    @Modifying
    @Query("UPDATE UserAuth a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);

}
