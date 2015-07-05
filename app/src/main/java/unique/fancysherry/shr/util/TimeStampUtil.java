package unique.fancysherry.shr.util;

/**
 * Created by fancysherry on 15-4-16.
 */
public class TimeStampUtil {
    public static String TimeStampToDate(Long timestampString) {
        Long timestamp = timestampString * 1000;
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
        return date;
    }
}
