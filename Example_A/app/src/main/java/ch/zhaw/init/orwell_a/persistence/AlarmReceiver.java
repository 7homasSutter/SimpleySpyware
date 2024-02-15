package ch.zhaw.init.orwell_a.persistence;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getCanonicalName();
    private static boolean isRepeating;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "onReceive: " + intent);
        if(intent != null && intent.hasExtra("interval")){
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                Toast.makeText(context, "Alarm executed!", Toast.LENGTH_LONG).show();
                long interval = intent.getExtras().getLong("interval");
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
                wl.acquire(10L*1000L);
                PersistenceService.startDataServices(context);
                new Handler().postDelayed(() -> {// Workaround: Alarm Manager executes permanent on some devices.
                if(isRepeating){
                    setAlarm(context, intent);
                    Log.i(TAG, "Alarm set!");
                }
                }, interval);
                wl.release();
            }
        }
    }

    public void setAlarm(Context context, Intent intent)
    {
        isRepeating = true;
        long interval = intent.getLongExtra("interval", 1000L * 10L);

        Intent intentBroadcastReceiver = new Intent(context, AlarmReceiver.class);
        intentBroadcastReceiver.putExtras(intent);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intentBroadcastReceiver,
                PendingIntent.FLAG_ONE_SHOT);
        Log.e(TAG, "Schedule alarmInterval: " + interval);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(am != null){
            am.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, interval, pi);
        }
    }

    public void cancelAlarm(Context context, Intent alarmIntent)
    {
        isRepeating = false;
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtras(alarmIntent);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(sender);
        }
        PersistenceService.stopMicrophoneService(context);
    }

}
