package classifier;

import model.UserProfile;

public class BusinessClassifier implements Classifier {
    private static final String[] BUSINESS_KEYWORDS = {
        "کسب‌وکار",
        "تجاری",
        "فروشگاه",
        "واسطه",
        "دلال",
        "business_type.*business",
        "آگهی.*تجاری",
        "فعالیت.*تجاری"
    };
    
    @Override
    public boolean matches(UserProfile profile) {
        String content = profile.getRawContent();
        
        for (String keyword : BUSINESS_KEYWORDS) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String getCategoryName() {
        return "businesses";
    }
}
