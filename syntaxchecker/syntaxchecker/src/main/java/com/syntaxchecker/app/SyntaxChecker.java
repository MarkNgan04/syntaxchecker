package com.syntaxchecker.app;

import java.util.*;

public class SyntaxChecker {

    // Method to check if the input is a valid if-else statement
    public static boolean isValidIfElse(String input) {
        input = input.trim(); // Remove leading/trailing spaces

        List<String> tokens = tokenize(input);
        if (tokens.isEmpty()) {
            return false;
        }

        return validateNestedIfElse(tokens);
    }

    // Recursive method to validate nested if-else structures
    private static boolean validateNestedIfElse(List<String> tokens) {
        try {
            if (tokens.isEmpty() || !tokens.get(0).equals("if")) {
                System.out.println("Error: 'if' keyword expected at the beginning.");
                return false; // 'if' expected
            }

            // Check for the opening parenthesis
            if (tokens.size() < 2 || !tokens.get(1).equals("(")) {
                System.out.println("Error: '(' expected after 'if'.");
                return false; // '(' expected after 'if'
            }

            // Extract and validate the condition in parentheses
            int conditionEndIndex = findClosingParenthesis(tokens, 1);
            if (conditionEndIndex == -1) {
                System.out.println("Error: ')' expected to close condition.");
                return false; // ')' expected
            }

            String condition = String.join(" ", tokens.subList(2, conditionEndIndex));
            String conditionError = isValidCondition(condition);
            if (conditionError != null) {
                System.out.println("Error in condition: " + conditionError);
                return false; // Invalid condition
            }

            // Extract and validate the 'if' block
            int blockStartIndex = conditionEndIndex + 1;
            if (blockStartIndex >= tokens.size() || !tokens.get(blockStartIndex).equals("{")) {
                // Allow for non-block single statements
                int nextIndex = blockStartIndex;
                if (nextIndex < tokens.size() && !tokens.get(nextIndex).equals("{")) {
                    // Validate the single statement (non-block)
                    List<String> ifStatementTokens = tokens.subList(blockStartIndex, tokens.size());
                    if (!validateSingleStatement(ifStatementTokens)) {
                        System.out.println("Error: Invalid content in 'if' statement.");
                        return false; // Invalid content in 'if' statement
                    }
                    return true; // Return after validating the single statement
                } else {
                    System.out.println("Error: '{' expected after condition.");
                    return false; // '{' expected after condition
                }
            }

            int blockEndIndex = findClosingBrace(tokens, blockStartIndex);
            if (blockEndIndex == -1) {
                System.out.println("Error: '}' expected to close 'if' block.");
                return false; // '}' expected for 'if' block
            }

            List<String> ifBlockTokens = tokens.subList(blockStartIndex + 1, blockEndIndex);
            if (!validateBlockContent(ifBlockTokens)) {
                System.out.println("Error: Invalid content in 'if' block.");
                return false; // Invalid content in 'if' block
            }

            // Check for an 'else' block
            if (blockEndIndex + 1 < tokens.size()) {
                String nextToken = tokens.get(blockEndIndex + 1);

                // If the next token is not 'else', the syntax is invalid
                if (!nextToken.equals("else")) {
                    System.out.println("Error: After an 'if' block, only 'else' is allowed.");
                    return false; // Only 'else' is valid after an 'if' block
                }

                // Proceed with the validation for the 'else' block
                int elseStartIndex = blockEndIndex + 2;

                if (elseStartIndex >= tokens.size()) {
                    System.out.println("Error: 'else' block expected but not found.");
                    return false; // 'else' block expected
                }

                // Check if the else block starts with a '{'
                if (tokens.get(elseStartIndex).equals("{")) {
                    int elseBlockEndIndex = findClosingBrace(tokens, elseStartIndex);
                    if (elseBlockEndIndex == -1) {
                        System.out.println("Error: '}' expected to close 'else' block.");
                        return false; // '}' expected for 'else' block
                    }
                    List<String> elseBlockTokens = tokens.subList(elseStartIndex + 1, elseBlockEndIndex);
                    if (!validateBlockContent(elseBlockTokens)) {
                        System.out.println("Error: Invalid content in 'else' block.");
                        return false; // Invalid content in 'else' block
                    }
                } else {
                    // Non-block 'else' statement
                    List<String> elseStatementTokens = tokens.subList(elseStartIndex, tokens.size());
                    if (!validateSingleStatement(elseStatementTokens)) {
                        System.out.println("Error: Invalid content in 'else' statement.");
                        return false; // Invalid content in 'else' statement
                    }
                }
            }
            return true; // Valid 'if-else' statement
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Index out of bounds: " + e.getMessage());
            return false;
        } catch (NullPointerException e) {
            System.out.println("Null pointer encountered: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }

    // Helper method to tokenize the input string
    private static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inString = false;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            // Toggle string state when encountering quotes
            if (ch == '\"') {
                inString = !inString;
                currentToken.append(ch); // Add the quote character to the current token
                continue;
            }

            // If inside a string, continue accumulating characters
            if (inString) {
                currentToken.append(ch);
                continue;
            }

            // Check for whitespace
            if (Character.isWhitespace(ch)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // Clear current token
                }
                continue; // Skip whitespace
            }

            // Handle delimiters like (), {}, ;
            if ("(){};".indexOf(ch) != -1) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // Clear current token
                }
                tokens.add(Character.toString(ch)); // Add delimiter as a token
                continue; // Skip to the next character
            }

