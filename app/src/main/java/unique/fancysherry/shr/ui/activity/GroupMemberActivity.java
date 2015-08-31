package unique.fancysherry.shr.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.adapter.recycleview.DeleteMemberAdapter;

public class GroupMemberActivity extends ActionBarActivity {
    @InjectView(R.id.group_member_list)
    RecyclerView group_member_list;

    private String group_id;

    private DeleteMemberAdapter deleteMemberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        ButterKnife.inject(this);
        Bundle mBundle=getIntent().getExtras();
        group_id=mBundle.getString("group_id");
        initAdapter();


    }

   private void initAdapter()
   {
       deleteMemberAdapter = new DeleteMemberAdapter(this);
       group_member_list.setLayoutManager(new LinearLayoutManager(this,
               LinearLayoutManager.VERTICAL, false));
       group_member_list.setAdapter(deleteMemberAdapter);
   }


}
