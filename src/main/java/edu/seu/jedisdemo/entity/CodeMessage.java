package edu.seu.jedisdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodeMessage {

    private boolean flag;
    private String code;
    private String message;
}
