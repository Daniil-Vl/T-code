package ru.tbank.submissionservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.submissionservice.controller.LanguageController;
import ru.tbank.submissionservice.service.language.LanguageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/languages")
public class LanguageControllerImpl implements LanguageController {

    private final LanguageService languageService;

    @Override
    public List<String> getLanguages() {
        return languageService.getLanguageNames();
    }

}
