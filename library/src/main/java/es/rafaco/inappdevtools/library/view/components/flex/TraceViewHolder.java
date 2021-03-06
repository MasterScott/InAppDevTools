/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.view.components.flex;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

import com.alorma.timeline.TimelineView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class TraceViewHolder extends FlexibleViewHolder {

    private final LinearLayout itemContent;
    private final CardView cardView;
    private final TimelineView timeline;
    private final ImageView navIcon;
    private final TextView exceptionView;
    private final TextView messageView;
    private final TextView whereView;
    private final TextView where2View;
    private final TextView where3View;
    private final TextView tag;

    public TraceViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.itemContent = view.findViewById(R.id.item_content);
        this.cardView = view.findViewById(R.id.card_view);
        this.timeline = view.findViewById(R.id.timeline);
        this.exceptionView = view.findViewById(R.id.exception);
        this.messageView = view.findViewById(R.id.message);
        this.whereView = view.findViewById(R.id.where);
        this.where2View = view.findViewById(R.id.where2);
        this.where3View = view.findViewById(R.id.where3);
        this.navIcon = view.findViewById(R.id.icon);
        this.tag = view.findViewById(R.id.tag);
    }

    @Override
    public void bindTo(Object abstractData, final int position) {
        final TraceItemData data = (TraceItemData) abstractData;
        if (data!=null){

            itemView.setActivated(data.isExpanded());
            itemContent.setVisibility(data.isExpanded()? View.VISIBLE : View.GONE);
            cardView.setVisibility(data.isExpanded()? View.VISIBLE : View.GONE);
            timeline.setVisibility(data.isExpanded()? View.VISIBLE : View.GONE);
            if (!data.isExpanded())
                return;


            timeline.setLineColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_top));
            timeline.setIndicatorColor(ContextCompat.getColor(itemView.getContext(), data.getColor()));
            timeline.setIndicatorSize(UiUtils.getPixelsFromDp(itemView.getContext(), 5));

            timeline.setTimelineAlignment(TimelineView.ALIGNMENT_MIDDLE);
            if (data.getPosition().equals(TraceItemData.Position.START)){
                whereView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_orange));
                timeline.setIndicatorColor(ContextCompat.getColor(itemView.getContext(), data.getColor()));
                timeline.setTimelineType(TimelineView.TYPE_START);
            }
            else if (data.getPosition().equals(TraceItemData.Position.END)){
                whereView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_yellow));
                timeline.setTimelineType(TimelineView.TYPE_END);
            }else{
                whereView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_yellow));
                timeline.setTimelineType(TimelineView.TYPE_MIDDLE);
            }
            exceptionView.setVisibility(TextUtils.isEmpty(data.getException()) ? View.GONE : View.VISIBLE);
            exceptionView.setText(data.getException());
            messageView.setVisibility(TextUtils.isEmpty(data.getMessage()) ? View.GONE : View.VISIBLE);
            messageView.setText(data.getMessage());

            final Sourcetrace trace = data.getSourcetrace();
            whereView.setText(trace.getShortClassName() + "." + trace.getMethodName() + "()");
            where2View.setText(trace.getPackageName());
            where2View.setTextColor(ContextCompat.getColor(itemView.getContext(), data.getColor()));

            tag.setText(data.getTag());
            tag.setTextColor(ContextCompat.getColor(itemView.getContext(), data.getColor()));
            UiUtils.setStrokeToDrawable(tag.getContext(), 1, data.getColor(), tag.getBackground());

            where3View.setText(trace.getFileName() + ":" + trace.getLineNumber());

            if (data.isOpenable()){
                UiUtils.setCardViewClickable(cardView, false);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OverlayService.performNavigation(SourceDetailScreen.class,
                                SourceDetailScreen.buildTraceParams(trace.getUid()));
                    }
                });
                
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_top));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 3));
                }
                where3View.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_white));
                navIcon.setVisibility(View.VISIBLE);
            }
            else{
                UiUtils.setCardViewClickable(cardView, false);
                cardView.setOnClickListener(null);
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.iadt_surface_bottom));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cardView.setElevation(0);
                }
                where3View.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.rally_gray));
                navIcon.setVisibility(View.GONE);
            }
        }
    }
}
