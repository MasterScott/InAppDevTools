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

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.AppCompatTextView;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v7.widget.AppCompatTextView;
import android.support.v4.content.ContextCompat;
//#endif

public class ConfigViewHolder extends FlexibleViewHolder {

    AppCompatTextView title;
    AppCompatTextView subtitle;
    Switch switchButton;
    ImageView editButton;
    AppCompatTextView textValue;

    public ConfigViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        title = view.findViewById(R.id.title);
        subtitle = view.findViewById(R.id.subtitle);
        switchButton = view.findViewById(R.id.switch_button);
        editButton = view.findViewById(R.id.edit_button);
        textValue = view.findViewById(R.id.text_value);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final ConfigData configData = (ConfigData) abstractData;
        Context context = title.getContext();
        title.setText(configData.getConfig().getKey());
        subtitle.setText(context.getText(configData.getConfig().getDesc()));

        if (configData.getConfig().getValueType() == boolean.class){
            switchButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
            textValue.setVisibility(View.GONE);

            Boolean value = (Boolean) configData.getInitialValue();
            switchButton.setChecked(value);
            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    configData.setNewValue(isChecked);
                }
            });
        }
        else{
            switchButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            textValue.setVisibility(View.VISIBLE);

            editButton.setImageDrawable(UiUtils.getDrawable(R.drawable.pd_edit));

            int contextualizedColor = ContextCompat.getColor(editButton.getContext(), R.color.rally_white);
            editButton.setColorFilter(contextualizedColor);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Iadt.showMessage("TODO: Not already implemented");
                }
            });
            textValue.setText((String) configData.getInitialValue());
        }
    }
}
