package ch.zhaw.init.orwell_a.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Objects;
import ch.zhaw.init.orwell_a.R;
import ch.zhaw.init.orwell_a.common.FileReaderInstance;
import ch.zhaw.init.orwell_a.ui.activity.MainActivity;
import ch.zhaw.init.orwell_a.ui.adapter.AudioFileAdapter;

public class AudioFragment extends Fragment {
    private AudioFileAdapter audioFileAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.audioplayer, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        String[] audioFiles = getAudioFilePaths();
        audioFileAdapter = new AudioFileAdapter(audioFiles, rootView);
        recyclerView.setAdapter(audioFileAdapter);
        return rootView;
    }

    private String[] getAudioFilePaths(){
        List<String> filesPaths = FileReaderInstance.readDirectory(Objects.requireNonNull(
                getActivity()).getFilesDir().toString(), ".mp3");
        return filesPaths.toArray(new String[0]);
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] audioFiles = getAudioFilePaths();
        audioFileAdapter.setAudioFilePaths(audioFiles);
        audioFileAdapter.notifyDataSetChanged();
    }
}
