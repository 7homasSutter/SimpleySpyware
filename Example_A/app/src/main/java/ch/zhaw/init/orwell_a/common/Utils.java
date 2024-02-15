package ch.zhaw.init.orwell_a.common;

import android.content.Context;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    /**
     * Creates a random file name.
     * @return File-name based on timestamp and a constant string.
     */
    public static String createFileName(){
        StringBuilder stringBuilder = new StringBuilder();
        long time = Calendar.getInstance().getTimeInMillis();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        Date date = c.getTime();
        SimpleDateFormat dfDate = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss_SS");
        stringBuilder.append(dfDate.format(date));
        return stringBuilder.toString();
    }

    public static void removeOldImages(Context ctx){
        File internalStorage = ctx.getFilesDir();
        List<String> imagesPaths = FileReaderInstance.readDirectory(internalStorage.getAbsolutePath(),".jpg");
        int imgCount = imagesPaths.size();
        for(int i = imgCount; i > 0; i--){
            if(imagesPaths.size() < 100){
                break;
            }
            try {
                File file = new File(imagesPaths.get(i-1));
                Files.delete(file.toPath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
