package com.ramendirectory.japanramendirectory.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

@Service
public class ContentFilterServiceImpl implements ContentFilterService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContentFilterServiceImpl.class);
    
    private Set<String> inappropriateWords = new HashSet<>();
    private static final String INAPPROPRIATE_WORDS_FILE = "inappropriate-words.txt";
    
    @PostConstruct
    public void init() {
        loadInappropriateWords();
        logger.info("Content filter initialized with {} inappropriate words", inappropriateWords.size());
    }
    
    /**
     * Loads inappropriate words from the resource file
     */
    private void loadInappropriateWords() {
        try {
            ClassPathResource resource = new ClassPathResource(INAPPROPRIATE_WORDS_FILE);
            InputStream inputStream = resource.getInputStream();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim().toLowerCase();
                    if (!line.isEmpty()) {
                        inappropriateWords.add(line);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load inappropriate words list", e);
        }
    }
    
    /**
     * Checks if a text contains any inappropriate words
     * 
     * @param text The text to check
     * @return true if inappropriate content is found, false otherwise
     */
    public boolean containsInappropriateContent(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        }
        
        String lowerText = text.toLowerCase();
        
        // Check for exact matches
        for (String word : inappropriateWords) {
            if (lowerText.equals(word) || lowerText.contains(word)) {
                logger.debug("Inappropriate content detected: '{}'", word);
                return true;
            }
        }
        
        // Check for word variations with numbers
        for (String word : inappropriateWords) {
            // Create a pattern that allows numbers within the word
            // e.g., "admin" would match "adm1n" or "4dmin"
            String patternStr = word.replaceAll(".", "$0+").replace("\\+", "[a-z0-9]");
            if (Pattern.compile(patternStr).matcher(lowerText).find()) {
                logger.debug("Inappropriate content variation detected based on: '{}'", word);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Filters inappropriate words from text by replacing them with asterisks
     * 
     * @param text The text to filter
     * @return Filtered text with inappropriate words replaced by asterisks
     */
    public String filterText(String text) {
        if (!StringUtils.hasText(text)) {
            return text;
        }
        
        String filteredText = text;
        for (String word : inappropriateWords) {
            if (filteredText.toLowerCase().contains(word)) {
                String replacement = "*".repeat(word.length());
                filteredText = filteredText.replaceAll("(?i)" + Pattern.quote(word), replacement);
            }
        }
        
        return filteredText;
    }
    
    /**
     * Gets inappropriate words found in the provided text
     * 
     * @param text The text to check
     * @return List of inappropriate words found in the text
     */
    public List<String> getInappropriateWordsInText(String text) {
        List<String> foundWords = new ArrayList<>();
        
        if (!StringUtils.hasText(text)) {
            return foundWords;
        }
        
        String lowerText = text.toLowerCase();
        
        for (String word : inappropriateWords) {
            if (lowerText.contains(word)) {
                foundWords.add(word);
            }
        }
        
        return foundWords;
    }
} 