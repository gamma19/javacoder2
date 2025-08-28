import React, {
  useState,
  useEffect,
  useMemo,
  useCallback,
  useRef,
  useReducer,
} from "react";
import "./App.css";

function App() {
  const [sourceCode, setSourceCode] = useState(`public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}`);
  const [className, setClassName] = useState("HelloWorld");
  const [input, setInput] = useState("");
  const [output, setOutput] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [executionTime, setExecutionTime] = useState(0);
  const [codeHistory, setCodeHistory] = useState([]);
  const [showHistory, setShowHistory] = useState(false);
  const [showTemplates, setShowTemplates] = useState(false);
  const [outputFormat, setOutputFormat] = useState("both"); // "both", "raw", "toString"

  // Example of useRef to focus editor
  const sourceEditorRef = useRef(null);

  useEffect(() => {
    if (sourceEditorRef.current) {
      sourceEditorRef.current.focus();
    }
  }, []);

  //localstorage'dan kod yukleme
  useEffect(() => {
    const savedHistory = localStorage.getItem("javaCodeHistory");
    if (savedHistory) {
      setCodeHistory(JSON.parse(savedHistory));
    }
    // Send visit info to backend (fire-and-forget)
    fetch("http://localhost:8080/api/visit", {
      method: "POST",
      mode: "cors",
    }).catch(() => {});
  }, []);

  //kod geçmişini güncelleme
  useEffect(() => {
    localStorage.setItem("javaCodeHistory", JSON.stringify(codeHistory));
  }, [codeHistory]);

  const codeTemplates = [
    {
      name: "Hello World",
      className: "HelloWorld",
      code: `public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}`,
    },
    {
      name: "Sadece Static Metodlar",
      className: "MathUtils",
      code: `public class MathUtils {
    public static void main(String[] args) {
        // Static metodları çağır
        int toplam = topla(5, 3);
        int carpim = carp(4, 6);
        double us = usAl(2, 8);
        
        System.out.println("Toplam: " + toplam);
        System.out.println("Çarpım: " + carpim);
        System.out.println("2^8 = " + us);
    }
    
    // Static metodlar (sınıf seviyesinde fonksiyonlar)
    public static int topla(int a, int b) {
        return a + b;
    }
    
    public static int carp(int a, int b) {
        return a * b;
    }
    
    public static double usAl(double taban, int us) {
        return Math.pow(taban, us);
    }
}`,
    },
    {
      name: "Utility Class Pattern",
      className: "StringUtils",
      code: `public class StringUtils {
    public static void main(String[] args) {
        String metin = "Merhaba Java Programlama";
        
        // Utility metodları kullan
        System.out.println("Orijinal: " + metin);
        System.out.println("Ters çevrilmiş: " + tersCevir(metin));
        System.out.println("Kelime sayısı: " + kelimeSayisi(metin));
        System.out.println("Büyük harfler: " + buyukHarfeCevir(metin));
    }
    
    // Utility metodlar (sınıf seviyesinde)
    public static String tersCevir(String str) {
        return new StringBuilder(str).reverse().toString();
    }
    
    public static int kelimeSayisi(String str) {
        return str.split("\\s+").length;
    }
    
    public static String buyukHarfeCevir(String str) {
        return str.toUpperCase();
    }
}`,
    },
    {
      name: "Faktöriyel Hesaplama",
      className: "Factorial",
      code: `public class Factorial {
    public static void main(String[] args) {
        int n = 5;
        long result = calculateFactorial(n);
        System.out.println(n + "! = " + result);
    }
    
    public static long calculateFactorial(int n) {
        if (n <= 1) return 1;
        return n * calculateFactorial(n - 1);
    }
}`,
    },
    {
      name: "Dizi İşlemleri",
      className: "ArrayExample",
      code: `public class ArrayExample {
    public static void main(String[] args) {
        int[] numbers = {5, 2, 8, 1, 9, 3};
        
        System.out.println("Dizi elemanları:");
        for (int i = 0; i < numbers.length; i++) {
            System.out.print(numbers[i] + " ");
        }
        
        System.out.println("\\nEn büyük sayı: " + findMax(numbers));
        System.out.println("En küçük sayı: " + findMin(numbers));
    }
    
    public static int findMax(int[] arr) {
        int max = arr[0];
        for (int num : arr) {
            if (num > max) max = num;
        }
        return max;
    }
    
    public static int findMin(int[] arr) {
        int min = arr[0];
        for (int num : arr) {
            if (num < min) min = num;
        }
        return min;
    }
}`,
    },
    {
      name: "Scanner ile Giriş",
      className: "UserInput",
      code: `import java.util.Scanner;

public class UserInput {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Adınızı girin: ");
        String name = scanner.nextLine();
        
        System.out.print("Yaşınızı girin: ");
        int age = scanner.nextInt();
        
        System.out.println("Merhaba " + name + "! Yaşınız: " + age);
        
        scanner.close();
    }
}`,
    },
    {
      name: "String İşlemleri",
      className: "StringOperations",
      code: `public class StringOperations {
    public static void main(String[] args) {
        String text = "Merhaba Java Programlama!";
        
        System.out.println("Orijinal metin: " + text);
        System.out.println("Büyük harfler: " + text.toUpperCase());
        System.out.println("Küçük harfler: " + text.toLowerCase());
        System.out.println("Uzunluk: " + text.length());
        System.out.println("İlk 7 karakter: " + text.substring(0, 7));
        System.out.println("Java kelimesi var mı: " + text.contains("Java"));
    }
}`,
    },
    {
      name: "Lambda ve Stream",
      className: "LambdaExample",
      code: `import java.util.Arrays;
import java.util.List;

public class LambdaExample {
    public static void main(String[] args) {
        List<Integer> sayilar = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        System.out.println("Tüm sayılar: " + sayilar);
        
        // Lambda ile çift sayıları filtrele
        List<Integer> ciftSayilar = sayilar.stream()
            .filter(sayi -> sayi % 2 == 0)
            .toList();
        
        System.out.println("Çift sayılar: " + ciftSayilar);
        
        // Lambda ile sayıları 2 ile çarp
        List<Integer> ikiIleCarpilmis = sayilar.stream()
            .map(sayi -> sayi * 2)
            .toList();
        
        System.out.println("2 ile çarpılmış: " + ikiIleCarpilmis);
        
        // Lambda ile toplam hesapla
        int toplam = sayilar.stream()
            .reduce(0, (a, b) -> a + b);
        
        System.out.println("Toplam: " + toplam);
    }
}`,
    },
    {
      name: "Enum Kullanımı",
      className: "EnumExample",
      code: `public class EnumExample {
    // Enum tanımı
    enum Gun {
        PAZARTESI("Pazartesi", 1),
        SALI("Salı", 2),
        CARSAMBA("Çarşamba", 3),
        PERSEMBE("Perşembe", 4),
        CUMA("Cuma", 5),
        CUMARTESI("Cumartesi", 6),
        PAZAR("Pazar", 7);
        
        private final String turkceAd;
        private final int sira;
        
        Gun(String turkceAd, int sira) {
            this.turkceAd = turkceAd;
            this.sira = sira;
        }
        
        public String getTurkceAd() {
            return turkceAd;
        }
        
        public int getSira() {
            return sira;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Haftanın günleri:");
        
        for (Gun gun : Gun.values()) {
            System.out.println(gun.getSira() + ". " + gun.getTurkceAd());
        }
        
        // Belirli bir günü bul
        Gun bugun = Gun.CARSAMBA;
        System.out.println("\\nBugün: " + bugun.getTurkceAd() + " (Sıra: " + bugun.getSira() + ")");
    }
}`,
    },
  ];

  const executeCode = useCallback(async () => {
    setIsLoading(true);
    setOutput("");
    setError("");
    setExecutionTime(0);

    try {
      const response = await fetch(
        "http://localhost:8080/api/code/execute/java",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            sourceCode,
            className,
            input,
          }),
        }
      );

      const result = await response.json();

      if (result.success) {
        setOutput(result.output);
        setExecutionTime(result.executionTime);

        // Save to history if execution was successful
        saveToHistory({
          sourceCode,
          className,
          input,
          output: result.output,
          executionTime: result.executionTime,
          timestamp: new Date().toLocaleString(),
        });
      } else {
        setError(result.error);
      }
    } catch (err) {
      setError("Bağlantı hatası: " + err.message);
    } finally {
      setIsLoading(false);
    }
  }, [sourceCode, className, input, codeHistory]);

  const saveToHistory = useCallback(
    (codeData) => {
      const newHistory = [codeData, ...codeHistory.slice(0, 9)]; //sadece son 10 ogeyi tutma
      setCodeHistory(newHistory);
    },
    [codeHistory]
  );

  const loadFromHistory = useCallback((historyItem) => {
    setSourceCode(historyItem.sourceCode);
    setClassName(historyItem.className);
    setInput(historyItem.input);
    setOutput(historyItem.output);
    setExecutionTime(historyItem.executionTime);
    setError("");
    setShowHistory(false);
  }, []);

  const loadTemplate = useCallback((template) => {
    setSourceCode(template.code);
    setClassName(template.className);
    setInput("");
    setOutput("");
    setError("");
    setExecutionTime(0);
    setShowTemplates(false);
  }, []);

  const clearHistory = useCallback(() => {
    setCodeHistory([]);
    localStorage.removeItem("javaCodeHistory");
  }, []);

  const clearAll = useCallback(() => {
    setSourceCode(`public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}`);
    setClassName("HelloWorld");
    setInput("");
    setOutput("");
    setError("");
    setExecutionTime(0);
  }, []);

  const toStringOutput = useMemo(() => {
    if (!output) return "";
    return output
      .split("\n")
      .map((line) => `"${line.replace(/"/g, '\\"')}"`)
      .join(' + "\\n" + ');
  }, [output]);

  const copyToClipboard = useCallback((text) => {
    navigator.clipboard.writeText(text).then(() => {
      console.log("Copied to clipboard");
    });
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <h1>Java Code Executor</h1>
        <p>Java kodunuzu yazın ve çalıştırın</p>
      </header>

      <div className="container">
        <div className="input-section">
          <div className="form-group">
            <label htmlFor="className">Sınıf Adı:</label>
            <input
              type="text"
              id="className"
              value={className}
              onChange={(e) => setClassName(e.target.value)}
              placeholder="Sınıf adını girin"
            />
          </div>
          <div className="form-group">
            <label htmlFor="sourceCode">Java Kodu:</label>
            <textarea
              id="sourceCode"
              value={sourceCode}
              onChange={(e) => setSourceCode(e.target.value)}
              placeholder="Java kodunuzu buraya yazın..."
              rows="15"
              className="code-editor"
              ref={sourceEditorRef}
            />
          </div>
          {/*
          
          <div className="form-group">
            <label htmlFor="input">Giriş (opsiyonel):</label>
            <textarea
              id="input"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Program girişi (Scanner için)"
              rows="3"
            />
          </div>
          
          
           */}

          <div className="button-group">
            <button
              onClick={executeCode}
              disabled={isLoading}
              className="execute-btn"
            >
              {isLoading ? "Çalıştırılıyor..." : "Kodu Çalıştır"}
            </button>
            <button onClick={clearAll} className="clear-btn">
              Temizle
            </button>
            <button
              onClick={() => setShowHistory(!showHistory)}
              className="history-btn"
            >
              {showHistory ? "Geçmişi Gizle" : "Geçmiş"}
            </button>
            <button
              onClick={() => setShowTemplates(!showTemplates)}
              className="template-btn"
            >
              {showTemplates ? "Şablonları Gizle" : "Şablonlar"}
            </button>
          </div>
          {showTemplates && (
            <div className="templates-section">
              <h3>Kod Şablonları</h3>
              <div className="templates-grid">
                {codeTemplates.map((template, index) => (
                  <div key={index} className="template-item">
                    <h4>{template.name}</h4>
                    <p className="template-class">{template.className}</p>
                    <button
                      onClick={() => loadTemplate(template)}
                      className="load-template-btn"
                    >
                      Yükle
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}
          {showHistory && (
            <div className="history-section">
              <div className="history-header">
                <h3>Kod Geçmişi</h3>
                {codeHistory.length > 0 && (
                  <button onClick={clearHistory} className="clear-history-btn">
                    Geçmişi Temizle
                  </button>
                )}
              </div>
              {codeHistory.length === 0 ? (
                <p className="no-history">Henüz kod geçmişi yok.</p>
              ) : (
                <div className="history-list">
                  {codeHistory.map((item, index) => (
                    <div key={index} className="history-item">
                      <div className="history-info">
                        <span className="history-class">{item.className}</span>
                        <span className="history-time">{item.timestamp}</span>
                        <span className="history-duration">
                          {item.executionTime}ms
                        </span>
                      </div>
                      <div className="history-preview">
                        {item.sourceCode.substring(0, 100)}...
                      </div>
                      <button
                        onClick={() => loadFromHistory(item)}
                        className="load-history-btn"
                      >
                        Yükle
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>

        <div className="output-section">
          <div className="output-header">
            <h3>Sonuç:</h3>
            <div className="output-controls">
              <select
                value={outputFormat}
                onChange={(e) => setOutputFormat(e.target.value)}
                className="output-format-select"
              >
                <option value="both">Hem Çıktı Hem toString</option>
                <option value="raw">Sadece Çıktı</option>
                <option value="toString">Sadece toString</option>
              </select>
              {output && (
                <button
                  onClick={() => copyToClipboard(output)}
                  className="copy-btn"
                  title="Çıktıyı kopyala"
                >
                  📋
                </button>
              )}
            </div>
          </div>

          {executionTime > 0 && (
            <div className="execution-info">
              <span>Çalışma süresi: {executionTime}ms</span>
            </div>
          )}

          {output && (
            <div className="output">
              {(outputFormat === "both" || outputFormat === "raw") && (
                <div className="output-raw">
                  <h4>Çıktı:</h4>
                  <pre>{output}</pre>
                </div>
              )}

              {(outputFormat === "both" || outputFormat === "toString") && (
                <div className="output-tostring">
                  <h4>toString Formatı:</h4>
                  <pre>{toStringOutput}</pre>
                  <button
                    onClick={() => copyToClipboard(toStringOutput)}
                    className="copy-tostring-btn"
                  >
                    toString'i Kopyala
                  </button>
                </div>
              )}
            </div>
          )}

          {error && (
            <div className="error">
              <h4>Hata:</h4>
              <pre>{error}</pre>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;
