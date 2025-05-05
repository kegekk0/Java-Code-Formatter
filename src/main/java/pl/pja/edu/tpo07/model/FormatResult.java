package pl.pja.edu.tpo07.model;

public class FormatResult {
    private String originalCode;
    private String formattedCode;
    private String error;

    public FormatResult(String originalCode, String formattedCode) {
        this.originalCode = originalCode;
        this.formattedCode = formattedCode;
    }

    public FormatResult(String originalCode, String formattedCode, String error) {
        this.originalCode = originalCode;
        this.formattedCode = formattedCode;
        this.error = error;
    }

    public String getOriginalCode() { return originalCode; }
    public String getFormattedCode() { return formattedCode; }
    public String getError() { return error; }
    public void setOriginalCode(String originalCode) { this.originalCode = originalCode; }
    public void setFormattedCode(String formattedCode) { this.formattedCode = formattedCode; }
    public void setError(String error) { this.error = error; }
}