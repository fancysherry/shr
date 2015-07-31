package unique.fancysherry.shr.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import unique.fancysherry.shr.R;

public class UserInformationResetActivity extends ActionBarActivity {
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information_reset);
        Bundle mBundle=getIntent().getExtras();
        user_id=mBundle.getString("user_id");
    }


}
