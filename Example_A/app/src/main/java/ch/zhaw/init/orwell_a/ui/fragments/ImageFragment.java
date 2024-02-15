package ch.zhaw.init.orwell_a.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import ch.zhaw.init.orwell_a.R;
import ch.zhaw.init.orwell_a.common.FileReaderInstance;
import ch.zhaw.init.orwell_a.ui.adapter.ImageFileAdapter;

public class ImageFragment extends Fragment {
    private static final String TAG = ImageFragment.class.getCanonicalName();
    private ImageFileAdapter imageFileAdapter;
    private TextView txtNumberOfImages;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        Log.i(TAG, "Images found: " + getImageFilePaths().length);
        imageFileAdapter = new ImageFileAdapter(getImageFilePaths());
        recyclerView.setAdapter(imageFileAdapter);
        this.txtNumberOfImages = rootView.findViewById(R.id.txt_ImageNumber);
        txtNumberOfImages.setText(String.valueOf(getImageFilePaths().length));
        return rootView;
    }

    private String[] getImageFilePaths(){
        List<String> filesPaths = FileReaderInstance.readDirectory(getActivity().getFilesDir().toString(),
                ".jpg");
        Collections.sort(filesPaths, Collections.reverseOrder());
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
        imageFileAdapter.setImageFilePaths(getImageFilePaths());
        imageFileAdapter.notifyDataSetChanged();
        txtNumberOfImages.setText(String.valueOf(imageFileAdapter.getItemCount()));
    }
}
