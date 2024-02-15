package ch.zhaw.init.orwell_a.ui.fragments;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ch.zhaw.init.orwell_a.R;
import ch.zhaw.init.orwell_a.common.AppPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SpyFragment extends Fragment {
    private final String TAG = SpyFragment.class.getCanonicalName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spy, container, false);
        refreshStatus(rootView);
        initPreferences(rootView);
        return rootView;
    }

    private void initPreferences(View rootView){
        Switch switchButton = rootView.findViewById(R.id.cameraSwitch);
        initSwitchButton(switchButton, AppPreferences.getPreferenceCamera());
        switchButton = rootView.findViewById(R.id.locationSwitch);
        initSwitchButton(switchButton, AppPreferences.getPreferencesLocation());
        switchButton = rootView.findViewById(R.id.microphoneSwitch);
        initSwitchButton(switchButton, AppPreferences.getPreferencesMicrophone());
    }

    /**
     * Init switch button with last set preference.
     */
    private void initSwitchButton(Switch switchButton, String preferenceName){
        Context ctx = getContext();
        if(ctx != null){
            SharedPreferences sh = ctx.getSharedPreferences(AppPreferences.getPreferencesName(), MODE_PRIVATE);
            boolean isActive = sh.getBoolean(preferenceName, false);
            sh.edit().putBoolean(preferenceName, isActive).apply();
            switchButton.setChecked(isActive);
        }
    }

    private void refreshStatus(View rootView){
        Handler handler = new Handler();
        Runnable statusUpdate = new Runnable() {
            @Override
            public void run() {
                TextView logStatus = rootView.findViewById(R.id.logs_status);
                Context ctx = getContext();
                if(ctx != null && logStatus != null){
                    if(isJobRunning()){
                        String running = ctx.getString(R.string.title_status) + " Running";
                        logStatus.setText(running);
                    }else{
                        String stopped = ctx.getString(R.string.title_status) + " Stopped";
                        logStatus.setText(stopped);
                    }
                }
                handler.postDelayed(this, 15000);
            }
        };
        handler.post(statusUpdate);
    }

    private boolean isJobRunning(){
        Context ctx = getContext();
        if(ctx != null){
            JobScheduler scheduler = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            return scheduler != null && !scheduler.getAllPendingJobs().isEmpty();
        }
        return false;
    }
}
