package ru.tbank.submissionservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.client.Judge0Client;
import ru.tbank.submissionservice.enums.Language;
import ru.tbank.submissionservice.exception.InvalidLanguageException;
import ru.tbank.submissionservice.service.LanguageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final Judge0Client client;

    @Override
    public List<Language> getLanguages() {
        return client.getLanguages();
    }

    @Override
    public int getLanguageIdByLanguageName(String languageName) {
        return getLanguageFromString(languageName).id();
    }

    private Language getLanguageFromString(String language) {
        List<Language> languages = getLanguages();

        for (Language lang : languages) {
            if (lang.name().equalsIgnoreCase(language)) {
                return lang;
            }
        }

        throw new InvalidLanguageException(
                String.format("Language %s not supported", language)
        );
    }

}
