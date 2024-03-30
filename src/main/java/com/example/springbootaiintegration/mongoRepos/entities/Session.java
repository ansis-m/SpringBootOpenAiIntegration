package com.example.springbootaiintegration.mongoRepos.entities;

import com.example.springbootaiintegration.mongoRepos.dtos.ExchangeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;

@Document
@Getter
@Setter
@AllArgsConstructor
public class Session {
    @Id
    private String id;
    private LinkedList<ExchangeDto> exchanges;

}
