package ch.zhaw.init.orwell_a.data.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;


public class CameraCaptureCallbackListener extends CameraCaptureSession.CaptureCallback {
    private static final String TAG = CameraCaptureCallbackListener.class.getCanonicalName();


    @Override
    public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                   @NonNull CaptureRequest request,
                                   @NonNull TotalCaptureResult result) {
        super.onCaptureCompleted(session, request, result);

        List<CaptureResult> resultList = result.getPartialResults();
        for(CaptureResult captureResult : resultList){
            Log.i(TAG, "resultList: " + captureResult.getSequenceId());
        }
        session.close();
    }
}
