package com.example.javacoder.controller;

import com.example.javacoder.model.CodeExecutionRequest;
import com.example.javacoder.model.CodeExecutionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class CodeExecutionControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        assertNotNull(webApplicationContext);
    }

    @Test
    void executeJavaCode_ValidCode_Success() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setSourceCode("public class Test { public static void main(String[] args) { System.out.println(\"Hello World\"); } }");
        request.setClassName("Test");
        request.setInput("");

        // When & Then
        mockMvc.perform(post("/api/code/execute/java")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.output").value("Hello World"))
                .andExpect(jsonPath("$.className").value("Test"))
                .andExpect(jsonPath("$.executionTime").isNumber());
    }

    @Test
    void executeJavaCode_InvalidCode_Error() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setSourceCode("public class Invalid { public static void main(String[] args) { System.out.println(; } }");
        request.setClassName("Invalid");
        request.setInput("");

        // When & Then
        mockMvc.perform(post("/api/code/execute/java")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.className").value("Invalid"));
    }

    @Test
    void executeJavaCode_WithInput_Success() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setSourceCode("import java.util.Scanner; public class InputTest { public static void main(String[] args) { Scanner sc = new Scanner(System.in); String input = sc.nextLine(); System.out.println(\"Input: \" + input); } }");
        request.setClassName("InputTest");
        request.setInput("Test Input");

        // When & Then
        mockMvc.perform(post("/api/code/execute/java")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.output").value("Input: Test Input"));
    }

    @Test
    void executeJavaCode_ComplexCalculation_Success() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setSourceCode("public class Calculator { public static void main(String[] args) { int a = 10; int b = 5; System.out.println(\"Sum: \" + (a + b)); System.out.println(\"Product: \" + (a * b)); } }");
        request.setClassName("Calculator");
        request.setInput("");

        // When & Then
        mockMvc.perform(post("/api/code/execute/java")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.output").value(org.hamcrest.Matchers.containsString("Sum: 15")))
                .andExpect(jsonPath("$.output").value(org.hamcrest.Matchers.containsString("Product: 50")));
    }

    @Test
    void getSupportedLanguages_Success() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // When & Then
        mockMvc.perform(get("/api/code/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Java"));
    }

    @Test
    void healthCheck_Success() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // When & Then
        mockMvc.perform(get("/api/code/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Java Code Executor is running!"));
    }

    @Test
    void executeJavaCode_EmptyRequest_BadRequest() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setSourceCode("");
        request.setClassName("");
        request.setInput("");

        // When & Then
        mockMvc.perform(post("/api/code/execute/java")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void executeJavaCode_MissingFields_BadRequest() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        CodeExecutionRequest request = new CodeExecutionRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/code/execute/java")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void executeJavaCode_InvalidJson_BadRequest() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // When & Then
        mockMvc.perform(post("/api/code/execute/java")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void executeJavaCode_ArrayOperations_Success() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setSourceCode("public class ArrayTest { public static void main(String[] args) { int[] arr = {1, 2, 3, 4, 5}; int sum = 0; for(int i : arr) sum += i; System.out.println(\"Array sum: \" + sum); } }");
        request.setClassName("ArrayTest");
        request.setInput("");

        // When & Then
        mockMvc.perform(post("/api/code/execute/java")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.output").value("Array sum: 15"));
    }

    @Test
    void executeJavaCode_ExceptionHandling_Success() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setSourceCode("public class ExceptionTest { public static void main(String[] args) { try { int result = 10 / 0; } catch (Exception e) { System.out.println(\"Caught exception: \" + e.getMessage()); } } }");
        request.setClassName("ExceptionTest");
        request.setInput("");

        // When & Then
        mockMvc.perform(post("/api/code/execute/java")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.output").value(org.hamcrest.Matchers.containsString("Caught exception")));
    }
} 