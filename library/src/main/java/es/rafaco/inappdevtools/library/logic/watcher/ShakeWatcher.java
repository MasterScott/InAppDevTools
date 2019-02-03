package es.rafaco.inappdevtools.library.logic.watcher;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import es.rafaco.inappdevtools.library.DevTools;

public class ShakeWatcher extends Watcher {

    private final SensorManager sensorManager;
    private final Sensor sensor;
    private InnerListener mListener;
    private InnerReceiver mReceiver;

    public ShakeWatcher(Context context) {
        super(context);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void setListener(Object listener) {
        mListener = (InnerListener) listener;
    }

    @Override
    public void start() {
        sensorManager.registerListener(mReceiver, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void stop() {
        sensorManager.unregisterListener(mReceiver);
    }

    class InnerReceiver implements SensorEventListener {

        private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
        private static final int SHAKE_SLOP_TIME_MS = 500;
        private long mShakeTimestamp;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }
                mShakeTimestamp = now;
                Log.d(DevTools.TAG, "RAFA - Shake action detected!");

                if (mListener!=null) mListener.onShake();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public interface InnerListener {
        void onShake();
    }
}
