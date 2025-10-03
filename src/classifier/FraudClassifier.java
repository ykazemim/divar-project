package classifier;

import model.UserProfile;

public class FraudClassifier implements Classifier {
    private static final String[] FRAUD_KEYWORDS = {
        "کلاهبردار",
        "بیعانه",
        "کلاهبرداری",
        "فریب",
        "گزارش.*معتبر",
        "ReliableReport",
        "طعمه",
        "مشکوک"
    };
    
    @Override
    public boolean matches(UserProfile profile) {
        String content = profile.getRawContent();
        
        for (String keyword : FRAUD_KEYWORDS) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String getCategoryName() {
        return "fraudsters";
    }
}
