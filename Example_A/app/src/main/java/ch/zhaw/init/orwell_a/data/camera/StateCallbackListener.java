package ch.zhaw.init.orwell_a.data.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;

import ch.zhaw.init.orwell_a.common.FileWriterInstance;
import ch.zhaw.init.orwell_a.common.Utils;

public class StateCallbackListener extends CameraDevice.StateCallback implements ImageReader.OnImageAvailableListener{
    private static final String TAG = StateCallbackListener.class.getCanonicalName();
    private boolean isCameraClosed;
    private CameraDevice cameraDevice;
    private CameraManager cameraManager;
    private Context ctx;
    private String currentCameraId;
    private Queue<String> cameraIds;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray(4);

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    StateCallbackListener(CameraManager cameraManager, Context ctx, Queue<String> cameraIds){
        this.cameraManager = cameraManager;
        this.ctx = ctx;
        this.cameraIds = cameraIds;
    }

    void takeNextPhoto() {
        if (ctx.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                if(!cameraIds.isEmpty()){
                    currentCameraId = cameraIds.poll();
                    Log.i(TAG, "OPEN CAMERA WITH ID: " + currentCameraId);
                    cameraManager.openCamera(currentCameraId, this, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getCameraOrientation() {
        WindowManager window = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        final int rotation = display.getRotation();
        return ORIENTATIONS.get(rotation);
    }

    private int getSurfaceOrientation() {
        CameraCharacteristics characteristics = null;
        try {
            characteristics = cameraManager.getCameraCharacteristics(cameraDevice.getId());
            return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        Log.i(TAG, "Camera onOpened: ID: " + camera.getId());
        isCameraClosed = false;
        cameraDevice = camera;
        new Handler().postDelayed(() -> { // Workaround: Delay against dark images on some devices.
            try {
                if(cameraDevice != null){
                    createImageReader();
                }
            } catch (Exception e) {
                Log.e(TAG, "Camera Error: " + e.getMessage());
            }
        }, 1000);
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        Log.d(TAG, "onDisconnected: ID: " + camera.getId());
        if (cameraDevice != null && !isCameraClosed) {
            isCameraClosed = true;
            cameraDevice.close();
        }
    }

    @Override
    public void onClosed(@NonNull CameraDevice camera) {
        isCameraClosed = true;
        Log.d(TAG, "OnClosed: ID: " + camera.getId());
        if (!cameraIds.isEmpty()) {
            takeNextPhoto();
        }
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        Log.e(TAG, "onError: ID: " + camera.getId() + " ErrorCode: " + error);
        if (cameraDevice != null) {
            cameraDevice.close();
            if (!cameraIds.isEmpty()) {
                takeNextPhoto();
            }
        }
    }

    private void createImageReader() throws CameraAccessException {
        final CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraDevice.getId());
        Size[] jpegSizes = null;
        StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (streamConfigurationMap != null) {
            jpegSizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
        }
        final boolean jpegSizesNotEmpty = jpegSizes != null && 0 < jpegSizes.length;
        int width = jpegSizesNotEmpty ? jpegSizes[0].getWidth() : 1980;
        int height = jpegSizesNotEmpty ? jpegSizes[0].getHeight() : 1020;

        ImageReader imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
        createCaptureSession(imageReader);
    }

    private void createCaptureSession(ImageReader imageReader) throws CameraAccessException {
        final List<Surface> outputSurfaces = new ArrayList<>();
        outputSurfaces.add(imageReader.getSurface());

        imageReader.setOnImageAvailableListener(this, null);

        final CaptureRequest.Builder captureBuilder = configureCaptureBuilder(imageReader);
        CameraSessionStateCallback cameraSessionStateCallback = new CameraSessionStateCallback(captureBuilder);
        cameraDevice.createCaptureSession(outputSurfaces, cameraSessionStateCallback, null);
    }

    private int jpegOrientation()  {
        int deviceRotation = getCameraOrientation();
        int sensorOrientation = getSurfaceOrientation();
        int surfaceRotation = ORIENTATIONS.get(deviceRotation);
        return (surfaceRotation + sensorOrientation + 270) % 360;
    }

    private CaptureRequest.Builder configureCaptureBuilder(ImageReader imageReader) throws CameraAccessException {
        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        captureBuilder.addTarget(imageReader.getSurface());
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, jpegOrientation());
        captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
        captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
        captureBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 6);
        captureBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);
        captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
        return captureBuilder;
    }

    @Override
    public void onImageAvailable(ImageReader imageReader) {
        Image image = imageReader.acquireLatestImage();
        if(image != null){
            cameraDevice.close();
            Log.i(TAG, "Image Available - height: " + image.getHeight() + " width: " + image.getWidth());
            byte[] bytes = getImageBytes(image);
            String filename = createFilename();
            FileWriterInstance.toInternalStorage(bytes, filename, ctx, false);
            Utils.removeOldImages(ctx);
        }
        if (image != null) {
            image.close();
        }
    }

    private String createFilename(){
        long time = Calendar.getInstance().getTime().getTime();
        return time + ".jpg";
    }

    /**
     * Converts a image object to a byte array.
     * @param image
     */
    private byte[] getImageBytes(Image image){
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return bytes;
    }

    /**
     * Convert bitmap to jpg.
     * @param bmp bitmap to be compressed to jpg format.
     * @return byte array of a jpg image.
     */
    private byte[] getBitmapBytes(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        return byteArray;
    }
}
