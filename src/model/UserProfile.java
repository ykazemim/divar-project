package model;

import java.util.HashSet;
import java.util.Set;

public class UserProfile {
    private final String userId;
    private final String uuid;
    private final String rawContent;
    private final Set<String> categories;
    private final Set<String> tags;
    
    public UserProfile(String userId, String uuid, String rawContent) {
        this.userId = userId;
        this.uuid = uuid;
        this.rawContent = rawContent;
        this.categories = new HashSet<>();
        this.tags = new HashSet<>();
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getUuid() {
        return uuid;
    }
    
    public String getRawContent() {
        return rawContent;
    }
    
    public Set<String> getCategories() {
        return categories;
    }
    
    public Set<String> getTags() {
        return tags;
    }
    
    public void addCategory(String category) {
        categories.add(category);
    }
    
    public void addTag(String tag) {
        tags.add(tag);
    }
    
    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }
    
    public boolean hasCategory(String category) {
        return categories.contains(category);
    }
    
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
}
