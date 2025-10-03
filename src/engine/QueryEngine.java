package engine;

import classifier.*;
import model.UserProfile;
import parser.ProfileParser;
import store.DataStore;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QueryEngine {
    private final DataStore dataStore;
    private final ProfileParser parser;
    private final List<Classifier> classifiers;
    
    public QueryEngine() {
        this.dataStore = new DataStore();
        this.parser = new ProfileParser();
        this.classifiers = new ArrayList<>();
        initializeClassifiers();
    }
    
    private void initializeClassifiers() {
        classifiers.add(new FraudClassifier());
        classifiers.add(new BusinessClassifier());
        classifiers.add(new NewUserClassifier());
        classifiers.add(new RealEstateClassifier());
    }
    
    public void loadProfiles(String dataDirectory) throws IOException {
        Path dirPath = Paths.get(dataDirectory);
        
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new IOException("Invalid data directory: " + dataDirectory);
        }
        
        int loadedCount = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.md")) {
            for (Path filePath : stream) {
                try {
                    UserProfile profile = parser.parse(filePath);
                    classifyUser(profile);
                    dataStore.addUser(profile);
                    loadedCount++;
                } catch (Exception e) {
                    System.err.println("Error parsing " + filePath + ": " + e.getMessage());
                }
            }
        }
        
        System.out.println("Loaded " + loadedCount + " profiles.");
    }
    
    private void classifyUser(UserProfile profile) {
        for (Classifier classifier : classifiers) {
            if (classifier.matches(profile)) {
                profile.addCategory(classifier.getCategoryName());
            }
        }
    }
    
    public Set<String> findByCategory(String category) {
        return dataStore.getUsersByCategory(category);
    }
    
    public Set<String> findByTag(String tag) {
        return dataStore.getUsersByTag(tag);
    }
    
    public String getUserProfile(String userId) {
        UserProfile profile = dataStore.getUser(userId);
        return profile != null ? profile.getRawContent() : null;
    }
    
    public boolean addTag(String userId, String tag) {
        return dataStore.addTagToUser(userId, tag);
    }
    
    public boolean removeTag(String userId, String tag) {
        return dataStore.removeTagFromUser(userId, tag);
    }
    
    public int getTotalUsers() {
        return dataStore.getUserCount();
    }
}
