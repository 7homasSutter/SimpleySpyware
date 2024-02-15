package ch.zhaw.init.orwell_a.common;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileWriterInstance {
    private static final String TAG = FileWriterInstance.class.getCanonicalName();

    /**
     * Creates new file in internal memory
     */
    public static void toInternalStorage(byte[] bytes, String filename, Context ctx, boolean append) {
        File internalStorage = ctx.getFilesDir();
        String newFilePath = internalStorage.getAbsolutePath() + "/" + filename;
        File file = new File(newFilePath);
        try (final OutputStream output = new FileOutputStream(file, append)) {
            output.write(bytes);
        } catch (final IOException e) {
            Log.e(TAG, "FileWriterInstance Error: ", e);
        }
    }
}
