package ca.dmi.uqtr.applicationchat;
import android.media.MediaRecorder;
import java.io.IOException;

public class AudioRecorderUtil {

    private static MediaRecorder mediaRecorder;
    private static String outputFile;

    public static void startRecording(String outputPath) {
        outputFile = outputPath;

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(outputFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
        }

        mediaRecorder.start();
    }

    public static void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }


}
