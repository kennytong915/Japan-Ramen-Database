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

/**
 * Service interface for filtering inappropriate content from user submissions
 */
public interface ContentFilterService {
    
    /**
     * Filters inappropriate content from a text string
     * 
     * @param text the text to filter
     * @return the filtered text
     */
    String filterText(String text);
    
    /**
     * Checks if text contains inappropriate content
     * 
     * @param text the text to check
     * @return true if the text contains inappropriate content
     */
    boolean containsInappropriateContent(String text);
    
    /**
     * Gets inappropriate words found in the provided text
     * 
     * @param text The text to check
     * @return List of inappropriate words found in the text
     */
    List<String> getInappropriateWordsInText(String text);
}

