package es.rafaco.inappdevtools.library.logic.log;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.view.overlay.screens.console.Shell;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatLineAdapter;

public class LogCatReaderTask extends AsyncTask<Void, String, Void> {

    private final int BUFFER_SIZE = 1024;
    private final String commandScript;
    private final int id;
    private boolean isRunning = true;
    private Process logprocess = null;
    private BufferedReader reader = null;
    private LogcatLineAdapter adaptor;
    private int readCounter = 0;
    private int nullCounter = 0;
    private int sameCounter = 0;
    private int processedCounter = 0;
    private Runnable onCancelledCallback;
    private static int counter = -1;

    public LogCatReaderTask(LogcatLineAdapter adaptor, String commandScript) {
        this.adaptor = adaptor;
        this.commandScript = commandScript;
        this.id = counter++;
        Log.v(Iadt.TAG, "LogCatReaderTask " + id + " created:" + commandScript);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String[] bashCommand = Shell.formatBashCommand(commandScript);
            logprocess = Runtime.getRuntime().exec(bashCommand);
        }
        catch (Exception e) {
            FriendlyLog.logException("Exception", e);
            isRunning = false;
        }

        try {
            reader = new BufferedReader(new InputStreamReader(
                    logprocess.getInputStream()),BUFFER_SIZE);
        }
        catch(IllegalArgumentException e){
            FriendlyLog.logException("Exception", e);
            isRunning = false;
        }

        String line;
        try {
            while (isRunning && (line = reader.readLine())!= null) {
                readCounter ++;
                String[] lineArray = new String[1];
                lineArray[0] = line;
                publishProgress(lineArray);
            }
        }
        catch (IOException e) {
            FriendlyLog.logException("Exception", e);
            isRunning = false;
        }

        Log.v(Iadt.TAG, "LogCatReaderTask " + id + " finished doInBackground");
        return null;
    }

    @Override
    protected void onCancelled() {
        isRunning = false;
        if (logprocess != null) logprocess.destroy();
        Log.v(Iadt.TAG, "LogCatReaderTask " + id + " onCancelled");
        Log.v(Iadt.TAG, String.format("Printed %s of %s (%S) lines (filtered %s nulls and %s duplicated)",
                adaptor.getItemCount(), readCounter, processedCounter, nullCounter, sameCounter));

        if(onCancelledCallback !=null){
            onCancelledCallback.run();
            onCancelledCallback = null;
        }
        super.onCancelled();
        //stopTask();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Log.v(Iadt.TAG, "LogCatReaderTask " + id + " onPostExecute");
        Log.v(Iadt.TAG, String.format("Printed %s of %s lines (filtered %s nulls and %s duplicated)", readCounter, adaptor.getItemCount(), nullCounter, sameCounter));
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        processedCounter ++;

        //TODO: Research why there are too much nulls and duplicated

        String newLine = values[0];
        onLineRead(newLine);
    }

    private void onLineRead(String newLine) {
        if(TextUtils.isEmpty(newLine)) {
            nullCounter++;
            return;
        }

        //Remove duplicated
        if(adaptor.getItemCount()>0){
            String previousLine = adaptor.getItemByPosition(adaptor.getItemCount()-1)
                    .getOriginalLine();
            if(newLine.equals(previousLine)){
                //TODO: Add a multiplicity counter to LogcatLine and increment it
                sameCounter ++;
                return;
            }
        }
        adaptor.add(newLine, id);
    }

    public void stopTask(){
        isRunning = false;
        this.cancel(true);
    }

    public void stopTask(Runnable callback){
        onCancelledCallback = callback;
        stopTask();
    }
}
