package com.example.javacoder.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeExecutionResponseTest {

    @Test
    void testDefaultConstructor() {
        // When
        CodeExecutionResponse response = new CodeExecutionResponse();

        // Then
        assertNotNull(response);
        assertNull(response.getOutput());
        assertNull(response.getError());
        assertEquals(0, response.getExecutionTime());
        assertFalse(response.isSuccess());
        assertNull(response.getClassName());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        String output = "Hello World";
        String error = null;
        long executionTime = 100L;
        boolean success = true;
        String className = "Test";

        // When
        CodeExecutionResponse response = new CodeExecutionResponse(output, error, executionTime, success, className);

        // Then
        assertNotNull(response);
        assertEquals(output, response.getOutput());
        assertEquals(error, response.getError());
        assertEquals(executionTime, response.getExecutionTime());
        assertEquals(success, response.isSuccess());
        assertEquals(className, response.getClassName());
    }

    @Test
    void testAllArgsConstructorWithError() {
        // Given
        String output = null;
        String error = "Compilation failed";
        long executionTime = 0L;
        boolean success = false;
        String className = "Invalid";

        // When
        CodeExecutionResponse response = new CodeExecutionResponse(output, error, executionTime, success, className);

        // Then
        assertNotNull(response);
        assertEquals(output, response.getOutput());
        assertEquals(error, response.getError());
        assertEquals(executionTime, response.getExecutionTime());
        assertEquals(success, response.isSuccess());
        assertEquals(className, response.getClassName());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        CodeExecutionResponse response = new CodeExecutionResponse();
        String output = "Test output";
        String error = "Test error";
        long executionTime = 150L;
        boolean success = true;
        String className = "TestClass";

        // When
        response.setOutput(output);
        response.setError(error);
        response.setExecutionTime(executionTime);
        response.setSuccess(success);
        response.setClassName(className);

        // Then
        assertEquals(output, response.getOutput());
        assertEquals(error, response.getError());
        assertEquals(executionTime, response.getExecutionTime());
        assertEquals(success, response.isSuccess());
        assertEquals(className, response.getClassName());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        CodeExecutionResponse response1 = new CodeExecutionResponse("output1", "error1", 100L, true, "class1");
        CodeExecutionResponse response2 = new CodeExecutionResponse("output1", "error1", 100L, true, "class1");
        CodeExecutionResponse response3 = new CodeExecutionResponse("output2", "error2", 200L, false, "class2");

        // Then
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        CodeExecutionResponse response = new CodeExecutionResponse("test output", "test error", 100L, true, "TestClass");

        // When
        String result = response.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("test output"));
        assertTrue(result.contains("test error"));
        assertTrue(result.contains("100"));
        assertTrue(result.contains("true"));
        assertTrue(result.contains("TestClass"));
    }

    @Test
    void testEqualsWithNull() {
        // Given
        CodeExecutionResponse response = new CodeExecutionResponse("output", "error", 100L, true, "class");

        // Then
        assertNotEquals(null, response);
    }

    @Test
    void testEqualsWithDifferentClass() {
        // Given
        CodeExecutionResponse response = new CodeExecutionResponse("output", "error", 100L, true, "class");
        Object differentObject = "string";

        // Then
        assertNotEquals(response, differentObject);
    }

    @Test
    void testEqualsWithSameObject() {
        // Given
        CodeExecutionResponse response = new CodeExecutionResponse("output", "error", 100L, true, "class");

        // Then
        assertEquals(response, response);
    }

    @Test
    void testEqualsWithNullFields() {
        // Given
        CodeExecutionResponse response1 = new CodeExecutionResponse(null, null, 0L, false, null);
        CodeExecutionResponse response2 = new CodeExecutionResponse(null, null, 0L, false, null);

        // Then
        assertEquals(response1, response2);
    }

    @Test
    void testEqualsWithDifferentOutput() {
        // Given
        CodeExecutionResponse response1 = new CodeExecutionResponse("output1", "error", 100L, true, "class");
        CodeExecutionResponse response2 = new CodeExecutionResponse("output2", "error", 100L, true, "class");

        // Then
        assertNotEquals(response1, response2);
    }

    @Test
    void testEqualsWithDifferentError() {
        // Given
        CodeExecutionResponse response1 = new CodeExecutionResponse("output", "error1", 100L, true, "class");
        CodeExecutionResponse response2 = new CodeExecutionResponse("output", "error2", 100L, true, "class");

        // Then
        assertNotEquals(response1, response2);
    }

    @Test
    void testEqualsWithDifferentExecutionTime() {
        // Given
        CodeExecutionResponse response1 = new CodeExecutionResponse("output", "error", 100L, true, "class");
        CodeExecutionResponse response2 = new CodeExecutionResponse("output", "error", 200L, true, "class");

        // Then
        assertNotEquals(response1, response2);
    }

    @Test
    void testEqualsWithDifferentSuccess() {
        // Given
        CodeExecutionResponse response1 = new CodeExecutionResponse("output", "error", 100L, true, "class");
        CodeExecutionResponse response2 = new CodeExecutionResponse("output", "error", 100L, false, "class");

        // Then
        assertNotEquals(response1, response2);
    }

    @Test
    void testEqualsWithDifferentClassName() {
        // Given
        CodeExecutionResponse response1 = new CodeExecutionResponse("output", "error", 100L, true, "class1");
        CodeExecutionResponse response2 = new CodeExecutionResponse("output", "error", 100L, true, "class2");

        // Then
        assertNotEquals(response1, response2);
    }

    @Test
    void testSuccessResponse() {
        // Given
        CodeExecutionResponse response = new CodeExecutionResponse();
        response.setOutput("Success output");
        response.setSuccess(true);
        response.setExecutionTime(50L);
        response.setClassName("SuccessClass");

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(response.getOutput());
        assertNull(response.getError());
        assertTrue(response.getExecutionTime() > 0);
    }

    @Test
    void testErrorResponse() {
        // Given
        CodeExecutionResponse response = new CodeExecutionResponse();
        response.setError("Compilation error");
        response.setSuccess(false);
        response.setExecutionTime(0L);
        response.setClassName("ErrorClass");

        // Then
        assertFalse(response.isSuccess());
        assertNotNull(response.getError());
        assertNull(response.getOutput());
        assertEquals(0L, response.getExecutionTime());
    }
} 