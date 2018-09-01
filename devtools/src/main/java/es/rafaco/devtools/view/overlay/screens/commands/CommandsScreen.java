package es.rafaco.devtools.view.overlay.screens.commands;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.utils.AppUtils;

public class CommandsScreen extends OverlayScreen {

    private EditText input;
    private Button btn;
    private TextView out;
    private String command;
    private ShellExecuter exe;
    private Button infoButton;
    private Button devOptionsButton;
    private Button forceCloseButton;
    private Button fullRestartButton;
    private Button installedButton;

    public CommandsScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Commands";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_commands; }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {
        input = getView().findViewById(R.id.txt);
        btn = getView().findViewById(R.id.btn);
        out = getView().findViewById(R.id.out);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                exe = new ShellExecuter();
                command = input.getText().toString();

                String outp = exe.Executer(command);
                out.setText(outp);
                Log.d("Output", outp);
            }
        });

        infoButton = view.findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                AppUtils.openAppSettings(getContext());
            }
        });

        devOptionsButton = view.findViewById(R.id.dev_options_button);
        devOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                AppUtils.openDeveloperOptions(getContext());
            }
        });

        forceCloseButton = view.findViewById(R.id.force_close_button);
        forceCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.exit();
            }
        });

        fullRestartButton = view.findViewById(R.id.full_restart_button);
        fullRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.fullRestart(getContext());
            }
        });

    }

    @Override
    protected void onStop() {
        //TODO!! exe.Executer(cancel previous commmand)
    }

    @Override
    protected void onDestroy() {
    }
}
