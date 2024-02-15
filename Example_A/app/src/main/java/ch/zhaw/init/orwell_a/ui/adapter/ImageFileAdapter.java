package ch.zhaw.init.orwell_a.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import ch.zhaw.init.orwell_a.R;


public class ImageFileAdapter extends RecyclerView.Adapter<ImageFileAdapter.ImageFileViewHolder> {
    private String[] imageFilePaths;

    static class ImageFileViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        ImageFileViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.imageView);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    public ImageFileAdapter(String[] imageFilePths) {
        this.imageFilePaths = imageFilePths;
    }

    public void setImageFilePaths(String[] imageFilePaths) {
        this.imageFilePaths = imageFilePaths;
    }

    @Override
    public ImageFileAdapter.ImageFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_file_row,
                parent, false);

        return new ImageFileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageFileViewHolder holder, final int position) {
        final File file = new File(imageFilePaths[position]);
        Uri fileUri = Uri.fromFile(file);
        Picasso.get()
                .load(fileUri)
                .resize(400, 400)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageFilePaths.length;
    }
}
