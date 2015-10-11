package unique.fancysherry.shr.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unique.fancysherry.shr.io.model.Share;


public class DateUtil {
  private DateUtil() {}

  public static final String FORMATE_HH_MM = "HH:mm";
  public static final String FORMATE_MM_DD_HH_MM = "MM.dd.HH:mm";
  public static final String FORMATE_MM_DD_HH_MM_SS = "MM-dd.HH:mm:ss";
  public static final String FORMATE_MM_DD = "MM.dd";

  public static long HOUR_LENGTH = 1000 * 60 * 60;
  public static final long DAY_LENGTH = 1000 * 60 * 60 * 24;

  public enum DATE {
    TODAY,
    YESTERDAY,
    OLDER
  }

  public static DATE getAbstractDate(long time) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);

    long todayStart = calendar.getTimeInMillis();
    long yesterdayStart = todayStart - 1000 * 60 * 60 * 24;
    if (time >= todayStart) {
      return DATE.TODAY;
    } else if (time >= yesterdayStart) {
      return DATE.YESTERDAY;
    } else {
      return DATE.OLDER;
    }
  }

  public static String getDate(long time, String format) {
    return new SimpleDateFormat(format).format(time);
  }

  private static final DateFormat[] PUBDATE_DATE_FORMATS = {
      new SimpleDateFormat("d' 'MMM' 'yy' 'HH:mm:ss", Locale.US),
      new SimpleDateFormat("d' 'MMM' 'yy' 'HH:mm:ss' 'Z", Locale.US),
      new SimpleDateFormat("d' 'MMM' 'yy' 'HH:mm:ss' 'z", Locale.US)};

  private static final DateFormat[] UPDATE_DATE_FORMATS = {
      new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.US),
      new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ssZ", Locale.US),
      new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSSz", Locale.US),
      new SimpleDateFormat("yyyy-MM-dd", Locale.US)};

  private static final String[][] TIMEZONES_REPLACE = { {"MEST", "+0200"}, {"EST", "-0500"},
      {"PST", "-0800"}};

  public static long parseUpdateDate(String dateStr, boolean tryAllFormat) {
    for (DateFormat format : UPDATE_DATE_FORMATS) {
      try {
        Date result = format.parse(dateStr);
        return result.getTime();
      } catch (ParseException ignored) {} // just do nothing
    }

    if (tryAllFormat)
      return parsePubdateDate(dateStr, false);
    else
      return 0;
  }

  public static long parsePubdateDate(String dateStr) {
    dateStr = improveDateString(dateStr);
    return parsePubdateDate(dateStr, true);
  }

  public static long parsePubdateDate(String dateStr, boolean tryAllFormat) {
    for (DateFormat format : PUBDATE_DATE_FORMATS) {
      try {
        Date result = format.parse(dateStr);
        return result.getTime();
      } catch (ParseException ignored) {} // just do nothing
    }

    if (tryAllFormat)
      return parseUpdateDate(dateStr, false);
    else
      return 0;
  }

  public static String improveDateString(String dateStr) {
    // We remove the first part if necessary (the day display)
    int coma = dateStr.indexOf(", ");
    if (coma != -1) {
      dateStr = dateStr.substring(coma + 2);
    }

    dateStr =
        dateStr.replaceAll("([0-9])T([0-9])", "$1 $2").replaceAll("Z$", "").replaceAll("  ", " ")
            .trim(); // fix useless char

    // Replace bad timezones
    for (String[] timezoneReplace : TIMEZONES_REPLACE) {
      dateStr = dateStr.replace(timezoneReplace[0], timezoneReplace[1]);
    }

    return dateStr;
  }


  // 计算日期天数的差值
  public static int dateDValue(String date_max, String date_min) throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String maxTime = getTime(date_max);
    String minTime = getTime(date_min);
    Date maxTime_val = format.parse(maxTime);
    Date minTime_val = format.parse(minTime);
    return Math.abs((int) (maxTime_val.getTime() - minTime_val.getTime()) / 86400000);
  }

  public static int calDaySize(List<Share> pShares) throws ParseException {
    int result = 0;
    if (pShares != null)
      result = 1;
    String flag = pShares.get(0).share_time;
    for (int i = 0; i < pShares.size(); i++) {
      if (!isDateEquals(flag, pShares.get(i).share_time)) {
        result++;
        flag = pShares.get(i).share_time;
      }
    }
    return result;
  }

  // 判断日期是否相同
  public static boolean isDateEquals(String date_max, String date_min) throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String maxTime = getTime(date_max);
    String minTime = getTime(date_min);
    Date maxTime_val = format.parse(maxTime);
    Date minTime_val = format.parse(minTime);
    if ((int) (maxTime_val.getTime() - minTime_val.getTime()) / 86400000 == 0)
      return true;
    else
      return false;
  }


  // 将日期转化为列表日期的输出格式，eg:今天，昨天，x月x日，
  public static String toListDate(String date) throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String oldTime = getTime(date);
    String nowTime = getNowTime();
    Date oldTime_val = format.parse(oldTime);
    Date nowTime_val = format.parse(nowTime);

    // 昨天 86400000=24*60*60*1000 一天
    if (nowTime_val.getTime() - oldTime_val.getTime() > 0
        && nowTime_val.getTime() - oldTime_val.getTime() <= 86400000)
      return "昨天";


    else if (nowTime_val.getTime() - oldTime_val.getTime() == 0)
      return "今天";

    else {
      String date_month = date.substring(5, 7);
      String date_day = date.substring(8, 10);
      return date_month + "月" + date_day + "日";
    }
  }

  private static String getTime(String time) {
    Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]");
    Matcher matcher = pattern.matcher(time);
    if (matcher.find()) {
      return matcher.group(0);
    }
    else
      return null;
  }

  private static String getNowTime() {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
    return df.format(new Date());// new Date()为获取当前系统时间
  }

}
