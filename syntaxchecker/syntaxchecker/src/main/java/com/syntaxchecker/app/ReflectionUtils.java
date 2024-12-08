package com.syntaxchecker.app;

import java.lang.reflect.Method;

public class ReflectionUtils {

    // Method to invoke the private 'isValidIfElse' method via reflection
    public static boolean invokeIsValidIfElse(String code) {
        try {
            // Get the SyntaxChecker class and create an instance
            Class<?> syntaxCheckerClass = Class.forName("com.syntaxchecker.app.SyntaxChecker");
            Object syntaxCheckerInstance = syntaxCheckerClass.getDeclaredConstructor().newInstance();

            // Get the 'isValidIfElse' method from SyntaxChecker class
            Method isValidIfElseMethod = syntaxCheckerClass.getDeclaredMethod("isValidIfElse", String.class);
            isValidIfElseMethod.setAccessible(true); // Make the private method accessible

            // Invoke the method with the provided code
            return (boolean) isValidIfElseMethod.invoke(syntaxCheckerInstance, code);

        } catch (Exception e) {
            e.printStackTrace();  // Handle reflection or instantiation exceptions
            return false;
        }
    }
}