package unique.fancysherry.shr.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupManageAdapter;

public class GroupChangeManagerActivity extends AppCompatActivity {

    @InjectView(R.id.group_manage_list)
    RecyclerView group_manage_list;
    private String group_id;

    private GroupManageAdapter groupManageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_change_manager);
        ButterKnife.inject(this);
        initAdapter();
        Bundle mBundle=getIntent().getExtras();
        group_id=mBundle.getString("group_id");


    }

    private void initAdapter()
    {
        groupManageAdapter = new GroupManageAdapter(this);
        group_manage_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        group_manage_list.setAdapter(groupManageAdapter);
    }
}
