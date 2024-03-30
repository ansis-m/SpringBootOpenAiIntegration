package com.example.springbootaiintegration.mongoRepos.entities;

import com.example.springbootaiintegration.mongoRepos.dtos.ExchangeDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class History {

    @Id
    private String id;

    private List<Session> history;

}
