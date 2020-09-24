package com.example.dao;

import lombok.Data;

import java.io.Serializable;

@Data
public class Pageable implements Serializable {
    private Integer pageNumber;
    private Integer pageSize;
}
