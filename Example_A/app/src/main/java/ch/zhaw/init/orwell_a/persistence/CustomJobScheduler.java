package ch.zhaw.init.orwell_a.persistence;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.PersistableBundle;
import android.util.Log;


public class CustomJobScheduler extends JobService {
    private static final String TAG = CustomJobScheduler.class.getCanonicalName();

    @Override
    public boolean onStartJob(JobParameters params) {
        PersistableBundle extras = params.getExtras();
        long interval = extras.getLong("interval", 1000 * 10L);
        setNewScheduler(interval);
        PersistenceService.startDataServices(this);
        Log.i(TAG, "JOB EXECUTED");
        return true;
    }

    /**
     * Starts the service again to schedule a new job.
     */
    private void setNewScheduler(long interval){
        Intent intent = new Intent(this, PersistenceService.class);
        intent.setAction("StartSpyJob");
        intent.putExtra("interval", interval);
        this.startForegroundService(intent);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "StopJobExecution");
        return false;
    }
}
