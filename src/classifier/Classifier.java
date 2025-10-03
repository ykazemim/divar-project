package classifier;

import model.UserProfile;

public interface Classifier {
    boolean matches(UserProfile profile);
    String getCategoryName();
}
