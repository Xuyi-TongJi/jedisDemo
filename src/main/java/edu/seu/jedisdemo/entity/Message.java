package edu.seu.jedisdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {

    private boolean execution;
    private String message;
}
