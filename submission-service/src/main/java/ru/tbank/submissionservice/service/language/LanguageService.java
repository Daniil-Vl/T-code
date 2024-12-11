package ru.tbank.submissionservice.service.language;

import ru.tbank.submissionservice.dto.language.Language;

import java.util.List;

public interface LanguageService {
    /**
     * Get available languages
     *
     * @return
     */
    List<Language> getLanguages();

    /**
     * Get available language names
     *
     * @return
     */
    List<String> getLanguageNames();

    /**
     * Retrieve language id by using language name
     * If language not supported, then throw InvalidLanguageException
     *
     * @param languageName
     * @return
     */
    int getLanguageIdByLanguageName(String languageName);
}
