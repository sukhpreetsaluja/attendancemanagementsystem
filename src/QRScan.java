package src;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.sql.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class QRScan implements Runnable, ThreadFactory {

    private VideoCapture webcam;
    private Executor executor = Executors.newSingleThreadExecutor(this);
    private JFrame webcamFrame = null;
    private volatile boolean running = true;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/attendance";
    private static final String USER = "root";
    private static final String PASS = "meow";

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    public QRScan() {
        initializeWebcam();
    }

    private BufferedImage matToBufferedImage(Mat frame) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (frame.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = frame.channels() * frame.cols() * frame.rows();
        byte[] b = new byte[bufferSize];
        frame.get(0, 0, b);
        BufferedImage image = new BufferedImage(frame.cols(), frame.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    private void initializeWebcam() {

        webcam = new VideoCapture(0);
        if (!webcam.isOpened()) {
            System.err.println("Error: Cannot open webcam!");
            return;
        }

        webcamFrame = new JFrame("QR Scanner");
        webcamFrame.setSize(640, 480);
        webcamFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        webcamFrame.setVisible(true);
        executor.execute(this);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(100);

                Mat frame = new Mat();
                if (webcam.read(frame)) {
                    BufferedImage image = matToBufferedImage(frame);

                    if (image != null) {
                        LuminanceSource source = new BufferedImageLuminanceSource(image);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                        try {
                            Result result = new MultiFormatReader().decode(bitmap);
                            if (result != null) {
                                String qrText = result.getText();
                                System.out.println("QR Code detected: " + qrText);
                                recordAttendance(qrText);
                                JOptionPane.showMessageDialog(null, "Attendance Recorded: " + qrText);

                                stopScanning();
                            }
                        } catch (NotFoundException e) {
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void recordAttendance(String empId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "INSERT INTO attendance (emp_details) VALUES (?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, empId);

            pstmt.executeUpdate();
            System.out.println("Attendance Recorded: " + empId);

        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void stopScanning() {
        running = false;
        if (webcam.isOpened()) {
            webcam.release();
        }
        webcamFrame.dispose();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "QR Scanner Thread");
        t.setDaemon(true);
        return t;
    }

    public static void main(String[] args) {
        JFrame dashboard = new JFrame("Dashboard");
        dashboard.setLayout(new FlowLayout());

        JButton scanButton = new JButton("Start Scanning");
        JButton stopButton = new JButton("Stop Scanning");

        QRScan qrScanner = new QRScan();
        scanButton.addActionListener(e -> qrScanner.initializeWebcam());
        stopButton.addActionListener(e -> qrScanner.stopScanning());    

        dashboard.add(scanButton);
        dashboard.add(stopButton);
        dashboard.setSize(400, 200);
        dashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboard.setVisible(true);
    }
}
