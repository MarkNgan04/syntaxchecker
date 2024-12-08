package com.syntaxchecker.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SyntaxCheckerController {

    @Autowired
    private SyntaxCheckerService syntaxCheckerService;

    // POST endpoint to check the syntax
    @PostMapping("/check")
    public ResponseEntity<?> checkSyntax(@RequestBody SyntaxCheckerRequest request) {
        try {
            // Validate that the code is not null or empty
            if (request.getCode() == null || request.getCode().trim().isEmpty()) {
                // Respond with 400 Bad Request if the code is null or empty
                return ResponseEntity.badRequest().body("Code snippet cannot be empty.");
            }

            // Call the service to check the syntax of the code
            boolean isValid = syntaxCheckerService.checkSyntax(request);

            if (isValid) {
                // Respond with 200 OK if syntax is valid
                return ResponseEntity.ok("Syntax is valid.");
            } else {
                // Respond with 422 Unprocessable Entity if syntax is invalid
                return ResponseEntity.unprocessableEntity().body("Syntax is invalid.");
            }
        } catch (Exception e) {
            // Respond with 500 Internal Server Error in case of exceptions
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // GET endpoint to get API status
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        int totalCalls = syntaxCheckerService.getTotalCalls();
        return ResponseEntity.ok("{\"totalCalls\": " + totalCalls + "}");
    }
}
