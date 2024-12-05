package com.directa24.main.challenge.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DirectorsResponse {
    private List<String> directors;
}
