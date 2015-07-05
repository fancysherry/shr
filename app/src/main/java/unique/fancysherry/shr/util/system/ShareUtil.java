package unique.fancysherry.shr.util.system;

import android.content.Intent;


public class ShareUtil {


    public static Intent getSystemShareIntent(String title,String text){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TITLE, title);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        return sendIntent;
    }
}
