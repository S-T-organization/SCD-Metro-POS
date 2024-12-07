package BarcodeScanner;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.sound.sampled.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RealTimeBarcodeScanner {
    static {
        System.loadLibrary("opencv_java4100"); // Load OpenCV native library
    }
    private static final int SERVER_PORT = 5050; // Server Port

    public void startServer() {
        System.out.println("Starting Scanner Server...");
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started on port: " + SERVER_PORT);
            Socket clientSocket = serverSocket.accept(); // Wait for the client
            System.out.println("Client connected.");

            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            startScanning(outputStream); // Start scanning barcodes
        } catch (Exception e) {
            System.out.println("Error in Scanner Server: " + e.getMessage());
        }
    }

    private void startScanning(DataOutputStream outputStream) {
        VideoCapture camera = new VideoCapture(0); // Default camera
        if (!camera.isOpened()) {
            System.out.println("Unable to access the camera.");
            return;
        }

        Mat frame = new Mat();
        System.out.println("Scanning started. Move a QR code in front of the camera...");

        try {
            while (true) {
                if (camera.read(frame)) {
                    String qrCode = detectQRCode(frame);
                    if (qrCode != null) {
                        System.out.println("QR Code Detected: " + qrCode);
                        Thread.sleep(500);
                        playBeepSound("C:\\Users\\user\\IdeaProjects\\Metro_POS\\src\\main\\resources\\beep_sound.wav");
                        outputStream.writeUTF(qrCode); // Send QR code to client
                        outputStream.flush();
                        Thread.sleep(1000); // Delay for next scan
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error during scanning: " + e.getMessage());
        } finally {
            camera.release();
        }
    }

    private String detectQRCode(Mat frame) {
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);

        MatLuminanceSource source = new MatLuminanceSource(gray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            MultiFormatReader reader = new MultiFormatReader();
            return reader.decode(bitmap).getText();
        } catch (NotFoundException e) {
            return null; // No QR code found
        }
    }

    // Custom LuminanceSource for OpenCV Mat
    private static class MatLuminanceSource extends LuminanceSource {
        private final byte[] luminance;

        protected MatLuminanceSource(Mat mat) {
            super(mat.width(), mat.height());
            if (mat.type() != CvType.CV_8UC1) {
                throw new IllegalArgumentException("Mat must be of type CV_8UC1");
            }
            luminance = new byte[mat.width() * mat.height()];
            mat.get(0, 0, luminance);
        }

        @Override
        public byte[] getRow(int y, byte[] row) {
            if (row == null || row.length < getWidth()) {
                row = new byte[getWidth()];
            }
            System.arraycopy(luminance, y * getWidth(), row, 0, getWidth());
            return row;
        }

        @Override
        public byte[] getMatrix() {
            return luminance;
        }
    }
    private static void playBeepSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }
}