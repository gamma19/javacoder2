package com.example.javacoder.service;

import com.example.javacoder.model.CodeExecutionRequest;
import com.example.javacoder.model.CodeExecutionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JavaCodeExecutorTest {

    @InjectMocks
    private JavaCodeExecutor javaCodeExecutor;

    private CodeExecutionRequest validRequest;
    private CodeExecutionRequest invalidRequest;
    private CodeExecutionRequest infiniteLoopRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CodeExecutionRequest();
        validRequest.setSourceCode("public class Test { public static void main(String[] args) { System.out.println(\"Hello World\"); } }");
        validRequest.setClassName("Test");
        validRequest.setInput("");

        invalidRequest = new CodeExecutionRequest();
        invalidRequest.setSourceCode("public class Invalid { public static void main(String[] args) { System.out.println(; } }");
        invalidRequest.setClassName("Invalid");
        invalidRequest.setInput("");

        infiniteLoopRequest = new CodeExecutionRequest();
        infiniteLoopRequest.setSourceCode("public class Infinite { public static void main(String[] args) { while(true) { } } }");
        infiniteLoopRequest.setClassName("Infinite");
        infiniteLoopRequest.setInput("");
    }

    @Test
    void execute_ValidCode_Success() {
        // When
        CodeExecutionResponse result = javaCodeExecutor.execute(validRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("Hello World"));
        assertTrue(result.getExecutionTime() > 0);
        assertEquals("Test", result.getClassName());
        assertNull(result.getError());
    }

    @Test
    void execute_InvalidCode_CompilationError() {
        // When
        CodeExecutionResponse result = javaCodeExecutor.execute(invalidRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("compilation failed") || result.getError().contains("error"));
        assertEquals("Invalid", result.getClassName());
        assertNull(result.getOutput());
    }

    @Test
    void execute_WithInput_Success() {
        // Given
        CodeExecutionRequest requestWithInput = new CodeExecutionRequest();
        requestWithInput.setSourceCode("import java.util.Scanner; public class InputTest { public static void main(String[] args) { Scanner sc = new Scanner(System.in); String input = sc.nextLine(); System.out.println(\"Input: \" + input); } }");
        requestWithInput.setClassName("InputTest");
        requestWithInput.setInput("Test Input");

        // When
        CodeExecutionResponse result = javaCodeExecutor.execute(requestWithInput);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("Input: Test Input"));
    }

    @Test
    void execute_ComplexCalculation_Success() {
        // Given
        CodeExecutionRequest calculationRequest = new CodeExecutionRequest();
        calculationRequest.setSourceCode("public class Calculator { public static void main(String[] args) { int a = 10; int b = 5; System.out.println(\"Sum: \" + (a + b)); System.out.println(\"Product: \" + (a * b)); } }");
        calculationRequest.setClassName("Calculator");
        calculationRequest.setInput("");

        // When
        CodeExecutionResponse result = javaCodeExecutor.execute(calculationRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("Sum: 15"));
        assertTrue(result.getOutput().contains("Product: 50"));
    }

    @Test
    void execute_ArrayOperations_Success() {
        // Given
        CodeExecutionRequest arrayRequest = new CodeExecutionRequest();
        arrayRequest.setSourceCode("public class ArrayTest { public static void main(String[] args) { int[] arr = {1, 2, 3, 4, 5}; int sum = 0; for(int i : arr) sum += i; System.out.println(\"Array sum: \" + sum); } }");
        arrayRequest.setClassName("ArrayTest");
        arrayRequest.setInput("");

        // When
        CodeExecutionResponse result = javaCodeExecutor.execute(arrayRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("Array sum: 15"));
    }

    @Test
    void execute_ExceptionHandling_Success() {
        // Given
        CodeExecutionRequest exceptionRequest = new CodeExecutionRequest();
        exceptionRequest.setSourceCode("public class ExceptionTest { public static void main(String[] args) { try { int result = 10 / 0; } catch (Exception e) { System.out.println(\"Caught exception: \" + e.getMessage()); } } }");
        exceptionRequest.setClassName("ExceptionTest");
        exceptionRequest.setInput("");

        // When
        CodeExecutionResponse result = javaCodeExecutor.execute(exceptionRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("Caught exception"));
    }

    @Test
    void getSupportedLanguage_ReturnsJava() {
        // When
        String language = javaCodeExecutor.getSupportedLanguage();

        // Then
        assertEquals("Java", language);
    }

    @Test
    void isReady_ReturnsTrue() {
        // When
        boolean isReady = javaCodeExecutor.isReady();

        // Then
        assertTrue(isReady);
    }

    @Test
    void execute_NullRequest_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            javaCodeExecutor.execute(null);
        });
    }

    @Test
    void execute_EmptySourceCode_ThrowsException() {
        // Given
        CodeExecutionRequest emptyRequest = new CodeExecutionRequest();
        emptyRequest.setSourceCode("");
        emptyRequest.setClassName("Empty");
        emptyRequest.setInput("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            javaCodeExecutor.execute(emptyRequest);
        });
    }

    @Test
    void execute_EmptyClassName_ThrowsException() {
        // Given
        CodeExecutionRequest noClassNameRequest = new CodeExecutionRequest();
        noClassNameRequest.setSourceCode("public class Test { }");
        noClassNameRequest.setClassName("");
        noClassNameRequest.setInput("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            javaCodeExecutor.execute(noClassNameRequest);
        });
    }
} 