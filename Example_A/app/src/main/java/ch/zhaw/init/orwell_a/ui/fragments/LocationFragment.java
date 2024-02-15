package ch.zhaw.init.orwell_a.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ch.zhaw.init.orwell_a.R;
import ch.zhaw.init.orwell_a.common.FileReaderInstance;
import ch.zhaw.init.orwell_a.data.location.GPSLocation;
import ch.zhaw.init.orwell_a.ui.adapter.LocationAdapter;

public class LocationFragment extends Fragment {
    private static final String TAG = LocationFragment.class.getCanonicalName();
    private LocationAdapter locationAdapter;
    private TextView txtNumberOfLocations;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        String[] locationFiles = getLocationLogs();
        List<GPSLocation> gpsLocations = getGPSLocations(locationFiles);
        locationAdapter = new LocationAdapter(gpsLocations);
        recyclerView.setAdapter(locationAdapter);
        txtNumberOfLocations = rootView.findViewById(R.id.txt_LocationNumber);
        if(!gpsLocations.isEmpty()){
            txtNumberOfLocations.setText(String.valueOf(gpsLocations.size()));
        }

        return rootView;
    }

    /**
     * Reads the data from the location log file and creates a list of GPSLocations objects.
     * @param locationFiles audioplayer in csv format split with ";".
     * @return a list of all locations in the given audioplayer.
     */
    private List<GPSLocation> getGPSLocations(String[] locationFiles){
        ArrayList<GPSLocation> gpsLocations = new ArrayList<>();
        for(String locationFilePath: locationFiles){
            File locationFile = new File(locationFilePath);
            try {
                BufferedReader br = new BufferedReader(new FileReader(locationFile));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");
                    gpsLocations.add(new GPSLocation(data[0],data[1],data[2],data[3]));
                }
                br.close();
            }
            catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return gpsLocations;
    }

    private String[] getLocationLogs(){
        List<String> filesPaths = FileReaderInstance.readDirectory(Objects.requireNonNull(
                getActivity()).getFilesDir().toString(), "locations.log");
        return filesPaths.toArray(new String[0]);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
    }


    @Override
    public void onStart() {
        super.onStart();
        updateAdapter();
    }

    private void updateAdapter(){
        locationAdapter.setGpsLocationList(getGPSLocations(getLocationLogs()));
        locationAdapter.notifyDataSetChanged();
        if(locationAdapter.getItemCount() > 0){
            txtNumberOfLocations.setText(String.valueOf(locationAdapter.getItemCount()));
        }
    }

}
