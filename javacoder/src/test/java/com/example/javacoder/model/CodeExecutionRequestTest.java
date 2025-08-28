package com.example.javacoder.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeExecutionRequestTest {

    @Test
    void testDefaultConstructor() {
        // When
        CodeExecutionRequest request = new CodeExecutionRequest();

        // Then
        assertNotNull(request);
        assertNull(request.getSourceCode());
        assertNull(request.getClassName());
        assertNull(request.getInput());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        String sourceCode = "public class Test { }";
        String className = "Test";
        String input = "test input";

        // When
        CodeExecutionRequest request = new CodeExecutionRequest(sourceCode, className, input);

        // Then
        assertNotNull(request);
        assertEquals(sourceCode, request.getSourceCode());
        assertEquals(className, request.getClassName());
        assertEquals(input, request.getInput());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        CodeExecutionRequest request = new CodeExecutionRequest();
        String sourceCode = "public class Test { }";
        String className = "Test";
        String input = "test input";

        // When
        request.setSourceCode(sourceCode);
        request.setClassName(className);
        request.setInput(input);

        // Then
        assertEquals(sourceCode, request.getSourceCode());
        assertEquals(className, request.getClassName());
        assertEquals(input, request.getInput());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        CodeExecutionRequest request1 = new CodeExecutionRequest("code1", "class1", "input1");
        CodeExecutionRequest request2 = new CodeExecutionRequest("code1", "class1", "input1");
        CodeExecutionRequest request3 = new CodeExecutionRequest("code2", "class2", "input2");

        // Then
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        CodeExecutionRequest request = new CodeExecutionRequest("test code", "TestClass", "test input");

        // When
        String result = request.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("test code"));
        assertTrue(result.contains("TestClass"));
        assertTrue(result.contains("test input"));
    }

    @Test
    void testEqualsWithNull() {
        // Given
        CodeExecutionRequest request = new CodeExecutionRequest("code", "class", "input");

        // Then
        assertNotEquals(null, request);
    }

    @Test
    void testEqualsWithDifferentClass() {
        // Given
        CodeExecutionRequest request = new CodeExecutionRequest("code", "class", "input");
        Object differentObject = "string";

        // Then
        assertNotEquals(request, differentObject);
    }

    @Test
    void testEqualsWithSameObject() {
        // Given
        CodeExecutionRequest request = new CodeExecutionRequest("code", "class", "input");

        // Then
        assertEquals(request, request);
    }

    @Test
    void testEqualsWithNullFields() {
        // Given
        CodeExecutionRequest request1 = new CodeExecutionRequest(null, null, null);
        CodeExecutionRequest request2 = new CodeExecutionRequest(null, null, null);

        // Then
        assertEquals(request1, request2);
    }

    @Test
    void testEqualsWithDifferentSourceCode() {
        // Given
        CodeExecutionRequest request1 = new CodeExecutionRequest("code1", "class", "input");
        CodeExecutionRequest request2 = new CodeExecutionRequest("code2", "class", "input");

        // Then
        assertNotEquals(request1, request2);
    }

    @Test
    void testEqualsWithDifferentClassName() {
        // Given
        CodeExecutionRequest request1 = new CodeExecutionRequest("code", "class1", "input");
        CodeExecutionRequest request2 = new CodeExecutionRequest("code", "class2", "input");

        // Then
        assertNotEquals(request1, request2);
    }

    @Test
    void testEqualsWithDifferentInput() {
        // Given
        CodeExecutionRequest request1 = new CodeExecutionRequest("code", "class", "input1");
        CodeExecutionRequest request2 = new CodeExecutionRequest("code", "class", "input2");

        // Then
        assertNotEquals(request1, request2);
    }
} 