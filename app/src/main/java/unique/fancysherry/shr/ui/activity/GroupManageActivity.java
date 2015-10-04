package unique.fancysherry.shr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unique.fancysherry.shr.R;

public class GroupManageActivity extends AppCompatActivity {
    @InjectView(R.id.change_group_name_layout_content)
    EditText change_group_name_edittext;
    @InjectView(R.id.change_group_introduce_layout_content)
    EditText change_group_introduce_edittext;
    @InjectView(R.id.manage_group_member_layout)
    RelativeLayout manage_group_member_layout;
    @InjectView(R.id.change_group_manager_btn)
    Button change_group_manager_btn;
    @InjectView(R.id.delete_group_btn)
    Button delete_group_btn;

    private Context context;
    private String group_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage);
        ButterKnife.inject(this);
        Bundle mBundle=getIntent().getExtras();
        group_id=mBundle.getString("group_id");
        context=this;

        manage_group_member_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, GroupMemberActivity.class);
                mIntent.putExtra(group_id,"group_id");
                startActivity(mIntent);

            }
        });

        change_group_manager_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, GroupChangeManagerActivity.class);
                mIntent.putExtra(group_id,"group_id");
                startActivity(mIntent);

            }
        });

        delete_group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, DeleteGroupActivity.class);
                mIntent.putExtra(group_id,"group_id");
                startActivity(mIntent);

            }
        });

    }


}
