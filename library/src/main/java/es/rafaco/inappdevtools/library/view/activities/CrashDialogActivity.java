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

package es.rafaco.inappdevtools.library.view.activities;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.app.AppCompatActivity;
//@import androidx.appcompat.view.ContextThemeWrapper;
//@import androidx.appcompat.widget.AppCompatButton;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatButton;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfigField;
import es.rafaco.inappdevtools.library.logic.reports.ReportType;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;

public class CrashDialogActivity extends AppCompatActivity {

    private Crash crash;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThreadUtils.runOnBack(new Runnable() {
            @Override
            public void run() {
                crash = IadtController.get().getDatabase().crashDao().getLast();
                CrashDialogActivity.this.buildDialog(crash);
            }
        });
    }

    private void buildDialog(final Crash crash) {
        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_crash, null);
        builder.setView(dialogView)
                .setTitle("Your app crashed and we restarted it")
                .setMessage("Here you have some information about what went wrong.\nPlease report it")
                .setCancelable(false);

        TextView crashTitle = dialogView.findViewById(R.id.detail_title);
        TextView crashSubtitle = dialogView.findViewById(R.id.detail_subtitle);
        TextView crashConsole = dialogView.findViewById(R.id.detail_console);

        crashTitle.setText(crash.getException());
        crashSubtitle.setText(crash.getMessage());
        crashConsole.setText(crash.getStacktrace());

        AppCompatButton crashContinueButton = dialogView.findViewById(R.id.crash_continue_button);
        AppCompatButton crashReportButton = dialogView.findViewById(R.id.crash_report_button);
        AppCompatButton crashDetailButton = dialogView.findViewById(R.id.crash_detail_button);

        crashContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyDialog();
            }
        });
        crashReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashReport(crash);
            }
        });
        crashDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashDetail(crash);
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void onCrashDetail(Crash crash) {
        if (Iadt.getConfig().getBoolean(BuildConfigField.OVERLAY_ENABLED)){
            OverlayService.performNavigation(CrashDetailScreen.class, String.valueOf(crash.getUid()));
            destroyDialog();
        }
    }

    private void onCrashReport(Crash crash) {
        IadtController.get().startReportWizard(ReportType.CRASH, crash.getUid());
        destroyDialog();
    }

    private void destroyDialog() {
        alertDialog.dismiss();
        finish();
    }
}
