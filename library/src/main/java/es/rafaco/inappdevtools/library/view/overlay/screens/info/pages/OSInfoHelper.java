package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.lang.reflect.Field;
import java.util.Locale;

import es.rafaco.inappdevtools.library.logic.utils.InstalledAppsUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import github.nisrulz.easydeviceinfo.base.EasyConfigMod;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;
import github.nisrulz.easydeviceinfo.base.EasyMemoryMod;
import github.nisrulz.easydeviceinfo.base.RingerMode;

public class OSInfoHelper extends AbstractInfoHelper  {

    EasyConfigMod configHelper;
    EasyDeviceMod deviceHelper;
    EasyMemoryMod memoryHelper;

    public OSInfoHelper(Context context) {
        super(context);
        this.configHelper = new EasyConfigMod(context);
        this.deviceHelper = new EasyDeviceMod(context);
        this.memoryHelper = new EasyMemoryMod(context);
    }

    @Override
    public String getOverview() {

        return "Android " + getAndroidVersionFull()
                + (deviceHelper.isDeviceRooted() ? " [Rooted]" : "") + Humanizer.newLine()
                + getDisplayLanguage() + " - " + getDisplayCountry() + Humanizer.newLine()
                + InstalledAppsUtils.getCount() + " installed apps";
    }

    public String getDisplayLanguage(){
        String langCode = deviceHelper.getLanguage();
        Locale loc = new Locale(langCode);
        String name = loc.getDisplayLanguage(loc);
        return TextUtils.isEmpty(name) ? langCode : name;
    }

    public String getDisplayCountry(){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String code = tm.getNetworkCountryIso();
        Locale loc = new Locale("", code);
        return loc.getDisplayCountry();
    }


    @Override
    public InfoReport getInfoReport() {
        return new InfoReport.Builder("")
                .add(getAndroidGroup(deviceHelper))
                .add(getConfigGroup(configHelper, deviceHelper))
                .add(getMemoryGroupGroup(configHelper, memoryHelper))
                .add(getInstalledApps())
                .build();
    }

    protected InfoGroup getAndroidGroup(EasyDeviceMod deviceHelper) {
        return new InfoGroup.Builder("")
                .add("Android version", getAndroidVersionFull())
                .add("Android SDK version",  getVersionCodeName()+ " (" + Build.VERSION.SDK_INT + ")")
                .add("isRooted", deviceHelper.isDeviceRooted())
                    .build();
    }

    private String getAndroidVersionFull() {
        if (getOsCodename() != null){
            return getOsCodename() + " (" + Build.VERSION.RELEASE + ")";
        }
        else{
            return Build.VERSION.RELEASE;
        }
    }

    private String getOsCodename() {
        //Parsing modern values not included in DeviceInfo
        if (Build.VERSION.SDK_INT >= 10){
            return null;
        }
        else if (getVersionCodeName().equals("O")){
            return "Oreo";
        }else if (getVersionCodeName().equals("P")){
            return "Pie";
        }else{
            return deviceHelper.getOSCodename();
        }
    }

    protected InfoGroup getConfigGroup(EasyConfigMod configHelper, EasyDeviceMod deviceHelper) {
        return new InfoGroup.Builder("Android")
                    .add("Local time", configHelper.getFormattedTime()
                            + " - " + configHelper.getFormattedDate())
                    .add("Up time", configHelper.getFormattedUpTime())
                    .add("Language", deviceHelper.getLanguage())
                    .add("Ringer mode", parseRingerMode(configHelper.getDeviceRingerMode()))
                    .build();
    }

    protected InfoGroup getMemoryGroupGroup(EasyConfigMod configHelper, EasyMemoryMod memoryHelper) {
        return new InfoGroup.Builder("Memory & Storage")
                    .add("RAM", Humanizer.parseByte(memoryHelper.getTotalRAM()))
                    .add("Internal",  Humanizer.parseByte(memoryHelper.getAvailableInternalMemorySize())
                            + "/" + Humanizer.parseByte(memoryHelper.getTotalInternalMemorySize()))
                    .add("External", Humanizer.parseByte(memoryHelper.getAvailableExternalMemorySize())
                            + "/" + Humanizer.parseByte(memoryHelper.getTotalExternalMemorySize()))
                    .add("hasSDCard", configHelper.hasSdCard())
                    .build();
    }

    protected InfoGroup getInstalledApps() {
        return new InfoGroup.Builder("Installed Apps")
                .add(InstalledAppsUtils.getString())
                .build();
    }


    @NonNull
    private String parseRingerMode(int deviceRingerMode) {
        String ringerMode;
        switch (deviceRingerMode) {
            case RingerMode.NORMAL:
                ringerMode = "Normal";
                break;
            case RingerMode.SILENT:
                ringerMode = "Silent";
                break;
            case RingerMode.VIBRATE:
                ringerMode = "Vibrate";
                break;
            default:
                ringerMode = "Unknown";
                break;
        }
        return ringerMode;
    }

    private String getVersionCodeName(){
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT].getName();
        return osName;
    }
}
