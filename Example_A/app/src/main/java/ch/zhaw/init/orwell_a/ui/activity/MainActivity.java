package ch.zhaw.init.orwell_a.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.Manifest;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import com.google.android.material.tabs.TabLayout;
import ch.zhaw.init.orwell_a.R;
import ch.zhaw.init.orwell_a.common.AppPreferences;
import ch.zhaw.init.orwell_a.data.location.LocationTrackerService;
import ch.zhaw.init.orwell_a.persistence.CustomJob;
import ch.zhaw.init.orwell_a.ui.adapter.TabAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createTabs();
        //scheduleTest();
    }

    /**
     * Creates the main tabs.
     */
    private void createTabs(){
        final ViewPager viewPager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        String[] tabTitles = new String[]{getString(R.string.tab_spy), getString(R.string.tab_images),
                getString(R.string.tab_files), getString(R.string.tab_location)};
        final TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), 1,
                tabLayout.getTabCount(), tabTitles);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void onClickSpyCamera(View view){
        Log.i(TAG, "OnClickCamera");
        Switch switchButton = findViewById(R.id.cameraSwitch);
        setBoolPreference(AppPreferences.getPreferenceCamera(), switchButton.isChecked());
        checkPermission(Manifest.permission.CAMERA);
    }

    public void onClickSpyLocation(View view){
        Switch switchButton = findViewById(R.id.locationSwitch);
        setBoolPreference(AppPreferences.getPreferencesLocation(), switchButton.isChecked());
        checkPermission( Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void onClickSpyMicrophone(View view){
        Switch switchButton = findViewById(R.id.microphoneSwitch);
        setBoolPreference(AppPreferences.getPreferencesMicrophone(), switchButton.isChecked());
        checkPermission(Manifest.permission.RECORD_AUDIO);
    }

    public void onClickStopSpy(View view){
        Intent intent = new Intent("StopSpy");
        intent.setPackage(this.getPackageName());
        startForegroundService(intent);
    }

    public void onClickStartSpy(View view){
        Intent intent = null;
        RadioGroup radioGroup = findViewById(R.id.radioPersistence);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if(selectedId == R.id.radioAlarmManager){
            Log.i(TAG,"Use AlarmManger ");
            intent = new Intent("StartSpyAlarm");
        }else{
            Log.i(TAG,"Use CustomJobScheduler ");
            intent = new Intent("StartSpyJob");
        }
        EditText interval = findViewById(R.id.editText_interval);
        long invervalSeconds = Long.valueOf(interval.getText().toString());
        if(invervalSeconds < 0){
            invervalSeconds = 10L;
        }
        Log.i(TAG, "Interval chosen: " + invervalSeconds);
        intent.putExtra("interval", 1000L * invervalSeconds);
        intent.setPackage(this.getPackageName());
        startForegroundService(intent);
        Log.i(TAG, "Spy started");
    }

    private void setBoolPreference(String key, boolean value){
        getSharedPreferences(AppPreferences.getPreferencesName(), MODE_PRIVATE)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    private void checkPermission(String permission){
        boolean isPermissionMissing = false;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED )
        {
            isPermissionMissing = true;
        }
        if(isPermissionMissing){
            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
        }
    }

    public void scheduleTest(){
        Intent intent = new Intent(this, LocationTrackerService.class);
        intent.setAction("StartServiceLocation");
        startService(intent);

        long interval = 20000L;
        ComponentName serviceComponent = new ComponentName(this, CustomJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(101, serviceComponent);
        builder.setOverrideDeadline(interval * 2);
        builder.setMinimumLatency(interval); // Don't use periodic so we can schedule under 15min.
        builder.setPersisted(true);
        android.app.job.JobScheduler jobScheduler = this.getSystemService(android.app.job.JobScheduler.class);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }else{
            Log.e(TAG, "CustomJobScheduler could't start");
        }
    }

}
