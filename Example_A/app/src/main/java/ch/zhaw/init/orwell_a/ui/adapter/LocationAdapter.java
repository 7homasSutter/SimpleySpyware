package ch.zhaw.init.orwell_a.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.zhaw.init.orwell_a.R;
import ch.zhaw.init.orwell_a.data.location.GPSLocation;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder>  {
    private List<GPSLocation> gpsLocationList;

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        private TextView time;
        private TextView longitude;
        private TextView latitude;
        private TextView altitude;

        LocationViewHolder(View view) {
            super(view);
            this.time = view.findViewById(R.id.timestamp);
            this.longitude = view.findViewById(R.id.longitude);
            this.latitude = view.findViewById(R.id.latitude);
            this.altitude = view.findViewById(R.id.altitude);
        }
    }

    public LocationAdapter(List<GPSLocation> gpsLocationList){
        this.gpsLocationList = gpsLocationList;
    }

    public void setGpsLocationList(List<GPSLocation> gpsLocationList) {
        this.gpsLocationList = gpsLocationList;
    }

    @Override
    public LocationAdapter.LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_row,
                parent, false);
        return new LocationAdapter.LocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.LocationViewHolder holder, final int position) {
        long timeInms = Long.valueOf(gpsLocationList.get(position).getTime());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInms);
        Date date = c.getTime();
        SimpleDateFormat dfDate = new SimpleDateFormat("dd.MMM.yyyy hh:mm:ss");
        holder.time.setText(dfDate.format(date));
        holder.longitude.setText(gpsLocationList.get(position).getLongitude());
        holder.latitude.setText(gpsLocationList.get(position).getLatitude());
        holder.altitude.setText(gpsLocationList.get(position).getAltitude());
    }

    @Override
    public int getItemCount() {
        return gpsLocationList.size();
    }
}
