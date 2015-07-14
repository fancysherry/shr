package unique.fancysherry.shr.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;


import java.util.regex.Pattern;

import unique.fancysherry.shr.util.LogUtil;

import static unique.fancysherry.shr.util.LogUtil.*;

/**
 * Created by fancysherry on 15-7-11.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG =makeLogTag(SyncAdapter.class);

    private Context context;


    private static final Pattern sSanitizeAccountNamePattern = Pattern.compile("(.).*?(.?)@");
    public static final String EXTRA_SYNC_USER_DATA_ONLY = "unique.fancysherry.shr.EXTRA_SYNC_USER_DATA_ONLY";;



  public SyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
  }

  public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
    super(context, autoInitialize, allowParallelSyncs);
  }

  @Override
  public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {

      final boolean uploadOnly = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
      final boolean manualSync = extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
      final boolean initialize = extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false);
      final boolean userDataOnly = extras.getBoolean(EXTRA_SYNC_USER_DATA_ONLY, false);

      final String logSanitizedAccountName = sSanitizeAccountNamePattern
              .matcher(account.name).replaceAll("$1...$2@");

      if (uploadOnly) {
          return;
      }

      LogUtil.e(TAG + "Beginning sync for account " + logSanitizedAccountName + "," +
              " uploadOnly=" + uploadOnly +
              " manualSync=" + manualSync +
              " userDataOnly =" + userDataOnly +
              " initialize=" + initialize);

      // Sync from bootstrap and remote data, as needed
//      new SyncHelper(mContext).performSync(syncResult, account, extras);


  }
}
