package ru.tbank.submissionservice.service;

import ru.tbank.submissionservice.enums.Language;

import java.util.List;

public interface LanguageService {
    List<Language> getLanguages();
    int getLanguageIdByLanguageName(String languageName);
}
