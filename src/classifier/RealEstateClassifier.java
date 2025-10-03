package classifier;

import model.UserProfile;

public class RealEstateClassifier implements Classifier {
    private static final String[] REAL_ESTATE_KEYWORDS = {
        "املاک",
        "مشاور املاک",
        "آپارتمان.*فروش",
        "real.estate",
        "real_estate",
        "آژانس املاک",
        "مسکن"
    };
    
    @Override
    public boolean matches(UserProfile profile) {
        String content = profile.getRawContent();
        
        if (!profile.hasCategory("businesses")) {
            return false;
        }
        
        for (String keyword : REAL_ESTATE_KEYWORDS) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String getCategoryName() {
        return "real_estate_agents";
    }
}
