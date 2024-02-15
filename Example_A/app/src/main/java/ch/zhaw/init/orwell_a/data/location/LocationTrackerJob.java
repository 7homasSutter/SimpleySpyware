package ch.zhaw.init.orwell_a.data.location;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class LocationTrackerJob extends JobService {
    private static final String TAG = LocationTrackerJob.class.getCanonicalName();
    private static final int JOBID = 232;
    private static final long TIMING_JOB = 1000 * 20L;

    @Override
    public boolean onStartJob(JobParameters params) {
        scheduleNewTrackingJob();
        try{
            Intent locationServiceIntent = new Intent("GetSinglePositionUpdate");
            locationServiceIntent.setPackage(this.getPackageName());
            Log.i(TAG, "Invisible Location Tracker executed at: " + Calendar.getInstance().getTime());
            this.startForegroundService(locationServiceIntent);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "Job Stopped");
        return false;
    }


    private void scheduleNewTrackingJob(){
        ComponentName serviceComponent = new ComponentName(this, LocationTrackerJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOBID, serviceComponent);
        builder.setMinimumLatency(TIMING_JOB);
        builder.setRequiresBatteryNotLow(true);
        JobScheduler jobScheduler = this.getSystemService(JobScheduler.class);
        if(jobScheduler != null){
            jobScheduler.schedule(builder.build());
        }
    }
}
