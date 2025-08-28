import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import App from "./App";

// Mock fetch globally
global.fetch = jest.fn();

describe("App Component", () => {
  beforeEach(() => {
    fetch.mockClear();
    localStorage.clear();
  });

  test("renders main heading", () => {
    render(<App />);
    expect(screen.getByText("Java Code Executor")).toBeInTheDocument();
  });

  test("renders subtitle", () => {
    render(<App />);
    expect(
      screen.getByText("Java kodunuzu yazın ve çalıştırın")
    ).toBeInTheDocument();
  });

  test("renders form elements", () => {
    render(<App />);

    expect(screen.getByLabelText("Sınıf Adı:")).toBeInTheDocument();
    expect(screen.getByLabelText("Java Kodu:")).toBeInTheDocument();
    expect(screen.getByLabelText("Giriş (opsiyonel):")).toBeInTheDocument();
    expect(screen.getByText("Kodu Çalıştır")).toBeInTheDocument();
    expect(screen.getByText("Temizle")).toBeInTheDocument();
  });

  test("has default code in textarea", () => {
    render(<App />);
    const codeTextarea = screen.getByLabelText("Java Kodu:");
    expect(codeTextarea.value).toContain("public class HelloWorld");
  });

  test("has default class name", () => {
    render(<App />);
    const classNameInput = screen.getByLabelText("Sınıf Adı:");
    expect(classNameInput.value).toBe("HelloWorld");
  });

  test("can change class name", () => {
    render(<App />);
    const classNameInput = screen.getByLabelText("Sınıf Adı:");

    fireEvent.change(classNameInput, { target: { value: "TestClass" } });
    expect(classNameInput.value).toBe("TestClass");
  });

  test("can change source code", () => {
    render(<App />);
    const codeTextarea = screen.getByLabelText("Java Kodu:");

    fireEvent.change(codeTextarea, {
      target: { value: "public class Test { }" },
    });
    expect(codeTextarea.value).toBe("public class Test { }");
  });

  test("can change input", () => {
    render(<App />);
    const inputTextarea = screen.getByLabelText("Giriş (opsiyonel):");

    fireEvent.change(inputTextarea, { target: { value: "test input" } });
    expect(inputTextarea.value).toBe("test input");
  });

  test("clear button resets form", () => {
    render(<App />);
    const classNameInput = screen.getByLabelText("Sınıf Adı:");
    const codeTextarea = screen.getByLabelText("Java Kodu:");
    const inputTextarea = screen.getByLabelText("Giriş (opsiyonel):");

    // Change values
    fireEvent.change(classNameInput, { target: { value: "TestClass" } });
    fireEvent.change(codeTextarea, {
      target: { value: "public class Test { }" },
    });
    fireEvent.change(inputTextarea, { target: { value: "test input" } });

    // Click clear button
    fireEvent.click(screen.getByText("Temizle"));

    // Check if values are reset
    expect(classNameInput.value).toBe("HelloWorld");
    expect(codeTextarea.value).toContain("public class HelloWorld");
    expect(inputTextarea.value).toBe("");
  });

  test("successful code execution", async () => {
    const mockResponse = {
      success: true,
      output: "Hello World",
      executionTime: 100,
      className: "Test",
    };

    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    });

    render(<App />);

    fireEvent.click(screen.getByText("Kodu Çalıştır"));

    await waitFor(() => {
      expect(screen.getByText("Hello World")).toBeInTheDocument();
    });

    expect(screen.getByText("Çalışma süresi: 100ms")).toBeInTheDocument();
  });

  test("failed code execution", async () => {
    const mockResponse = {
      success: false,
      error: "Compilation failed",
      className: "Invalid",
    };

    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    });

    render(<App />);

    fireEvent.click(screen.getByText("Kodu Çalıştır"));

    await waitFor(() => {
      expect(screen.getByText("Compilation failed")).toBeInTheDocument();
    });
  });

  test("network error handling", async () => {
    fetch.mockRejectedValueOnce(new Error("Network error"));

    render(<App />);

    fireEvent.click(screen.getByText("Kodu Çalıştır"));

    await waitFor(() => {
      expect(screen.getByText(/Bağlantı hatası/)).toBeInTheDocument();
    });
  });

  test("loading state during execution", async () => {
    fetch.mockImplementation(
      () => new Promise((resolve) => setTimeout(resolve, 100))
    );

    render(<App />);

    fireEvent.click(screen.getByText("Kodu Çalıştır"));

    expect(screen.getByText("Çalıştırılıyor...")).toBeInTheDocument();
  });

  test("history button toggles history section", () => {
    render(<App />);

    const historyButton = screen.getByText("Geçmiş");
    fireEvent.click(historyButton);

    expect(screen.getByText("Kod Geçmişi")).toBeInTheDocument();
    expect(screen.getByText("Henüz kod geçmişi yok.")).toBeInTheDocument();

    fireEvent.click(historyButton);
    expect(screen.queryByText("Kod Geçmişi")).not.toBeInTheDocument();
  });

  test("templates button toggles templates section", () => {
    render(<App />);

    const templatesButton = screen.getByText("Şablonlar");
    fireEvent.click(templatesButton);

    expect(screen.getByText("Kod Şablonları")).toBeInTheDocument();
    expect(screen.getByText("Hello World")).toBeInTheDocument();
    expect(screen.getByText("Faktöriyel Hesaplama")).toBeInTheDocument();

    fireEvent.click(templatesButton);
    expect(screen.queryByText("Kod Şablonları")).not.toBeInTheDocument();
  });

  test("can load template", () => {
    render(<App />);

    // Open templates
    fireEvent.click(screen.getByText("Şablonlar"));

    // Load a template
    fireEvent.click(screen.getByText("Yükle"));

    // Check if template is loaded
    const classNameInput = screen.getByLabelText("Sınıf Adı:");
    expect(classNameInput.value).toBe("HelloWorld");
  });

  test("output format selection", () => {
    render(<App />);

    const formatSelect = screen.getByDisplayValue("Hem Çıktı Hem toString");
    expect(formatSelect).toBeInTheDocument();

    fireEvent.change(formatSelect, { target: { value: "raw" } });
    expect(formatSelect.value).toBe("raw");
  });

  test("saves successful execution to history", async () => {
    const mockResponse = {
      success: true,
      output: "Hello World",
      executionTime: 100,
      className: "Test",
    };

    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    });

    render(<App />);

    fireEvent.click(screen.getByText("Kodu Çalıştır"));

    await waitFor(() => {
      expect(screen.getByText("Hello World")).toBeInTheDocument();
    });

    // Check if history is saved
    const savedHistory = JSON.parse(
      localStorage.getItem("javaCodeHistory") || "[]"
    );
    expect(savedHistory).toHaveLength(1);
    expect(savedHistory[0].output).toBe("Hello World");
    expect(savedHistory[0].className).toBe("Test");
  });

  test("does not save failed execution to history", async () => {
    const mockResponse = {
      success: false,
      error: "Compilation failed",
      className: "Invalid",
    };

    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    });

    render(<App />);

    fireEvent.click(screen.getByText("Kodu Çalıştır"));

    await waitFor(() => {
      expect(screen.getByText("Compilation failed")).toBeInTheDocument();
    });

    // Check that history is not saved
    const savedHistory = JSON.parse(
      localStorage.getItem("javaCodeHistory") || "[]"
    );
    expect(savedHistory).toHaveLength(0);
  });

  test("loads history from localStorage", () => {
    const mockHistory = [
      {
        sourceCode: "public class Test { }",
        className: "Test",
        input: "",
        output: "Hello",
        executionTime: 100,
        timestamp: "2023-01-01 12:00:00",
      },
    ];

    localStorage.setItem("javaCodeHistory", JSON.stringify(mockHistory));

    render(<App />);

    // Open history
    fireEvent.click(screen.getByText("Geçmiş"));

    expect(screen.getByText("Test")).toBeInTheDocument();
    expect(screen.getByText("2023-01-01 12:00:00")).toBeInTheDocument();
  });

  test("can load from history", () => {
    const mockHistory = [
      {
        sourceCode: "public class Test { }",
        className: "Test",
        input: "test input",
        output: "Hello",
        executionTime: 100,
        timestamp: "2023-01-01 12:00:00",
      },
    ];

    localStorage.setItem("javaCodeHistory", JSON.stringify(mockHistory));

    render(<App />);

    // Open history and load
    fireEvent.click(screen.getByText("Geçmiş"));
    fireEvent.click(screen.getByText("Yükle"));

    // Check if values are loaded
    expect(screen.getByLabelText("Sınıf Adı:").value).toBe("Test");
    expect(screen.getByLabelText("Giriş (opsiyonel):").value).toBe(
      "test input"
    );
  });

  test("can clear history", () => {
    const mockHistory = [
      {
        sourceCode: "public class Test { }",
        className: "Test",
        input: "",
        output: "Hello",
        executionTime: 100,
        timestamp: "2023-01-01 12:00:00",
      },
    ];

    localStorage.setItem("javaCodeHistory", JSON.stringify(mockHistory));

    render(<App />);

    // Open history and clear
    fireEvent.click(screen.getByText("Geçmiş"));
    fireEvent.click(screen.getByText("Geçmişi Temizle"));

    // Check if history is cleared
    expect(screen.getByText("Henüz kod geçmişi yok.")).toBeInTheDocument();
    expect(localStorage.getItem("javaCodeHistory")).toBeNull();
  });
});
