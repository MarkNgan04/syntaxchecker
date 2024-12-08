package com.syntaxchecker.app;

public class SyntaxCheckerRequest {
    private String code; // The code snippet to be checked

    // Constructor
    public SyntaxCheckerRequest(String code) {
        this.code = code;
    }

    // Getter and Setter
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}