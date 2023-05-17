package org.example.reactormessagingclient.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CustomDto {
    private int id;
    private String name;
}
