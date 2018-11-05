package es.rafaco.devtools.view.overlay.screens.home;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import es.rafaco.devtools.R;

class HeaderViewHolder extends RecyclerView.ViewHolder {

    TextView titleView;

    public HeaderViewHolder(View view) {
        super(view);
        this.titleView = view.findViewById(R.id.title);
    }

    public void bindTo(String data) {
        titleView.setVisibility(TextUtils.isEmpty(data) ? View.GONE : View.VISIBLE);
        if (data != null) titleView.setText(data);
    }
}
