package unique.fancysherry.shr.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fancysherry on 15-3-6.
 */
public class Md5Util {

    /**
     * @param str
     * @return
     * @Date: 2013-9-6
     * @Author: lulei
     * @Description:  32位小写MD5
     */
    public static String parseStrToMd5L32(String str){
        String reStr = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes){
                int bt = b&0xff;
                if (bt < 16){
                    stringBuffer.append(0);
                }
                stringBuffer.append(Integer.toHexString(bt));
            }
            reStr = stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return reStr;
    }


}