            // Handle operators like &&, ||, etc.
            if (ch == '&' || ch == '|') {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // Clear current token
                }

                if (i + 1 < input.length() && input.charAt(i + 1) == ch) {
                    tokens.add("" + ch + ch); // Add && or ||
                    i++; // Skip the next character
                } else {
                    tokens.add(Character.toString(ch)); // Add single & or |
                }
                continue;
            }

            // Handle other characters (build the current token)
            currentToken.append(ch);
        }

        // Add the last token if exists
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString()); // Add the last token
        }

        return tokens;
    }


    // Method to check if the condition is valid
    public static String isValidCondition(String condition) {
        if (condition.isEmpty()) {
            return "Condition can't be empty.";
        }

        Stack<Character> stack = new Stack<>();

        // Check for balanced parentheses
        for (char ch : condition.toCharArray()) {
            if (ch == '(') {
                stack.push(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) {
                    return "Unbalanced parentheses.";
                }
                stack.pop();
            }
        }

        List<String> tokens = tokenize(condition);

        // Check for invalid '=' usage
        if (tokens.contains("=") && !tokens.contains("==")) {
            return "'=' should be '==' for comparison.";
        }

        String firstToken = tokens.get(0); // Get the first token

        // Ensure the first token is not an operator
        if (isComparisonOperator(firstToken) || isLogicalOperator(firstToken)) {
            return "Operators can't be leading in the condition.";
        }

        // Loop through tokens and check for adjacent operators and operands
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i).trim(); // Remove any surrounding whitespace

            // Check if the token is neither an operand nor an operator
            if (!isValidOperand(token) && !isComparisonOperator(token) && !isLogicalOperator(token)) {
                return "Invalid condition. '" + token + "' is neither a valid operand nor a valid operator.";
            }

            // Check for adjacent operands (identifiers, numeric, or boolean literals)
            if (i > 0) {
                String prevToken = tokens.get(i - 1).trim();

                if (isValidOperand(prevToken) && isValidOperand(token)) {
                    return "Adjacent operands without an operator in between.";
                }
            }
        }

        return null; // No errors found
    }

    private static boolean isValidOperand(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*|[0-9]+|true|false");
    }

    private static boolean isComparisonOperator(String token) {
        return token.equals("==") || token.equals("!=") || token.equals(">") || token.equals("<");
    }

    private static boolean isLogicalOperator(String token) {
        return token.equals("&&") || token.equals("||");
    }

    // Method to find the index of the closing parenthesis in the condition
    private static int findClosingParenthesis(List<String> tokens, int start) {
        Stack<String> stack = new Stack<>();

        for (int i = start; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                stack.pop();
                if (stack.isEmpty()) {
                    return i;
                }
            }
        }

        return -1; // Closing parenthesis not found
    }

    private static int findClosingBrace(List<String> tokens, int start) {
        Stack<String> stack = new Stack<>();

        for (int i = start; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("{")) {
                stack.push(token);
            } else if (token.equals("}")) {
                stack.pop();
                if (stack.isEmpty()) {
                    return i;
                }
            }
        }

        return -1; // Closing brace not found
    }

    // Validate single statements (non-block statements)
    private static boolean validateSingleStatement(List<String> tokens) {
        if (tokens.size() < 1) {
            System.out.println("Error: No content found in the statement.");
            return false;
        }

        // Further validation of the single statement content can be added here
        return true; // Placeholder for further validation
    }

    // Validate block content
    private static boolean validateBlockContent(List<String> blockTokens) {
        if (blockTokens.size() < 1) {
            return false; // Block is empty
        }

        // Further block content validation can be added here
        return true; // Placeholder for block validation
    }
}