package ru.tbank.submissionservice.service.language.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.client.Judge0Client;
import ru.tbank.submissionservice.dto.language.Language;
import ru.tbank.submissionservice.exception.language.InvalidLanguageException;
import ru.tbank.submissionservice.service.language.LanguageService;

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
    public List<String> getLanguageNames() {
        return getLanguages().stream().map(Language::name).toList();
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
