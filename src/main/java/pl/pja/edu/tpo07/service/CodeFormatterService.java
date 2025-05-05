package pl.pja.edu.tpo07.service;

import pl.pja.edu.tpo07.model.FormatResult;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.springframework.stereotype.Service;

@Service
public class CodeFormatterService {
    private final Formatter formatter = new Formatter();

    public FormatResult formatJavaCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return new FormatResult("", "", "Please enter Java code to format");
        }

        try {
            String formattedCode = formatter.formatSource(code);
            return new FormatResult(code, formattedCode);
        } catch (FormatterException e) {
            String errorMsg = "Formatting error: " + e.getMessage();
            return new FormatResult(code, code, errorMsg);
        } catch (Exception e) {
            String errorMsg = "Unexpected error: " + e.getMessage();
            return new FormatResult(code, code, errorMsg);
        }
    }
}