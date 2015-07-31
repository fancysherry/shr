package unique.fancysherry.shr.ui.adapter.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import unique.fancysherry.shr.R;
import unique.fancysherry.shr.ui.adapter.recycleview.stickheader.StickyHeadersAdapter;

/**
 * Created by fancysherry on 15-7-15.
 */
public class HeaderAdapter implements StickyHeadersAdapter<HeaderAdapter.ViewHolder> {

    private List<String> items;

    public HeaderAdapter(List<String> items) {
        this.items = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_list_top_header, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder headerViewHolder, int position) {

    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.share_list_date);
        }
    }
}
