package com.example.springbootaiintegration.mongoRepos;

import com.example.springbootaiintegration.mongoRepos.entities.History;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface HistoryRepository extends MongoRepository<History, String> {
    @Override
    Optional<History> findById(String uuid);

}
