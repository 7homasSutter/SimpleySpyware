package ch.zhaw.init.orwell_a.ui.adapter;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import ch.zhaw.init.orwell_a.R;

public class AudioFileAdapter extends RecyclerView.Adapter<AudioFileAdapter.AudioFileViewHolder> {
    private String[] audioFilePaths;
    private ImageButton play;
    private ImageButton pause;
    private ImageButton stop;
    private String currentTrack;
    private double currentTimePosition = 0;
    private double finalTime = 0;
    private SeekBar seekbar;
    private MediaPlayer mediaPlayer;
    private int currentState;
    private Timer timer = new Timer();
    private enum playerState {
        PLAYING(0), STOPPED(10), PAUSED(5);
        private final int value;
        playerState(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    static class AudioFileViewHolder extends RecyclerView.ViewHolder {
        private TextView filePath;

        AudioFileViewHolder(View view) {
            super(view);
            this.filePath = view.findViewById(R.id.audioFileName);
        }
    }

    public AudioFileAdapter(String[] audioFilePaths, View rootView) {
        this.audioFilePaths = audioFilePaths;
        this.play = rootView.findViewById(R.id.btn_play);
        this.pause = rootView.findViewById(R.id.btn_pause);
        this.stop = rootView.findViewById(R.id.btn_stop);
        this.mediaPlayer = new MediaPlayer();
        this.seekbar = rootView.findViewById(R.id.seekBar);
        currentState = playerState.STOPPED.getValue();
        if(audioFilePaths.length > 0){
            try {
                mediaPlayer.setDataSource(audioFilePaths[0]);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            play.setEnabled(false);
            pause.setEnabled(false);
            stop.setEnabled(false);
        }
        setButtonClickListener();
        setSeekbarListener();
    }

    public void setAudioFilePaths(String[] audioFilePaths) {
        this.audioFilePaths = audioFilePaths;
    }

    private void setButtonClickListener(){
        play.setOnClickListener(view -> play());
        pause.setOnClickListener(view -> pause());
        stop.setOnClickListener(view -> stop());
    }

    private void setSeekbarListener(){
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                    seekbar.setProgress((int) currentTimePosition);
                }
            }
        });
    }

    @Override
    public AudioFileAdapter.AudioFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_file_row,
                parent, false);
        return new AudioFileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioFileViewHolder holder, final int position) {
        final File file = new File(audioFilePaths[position]);
        holder.filePath.setText(file.getName());
        holder.filePath.setOnClickListener(view -> {
            try {
                if(currentState == playerState.STOPPED.getValue()){
                    mediaPlayer = new MediaPlayer();
                    currentTrack = audioFilePaths[position];
                    mediaPlayer.setDataSource(currentTrack);
                    mediaPlayer.prepare();
                    play();
                }
                if(currentState == playerState.PAUSED.getValue()){
                    resume();
                }
                if(currentState == playerState.PLAYING.getValue() && audioFilePaths[position].equals(currentTrack)){
                    pause();
                }else{
                    stop();
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(audioFilePaths[position]);
                    mediaPlayer.prepare();
                    play();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return audioFilePaths.length;
    }

    private void pause(){
        play.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(true);
        mediaPlayer.pause();
        currentState = playerState.PAUSED.getValue();
    }

    private void stop(){
        play.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(false);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePaths[0]);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentState = playerState.STOPPED.getValue();
        timer.cancel();
    }

    private void resume(){
        mediaPlayer.start();
        currentState = playerState.PLAYING.getValue();
    }

    private void play(){
        mediaPlayer.start();
        finalTime = mediaPlayer.getDuration();
        currentTimePosition = mediaPlayer.getCurrentPosition();
        seekbar.setMax((int) finalTime);
        seekbar.setProgress((int) currentTimePosition);
        play.setEnabled(false);
        pause.setEnabled(true);
        stop.setEnabled(true);
        currentState = playerState.PLAYING.getValue();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                currentTimePosition = mediaPlayer.getCurrentPosition();
                seekbar.setProgress((int) currentTimePosition);
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }
}
