package pl.pja.edu.tpo07.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import pl.pja.edu.tpo07.model.*;
import pl.pja.edu.tpo07.service.CodeFormatterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/format")
public class FormatterController {
    private final CodeFormatterService codeFormatterService;
    private static final String STORAGE_DIR = "code_storage";

    public FormatterController(CodeFormatterService codeFormatterService) {
        this.codeFormatterService = codeFormatterService;
        new File(STORAGE_DIR).mkdirs();
    }

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("codeForm", new CodeForm());
        return "index";
    }

    @PostMapping
    public String formatCode(@ModelAttribute CodeForm codeForm, Model model) {
        FormatResult result = codeFormatterService.formatJavaCode(codeForm.getCode());

        if (codeForm.getCustomId() != null && !codeForm.getCustomId().isEmpty()) {
            LocalDateTime expirationTime = calculateExpirationTime(
                    codeForm.getStorageDuration(),
                    codeForm.getDurationUnit()
            );

            StoredCode storedCode = new StoredCode(
                    codeForm.getCustomId(),
                    result.getFormattedCode(),
                    expirationTime
            );

            storeCode(storedCode);
        }

        model.addAttribute("result", result);
        return "result";
    }

    @GetMapping("/saved/{id}")
    public String showSavedCode(@PathVariable String id, Model model) {
        StoredCode storedCode = loadCode(id);
        if (storedCode == null || storedCode.isExpired()) {
            model.addAttribute("error", "Code not found or has expired");
            return "error";
        }

        model.addAttribute("code", storedCode.getCode());
        return "saved_code";
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredCodes() {
        File storageDir = new File(STORAGE_DIR);
        for (File file : Objects.requireNonNull(storageDir.listFiles())) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                StoredCode storedCode = (StoredCode) ois.readObject();
                if (storedCode.isExpired()) {
                    file.delete();
                }
            } catch (IOException | ClassNotFoundException e) {
                file.delete();
            }
        }
    }

    private LocalDateTime calculateExpirationTime(int duration, String unit) {
        return switch (unit.toLowerCase()) {
            case "seconds" -> LocalDateTime.now().plusSeconds(Math.max(10, Math.min(duration, 90*24*60*60)));
            case "minutes" -> LocalDateTime.now().plusMinutes(Math.max(1, Math.min(duration, 90*24*60)));
            case "hours" -> LocalDateTime.now().plusHours(Math.max(1, Math.min(duration, 90*24)));
            case "days" -> LocalDateTime.now().plusDays(Math.max(1, Math.min(duration, 90)));
            default -> LocalDateTime.now().plusSeconds(10);
        };
    }

    private void storeCode(StoredCode storedCode) {
        File file = new File(STORAGE_DIR + File.separator + storedCode.getId() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(storedCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StoredCode loadCode(String id) {
        File file = new File(STORAGE_DIR + File.separator + id + ".ser");
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (StoredCode) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    @GetMapping("/saved")
    public String listSavedCodes(Model model) {
        List<StoredCode> validCodes = new ArrayList<>();
        File storageDir = new File(STORAGE_DIR);

        if (storageDir.exists()) {
            for (File file : Objects.requireNonNull(storageDir.listFiles())) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    StoredCode storedCode = (StoredCode) ois.readObject();
                    if (!storedCode.isExpired()) {
                        validCodes.add(storedCode);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        validCodes.sort(Comparator.comparing(StoredCode::getExpirationTime));
        model.addAttribute("savedCodes", validCodes);
        return "saved_codes_list";
    }

}