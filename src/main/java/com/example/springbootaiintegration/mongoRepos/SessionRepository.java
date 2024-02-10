package com.example.springbootaiintegration.mongoRepos;

import com.example.springbootaiintegration.mongoRepos.entities.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;


@Repository
public interface SessionRepository extends MongoRepository<Session, String> {

    @Override
    Optional<Session> findById(String uuid);

}
