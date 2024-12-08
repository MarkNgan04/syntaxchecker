package com.syntaxchecker.app;

import org.springframework.stereotype.Service;

@Service
public class SyntaxCheckerService {

    private int totalCalls = 0; // To track the number of API calls

    // Method to check the syntax of the provided code
    public boolean checkSyntax(SyntaxCheckerRequest request) {
        totalCalls++;
        String code = request.getCode();

        // Use the isValidIfElse method from the SyntaxChecker class
        return SyntaxChecker.isValidIfElse(code); // Check the syntax using isValidIfElse
    }

    // Method to get the total number of API calls
    public int getTotalCalls() {
        return totalCalls;
    }
}