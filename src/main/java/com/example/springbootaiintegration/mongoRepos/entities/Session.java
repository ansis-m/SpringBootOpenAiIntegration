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
    //current cookie
    private String id;
    private String name;
    private Model currentModel;
    private LinkedList<ExchangeDto> exchanges;

    public Session(){}

    public Session(String id) {
        this.id = id;
        this.exchanges = new LinkedList<>();
    }

    public Session(String id, LinkedList<ExchangeDto> exchanges) {
        this.id = id;
        this.exchanges = exchanges;
    }

}
