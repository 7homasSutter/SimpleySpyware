package ch.zhaw.init.orwell_a.common;

import android.util.Log;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ch.zhaw.init.orwell_a.ui.fragments.AudioFragment;

public class FileReaderInstance {
    private static final String TAG = AudioFragment.class.getCanonicalName();


    /**
     * Gets a list of all audioplayer from the given directory.
     * @param path to be read recursive.
     * @param typeFilter the kind of file that will be searched. (.mp3,.jpeg, ...)
     * @return a file array with all audioplayer and directories.
     */
    public static List<String> readDirectory(String path, final String typeFilter){
        List<String> fileList = new ArrayList<>();
        Path rootPath = Paths.get(path);
        try (Stream<Path> stream = Files.walk(rootPath, Integer.MAX_VALUE)) {
            fileList = stream
                    .map(String::valueOf)
                    .filter(x -> x.contains(typeFilter))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return fileList;
    }
}
