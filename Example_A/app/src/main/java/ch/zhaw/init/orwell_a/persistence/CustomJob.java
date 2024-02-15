package ch.zhaw.init.orwell_a.persistence;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.util.Log;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ch.zhaw.init.orwell_a.data.location.LocationTrackerService;

import static android.Manifest.permission.RECORD_AUDIO;

public class CustomJob extends JobService {
    private static final String TAG = CustomJob.class.getCanonicalName();
    private MediaRecorder mediaRecorder;
    private String outPutFile;
    private static boolean isRecording;

    @Override
    public boolean onStartJob(JobParameters params) {
        newJob();
        if(checkPermission()){
            startAudioCapturing();
        }else{
            Log.e(TAG, "No Audio Permission");
        }
        Intent intent = new Intent(this, LocationTrackerService.class);
        intent.setAction("StartServiceLocation");
        startService(intent);

        Log.e(TAG, "CustomJob EXECUTED");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "StopJobExecution");
        return false;
    }

    private void newJob(){
        long interval = 10000L;
        ComponentName serviceComponent = new ComponentName(this, CustomJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(101, serviceComponent);
        builder.setOverrideDeadline(interval * 2);
        builder.setMinimumLatency(interval); // Don't use periodic so we can schedule under 15min.
        builder.setPersisted(true);
        JobScheduler jobScheduler = this.getSystemService(JobScheduler.class);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }else{
            Log.e(TAG, "CustomJobScheduler could't start");
        }
    }

    /**
     * Start capturing an audio file.
     */
    private void startAudioCapturing(){
        File internalStorage = getFilesDir();
        outPutFile = createFileName();
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
        mediaRecorder.setMaxDuration(1000 * 10 * 60); // Set a maximum duration for one file
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Creates a random file name.
     * @return File-name based on timestamp and a constant string.
     */
    private String createFileName(){
        StringBuilder stringBuilder = new StringBuilder();
        long time = Calendar.getInstance().getTimeInMillis();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        Date date = c.getTime();
        SimpleDateFormat dfDate = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss_SS");
        stringBuilder.append(dfDate.format(date));
        return stringBuilder.toString();
    }

}
