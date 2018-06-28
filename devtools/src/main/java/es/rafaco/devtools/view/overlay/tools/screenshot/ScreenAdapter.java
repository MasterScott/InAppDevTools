package es.rafaco.devtools.view.overlay.tools.screenshot;

import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.utils.DateUtils;

public class ScreenAdapter extends RecyclerView.Adapter<ScreenAdapter.MyViewHolder> {

    private Context mContext;
    private List<Screen> screenList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.subtitle);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public ScreenAdapter(Context mContext, List<Screen> screenList) {
        this.mContext = mContext;
        this.screenList = screenList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tool_screen_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Screen screen = screenList.get(position);
        holder.title.setText(screen.getActivityName());
        holder.count.setText(DateUtils.getElapsedTimeString(screen.getDate()));

        // loading album cover using Glide library
        //Glide.with(mContext).load(screen.getThumbnail()).into(holder.thumbnail);
        //ImageUtils.loadInto(screen.getAbsolutePath(), holder.thumbnail);
        new ImageLoaderAsyncTask(holder.thumbnail).execute(screen.getAbsolutePath());

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {

        Context wrapper = new ContextThemeWrapper(mContext, R.style.popupMenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.screen, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int i = menuItem.getItemId();
            if (i == R.id.action_preview) {
                Toast.makeText(mContext, "Preview", Toast.LENGTH_SHORT).show();
                return true;
            } else if (i == R.id.action_open) {
                Toast.makeText(mContext, "Open", Toast.LENGTH_SHORT).show();
                return true;
            } else if (i == R.id.action_delete) {
                Toast.makeText(mContext, "Delete", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return screenList.size();
    }
}