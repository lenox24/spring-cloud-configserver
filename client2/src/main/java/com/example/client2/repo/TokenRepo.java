package com.example.client2.repo;

import com.example.client2.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TokenRepo extends MongoRepository<Token, String> {
    List<Token> findByTypeAndAgree(String type, String Agree);

    Token findByToken(String token);

    List<Token> findByAgree(String agree);
}
