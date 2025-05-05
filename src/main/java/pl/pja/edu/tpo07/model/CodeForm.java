package pl.pja.edu.tpo07.model;

public class CodeForm {
    private String code = "";
    private String customId = "";
    private int storageDuration = 100;
    private String durationUnit = "seconds";

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getCustomId() { return customId; }
    public void setCustomId(String customId) { this.customId = customId; }
    public int getStorageDuration() { return storageDuration; }
    public void setStorageDuration(int storageDuration) { this.storageDuration = storageDuration; }
    public String getDurationUnit() { return durationUnit; }
    public void setDurationUnit(String durationUnit) { this.durationUnit = durationUnit; }
}