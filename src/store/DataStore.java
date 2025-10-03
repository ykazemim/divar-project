package store;

import model.UserProfile;

import java.util.*;

public class DataStore {
    private final Map<String, UserProfile> userIndex;
    private final Map<String, Set<String>> categoryIndex;
    private final Map<String, Set<String>> tagIndex;
    
    public DataStore() {
        this.userIndex = new HashMap<>();
        this.categoryIndex = new HashMap<>();
        this.tagIndex = new HashMap<>();
    }
    
    public void addUser(UserProfile profile) {
        userIndex.put(profile.getUserId(), profile);
        
        for (String category : profile.getCategories()) {
            categoryIndex.computeIfAbsent(category, k -> new HashSet<>())
                        .add(profile.getUserId());
        }
        
        for (String tag : profile.getTags()) {
            tagIndex.computeIfAbsent(tag, k -> new HashSet<>())
                    .add(profile.getUserId());
        }
    }
    
    public UserProfile getUser(String userId) {
        return userIndex.get(userId);
    }
    
    public Set<String> getUsersByCategory(String category) {
        return categoryIndex.getOrDefault(category, Collections.emptySet());
    }
    
    public Set<String> getUsersByTag(String tag) {
        return tagIndex.getOrDefault(tag, Collections.emptySet());
    }
    
    public boolean addTagToUser(String userId, String tag) {
        UserProfile profile = userIndex.get(userId);
        if (profile == null) {
            return false;
        }
        
        profile.addTag(tag);
        tagIndex.computeIfAbsent(tag, k -> new HashSet<>())
                .add(userId);
        return true;
    }
    
    public boolean removeTagFromUser(String userId, String tag) {
        UserProfile profile = userIndex.get(userId);
        if (profile == null) {
            return false;
        }
        
        boolean removed = profile.removeTag(tag);
        if (removed) {
            Set<String> users = tagIndex.get(tag);
            if (users != null) {
                users.remove(userId);
                if (users.isEmpty()) {
                    tagIndex.remove(tag);
                }
            }
        }
        return removed;
    }
    
    public int getUserCount() {
        return userIndex.size();
    }
}
