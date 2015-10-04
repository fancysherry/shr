package unique.fancysherry.shr.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.adapter.recycleview.GroupManageAdapter;
import unique.fancysherry.shr.ui.adapter.recycleview.InvitedMemberAdapter;

public class InviteMemberActivity extends AppCompatActivity {
    @InjectView(R.id.group_invited_list)
    RecyclerView group_invited_list;

    private InvitedMemberAdapter invitedMemberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);
        initAdapter();


    }

    private void initAdapter()
    {
        invitedMemberAdapter = new InvitedMemberAdapter(this);
        group_invited_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        group_invited_list.setAdapter(invitedMemberAdapter);
    }

}
