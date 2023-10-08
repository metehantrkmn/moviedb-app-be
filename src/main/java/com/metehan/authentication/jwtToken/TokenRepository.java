package com.metehan.authentication.jwtToken;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<Token,Integer> {
    Optional<Token> findTokenByToken(String token);
    //find all unrevoked tokens of a user => use custom query
    @Query(value="""
            select t from Token t where t.user.id=:id and t.revoked=false
            """, nativeQuery = false)
    List<Token> getActiveTokens(@Param("id") Integer id);
}
