package com.example.javacoder.service;

import com.example.javacoder.model.CodeExecutionRequest;
import com.example.javacoder.model.CodeExecutionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JavaCodeExecutor extends AbstractCodeExecutor {
    
    private static final int TIMEOUT_SECONDS = 10;
    private static final String JAVA_COMPILER = "javac";
    private static final String JAVA_RUNTIME = "java";
    
    @Override
    protected CodeExecutionResponse doExecute(CodeExecutionRequest request) throws Exception {
        Path javaFile = null;
        Path classFile = null;
        Path uniqueDir = null;
        
        try {
            // Java dosyasını oluşturma
            javaFile = createTempFile(request.getClassName(), JAVA_EXTENSION);
            log.debug("Java dosyası oluşturuldu: {}", javaFile);
            
            // Benzersiz dizini alma
            uniqueDir = javaFile.getParent();
            log.debug("Benzersiz dizin: {}", uniqueDir);
            
            Files.write(javaFile, request.getSourceCode().getBytes());
            log.debug("Kod dosyaya yazıldı");
            
            // Kodu derleme
            compileJavaCode(javaFile);
            log.debug("Kod başarıyla derlendi");
            
            // Class dosyası yolunu alma
            classFile = javaFile.resolveSibling(request.getClassName() + CLASS_EXTENSION);
            log.debug("Class dosyası yolu: {}", classFile);
            
            // Kodu çalıştırma
            String output = runJavaCode(request.getClassName(), request.getInput(), uniqueDir.toString());
            log.debug("Kod başarıyla çalıştırıldı");
            
            return new CodeExecutionResponse(output, null, 0, true, request.getClassName());
            
        } catch (Exception e) {
            log.error("Kod çalıştırma sırasında hata: {}", e.getMessage(), e);
            throw e;
        } finally {
            // Geçici dosyaları temizle
            cleanupTempFiles(javaFile, classFile);
            // Benzersiz dizini de temizle
            if (uniqueDir != null) {
                cleanupDirectory(uniqueDir);
            }
        }
    }
    
    /**
     * Java kodunu derler
     * @param javaFile Derlenecek Java dosyası
     * @throws Exception Derleme hatası
     */
    private void compileJavaCode(Path javaFile) throws Exception {
        if (javaFile == null) {
            throw new IllegalArgumentException("Java dosyası yolu null olamaz");
        }
        
        ProcessBuilder compiler = new ProcessBuilder(JAVA_COMPILER, javaFile.toString());
        compiler.redirectErrorStream(true);
        
        log.debug("Derleme komutu: {} {}", JAVA_COMPILER, javaFile);
        Process process = compiler.start();
        
        // Derleme çıktısını oku
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        // Timeout ile bekle
        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        if (!finished) {
            process.destroyForcibly();
            throw new Exception("Derleme zaman aşımına uğradı");
        }
        
        if (process.exitValue() != 0) {
            throw new Exception("Derleme hatası:\n" + output.toString());
        }
        
        log.debug("Derleme başarılı");
    }
    
    /**
     * Java kodunu çalıştırır
     * @param className Çalıştırılacak sınıf adı
     * @param input Program girişi
     * @param classPath Class path
     * @return Program çıktısı
     * @throws Exception Çalıştırma hatası
     */
    private String runJavaCode(String className, String input, String classPath) throws Exception {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("Sınıf adı boş olamaz");
        }
        
        ProcessBuilder runner = new ProcessBuilder(JAVA_RUNTIME, "-cp", classPath, className);
        runner.redirectErrorStream(true);
        
        log.debug("Çalıştırma komutu: {} -cp {} {}", JAVA_RUNTIME, classPath, className);
        Process process = runner.start();
        
        // Giriş verisini yaz
        if (input != null && !input.trim().isEmpty()) {
            process.getOutputStream().write(input.getBytes());
            process.getOutputStream().close();
            log.debug("Giriş verisi yazıldı: {}", input);
        }
        
        // Çıktıyı oku
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        // Timeout ile bekle
        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        if (!finished) {
            process.destroyForcibly();
            throw new Exception("Çalıştırma zaman aşımına uğradı");
        }
        
        if (process.exitValue() != 0) {
            throw new Exception("Çalıştırma hatası:\n" + output.toString());
        }
        
        log.debug("Çalıştırma başarılı, çıktı: {}", output.toString());
        return output.toString();
    }
    
    @Override
    public String getSupportedLanguage() {
        return "Java";
    }
} 