package es.rafaco.devtools.view.dialog;

import android.content.ClipData;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Anr;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.devtools.view.overlay.screens.report.ReportHelper;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreensScreen;

public class ReportDialogActivity extends AppCompatActivity {

    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                final List<Crash> crashes = DevTools.getDatabase().crashDao().getAll();
                final List<Anr> anrs = DevTools.getDatabase().anrDao().getAll();
                final List<Screen> screens = DevTools.getDatabase().screenDao().getAll();
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buildDialog(crashes, anrs, screens);
                    }
                });
            }
        });
    }

    private void buildDialog(List<Crash> crashes, List<Anr> anrs, List<Screen> screens) {

        ContextWrapper ctw = new ContextThemeWrapper(this, R.style.LibTheme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_report, null);
        builder.setView(dialogView)
                .setTitle("Building report")
                .setMessage("Choose what do you want to include")
                .setCancelable(false);

        initAdapter(dialogView);
        loadData(adapter, crashes, anrs, screens);


        AppCompatButton crashCancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
        crashCancelButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               alertDialog.dismiss();
               finish();
           }
        });
        AppCompatButton crashReportButton = dialogView.findViewById(R.id.dialog_report_button);
        crashReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImagesSelected(null);
                //startImagePicker();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void initAdapter(View dialogView){

        adapter = new DecoratedToolInfoAdapter(this, new ArrayList<DecoratedToolInfo>());
        adapter.enableSwitchMode();
        recyclerView = dialogView
                .findViewById(R.id.report_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void loadData(DecoratedToolInfoAdapter adapter, List<Crash> crashes, List<Anr> anrs, List<Screen> screens) {

        ArrayList<DecoratedToolInfo> array = new ArrayList<>();

        NavigationStep step = new NavigationStep(InfoScreen.class, null);
        array.add(new DecoratedToolInfo(
                "Info",
                " - ",
                R.color.rally_white,
                1,
                step));

        step = new NavigationStep(ErrorsScreen.class, null);
        array.add(new DecoratedToolInfo(
                "Errors",
                crashes.size() + " crashes and " + anrs.size() + " ANRs",
                3,
                R.color.rally_orange,
                step));

        step = new NavigationStep(ScreensScreen.class, null);
        array.add(new DecoratedToolInfo(
                "Screenshots",
                screens.size() + " screens",
                5,
                R.color.rally_purple,
                step));


        Collections.sort(array, new Comparator<DecoratedToolInfo>() {
            @Override
            public int compare(DecoratedToolInfo o1, DecoratedToolInfo o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });

        adapter.replaceAll(array);
    }


    public void startImagePicker(){
        startActivityForResult(getImagePickerIntent(), 12341);
    }

    private Intent getImagePickerIntent() {

        File screensFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Screenshots");

        Uri screenFolderUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = getApplicationContext().getPackageName() + ".devtools.provider";
            screenFolderUri = FileProvider.getUriForFile(getApplicationContext(), authority, screensFolder);
        } else {
            screenFolderUri = Uri.fromFile(screensFolder);
        }

        Intent intent = new Intent(Intent.ACTION_PICK, screenFolderUri);
        intent.setType("image/*"); //FileUtils.getMimeType(file)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imageEncoded;
        List<String> imagesEncodedList;
        try {
            // When an Image is picked
            if (requestCode == 12341 && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        //TODO: Version problem < JELLY_BEAN
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri);
                                // Get the cursor
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                imageEncoded  = cursor.getString(columnIndex);
                                imagesEncodedList.add(imageEncoded);
                                cursor.close();

                            }
                            Log.v(DevTools.TAG, "Selected Images" + mArrayUri.size());
                            onImagesSelected(mArrayUri);
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong",
                    Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onImagesSelected(ArrayList<Uri> mArrayUri) {
        //TODO: use selected images
        DevTools.sendReport(ReportHelper.ReportType.SESSION, mArrayUri);
        alertDialog.dismiss();
        finish();
    }
}
