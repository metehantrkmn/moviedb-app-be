package com.moviedb.app.authentication.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
    Optional<User> findUserByEmail(String email);
    boolean existsUserByEmail(String email);

    @Query(value = """
            select u from User u where u.enabled=false
            """)
    List<User> getAllUnVerifiedUsers();

    //this method doesnt work because in custom queries entity manager doenst intercept cascaded operations
    //The @Modifying annotation is used to enhance the @Query annotation so that we can execute not only SELECT queries,
    //but also INSERT, UPDATE, DELETE, and even DDL queries.
    @Modifying
    @Query(value = """
            delete from User u where u.enabled=false
            """)
    void deleteUnVerifiedUsers();

    long deleteUserByEnabled(boolean bool);
}
