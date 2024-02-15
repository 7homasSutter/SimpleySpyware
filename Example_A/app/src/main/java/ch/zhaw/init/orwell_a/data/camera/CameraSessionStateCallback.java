package ch.zhaw.init.orwell_a.data.camera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;

import androidx.annotation.NonNull;

public class CameraSessionStateCallback extends CameraCaptureSession.StateCallback {
    private static final String TAG = CameraSessionStateCallback.class.getCanonicalName();
    private CaptureRequest.Builder captureBuilder;

    CameraSessionStateCallback(CaptureRequest.Builder captureBuilder){
        this.captureBuilder = captureBuilder;
    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        try {
            CameraCaptureCallbackListener captureListener = new CameraCaptureCallbackListener();
            session.capture(captureBuilder.build(), captureListener, null);
        } catch (final CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        Log.e(TAG, "Error CameraSessionStateCallback");
    }
}
