package unique.fancysherry.shr.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import unique.fancysherry.shr.R;

public class DeleteGroupActivity extends ActionBarActivity {

  private String group_id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_delete_group);
    Bundle mBundle = getIntent().getExtras();
    group_id = mBundle.getString("group_id");
  }


}
