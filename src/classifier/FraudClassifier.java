package classifier;

import model.UserProfile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FraudClassifier implements Classifier {
    
    private static final String[] EXPLICIT_FRAUD_KEYWORDS = {
        "کلاهبردار",
        "کلاهبرداری",
        "فریب",
        "تقلب"
    };
    
    private static final String[] SCAM_BEHAVIOR_PATTERNS = {
        "بیعانه.*گرفت",
        "بیانه.*گرفت",
        "بیعانه.*میکنه",
        "بیعانه.*نمی.*آمد",
        "بیعانه.*نیامد",
        "سر.*کار.*نیامد",
        "سر.*کار.*نمی.*آید",
        "پول.*گرفت.*کار.*انجام.*نداد",
        "پول.*گرفت.*ولی",
        "جواب.*نمی.*دهد",
        "گوشی.*جواب.*نمی",
        "شماره.*جواب.*نمی",
        "شرکت.*دفتر.*ندارد",
        "شرکت.*دفتر.*نداره"
    };
    
    private static final String[] PLATFORM_ABUSE_PATTERNS = {
        "آگهی.*تکراری.*است",
        "تکراری.*بودن",
        "نقض.*قوانین",
        "رد.*شده.*تکرار",
        "supply_publish_rate[\"']?:\\s*0\\.0",
        "retire_reason.*تکراری"
    };
    
    private static final String[] REPORT_KEYWORDS = {
        "ReliableReport",
        "گزارش.*معتبر.*کلاهبرداری",
        "گزارش.*کلاهبرداری",
        "result_is_accepted.*True"
    };
    
    private static final Pattern MULTI_CITY_PATTERN = Pattern.compile("supply_unique_cities[\"']?:\\s*([3-9]|[1-9][0-9]+)");
    private static final Pattern ZERO_PUBLISH_RATE = Pattern.compile("supply_publish_rate[\"']?:\\s*0\\.0");
    private static final Pattern HIGH_REJECTION = Pattern.compile("supply_total_posts[\"']?:\\s*([5-9]|[1-9][0-9]+).*supply_published_posts[\"']?:\\s*0");
    private static final Pattern CONTACT_DISABLED = Pattern.compile("contact_chat_enabled[\"']?:\\s*false");
    
    @Override
    public boolean matches(UserProfile profile) {
        String content = profile.getRawContent();
        int fraudScore = 0;
        
        for (String keyword : EXPLICIT_FRAUD_KEYWORDS) {
            if (content.matches("(?s).*" + keyword + ".*")) {
                fraudScore += 5;
                break;
            }
        }
        
        int scamBehaviorCount = 0;
        for (String pattern : SCAM_BEHAVIOR_PATTERNS) {
            if (content.matches("(?s).*" + pattern + ".*")) {
                scamBehaviorCount++;
            }
        }
        if (scamBehaviorCount >= 2) {
            fraudScore += 4;
        } else if (scamBehaviorCount == 1) {
            fraudScore += 2;
        }
        
        for (String pattern : REPORT_KEYWORDS) {
            if (content.matches("(?s).*" + pattern + ".*")) {
                fraudScore += 3;
                break;
            }
        }
        
        int abuseCount = 0;
        for (String pattern : PLATFORM_ABUSE_PATTERNS) {
            if (content.matches("(?s).*" + pattern + ".*")) {
                abuseCount++;
            }
        }
        if (abuseCount >= 2) {
            fraudScore += 3;
        }
        
        if (MULTI_CITY_PATTERN.matcher(content).find()) {
            fraudScore += 3;
        }
        
        if (HIGH_REJECTION.matcher(content.replaceAll("\\s+", " ")).find()) {
            fraudScore += 2;
        }
        
        if (CONTACT_DISABLED.matcher(content).find() && content.contains("supply_total_calls")) {
            Matcher callMatcher = Pattern.compile("supply_total_calls[\"']?:\\s*([0-9]+)").matcher(content);
            if (callMatcher.find()) {
                int calls = Integer.parseInt(callMatcher.group(1));
                if (calls > 10) {
                    fraudScore += 2;
                }
            }
        }
        
        return fraudScore >= 5;
    }
    
    @Override
    public String getCategoryName() {
        return "fraudsters";
    }
}
