package com.example.springbootaiintegration.mongoRepos.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashMap;


@Document
@Data
public class History {

    @Id
    //current cookie, the same as session
    private String id;

    //stores cookies and names
    private LinkedHashMap<String, String> sessionIdsAndNames;

}
