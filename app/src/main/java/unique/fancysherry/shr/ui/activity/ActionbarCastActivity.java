package unique.fancysherry.shr.ui.activity;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.util.system.ResourceHelper;

public abstract class ActionbarCastActivity extends ActionBarActivity {
    public Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_actionbar_cast);
    }




    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mToolbar.setTitle(title);
    }



    protected abstract void initializeToolbar();





}
