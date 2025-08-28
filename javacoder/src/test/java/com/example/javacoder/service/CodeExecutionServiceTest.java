package com.example.javacoder.service;

import com.example.javacoder.model.CodeExecutionRequest;
import com.example.javacoder.model.CodeExecutionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeExecutionServiceTest {

    @Mock
    private JavaCodeExecutor javaCodeExecutor;

    @InjectMocks
    private CodeExecutionService codeExecutionService;

    private CodeExecutionRequest validRequest;
    private CodeExecutionRequest invalidRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CodeExecutionRequest();
        validRequest.setSourceCode("public class Test { public static void main(String[] args) { System.out.println(\"Hello\"); } }");
        validRequest.setClassName("Test");
        validRequest.setInput("");

        invalidRequest = new CodeExecutionRequest();
        invalidRequest.setSourceCode("invalid java code");
        invalidRequest.setClassName("Invalid");
        invalidRequest.setInput("");
    }

    @Test
    void executeJavaCode_Success() {
        // Given
        CodeExecutionResponse expectedResponse = new CodeExecutionResponse();
        expectedResponse.setOutput("Hello");
        expectedResponse.setSuccess(true);
        expectedResponse.setExecutionTime(100L);
        expectedResponse.setClassName("Test");

        when(javaCodeExecutor.execute(validRequest)).thenReturn(expectedResponse);

        // When
        CodeExecutionResponse result = codeExecutionService.executeJavaCode(validRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Hello", result.getOutput());
        assertEquals(100L, result.getExecutionTime());
        assertEquals("Test", result.getClassName());
    }

    @Test
    void executeJavaCode_CompilationError() {
        // Given
        CodeExecutionResponse errorResponse = new CodeExecutionResponse();
        errorResponse.setError("Compilation failed");
        errorResponse.setSuccess(false);
        errorResponse.setClassName("Invalid");

        when(javaCodeExecutor.execute(invalidRequest)).thenReturn(errorResponse);

        // When
        CodeExecutionResponse result = codeExecutionService.executeJavaCode(invalidRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Compilation failed", result.getError());
        assertEquals("Invalid", result.getClassName());
    }

    @Test
    void executeJavaCode_Exception() {
        // Given
        when(javaCodeExecutor.execute(any())).thenThrow(new RuntimeException("Test exception"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            codeExecutionService.executeJavaCode(validRequest);
        });
    }

    @Test
    void getSupportedLanguages() {
        // When
        List<String> languages = codeExecutionService.getSupportedLanguages();

        // Then
        assertNotNull(languages);
        assertFalse(languages.isEmpty());
        assertTrue(languages.contains("Java"));
    }

    @Test
    void executeJavaCode_NullRequest() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            codeExecutionService.executeJavaCode(null);
        });
    }

    @Test
    void executeJavaCode_EmptySourceCode() {
        // Given
        CodeExecutionRequest emptyRequest = new CodeExecutionRequest();
        emptyRequest.setSourceCode("");
        emptyRequest.setClassName("Empty");
        emptyRequest.setInput("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            codeExecutionService.executeJavaCode(emptyRequest);
        });
    }

    @Test
    void executeJavaCode_EmptyClassName() {
        // Given
        CodeExecutionRequest noClassNameRequest = new CodeExecutionRequest();
        noClassNameRequest.setSourceCode("public class Test { }");
        noClassNameRequest.setClassName("");
        noClassNameRequest.setInput("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            codeExecutionService.executeJavaCode(noClassNameRequest);
        });
    }
} 