package br.edu.ufcspa.snorlax_angelo.view;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import br.edu.ufcspa.snorlax_angelo.database.DataBaseAdapter;
import br.edu.ufcspa.snorlax_angelo.model.RecordedFiles;
import br.edu.ufcspa.snorlax_angelo.view.AudioRecorderActivity;

/**
 * Created by Carlos on 03/10/2016.
 */
public class UploadFileAsync extends AsyncTask<String, Void, String> {
    String sourceFileUri;
    Integer idRecordedFile=0;
    @Override
    protected String doInBackground(String... params) {
        Log.d("app", "open task...");
        try {
            sourceFileUri = params[0];
            idRecordedFile=Integer.valueOf(params[1]);
            Log.d("app","async task source file:"+sourceFileUri);
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 10 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);
            int serverResponseCode;

            if (sourceFile.isFile()) {

                try {
                    String upLoadServerUri = "http://angelo.inf.ufrgs.br/snorlax/UploadToServer.php";

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    //fileInputStream=inAudioData;
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();

                    Log.d("app","File upload started...");
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE",
                            "multipart/form-data");
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploadedfile", sourceFileUri);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                            + sourceFileUri + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    Log.d("app", "uploading file...");
                    while (bytesRead > 0) {
                       // Log.d("-while (bytesRead > 0)", " ");
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math
                                .min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,
                                bufferSize);
                    }

                    // send multipart form data necesssary after file
                    // data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens
                            + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn
                            .getResponseMessage();

                    if (serverResponseCode == 200) {
                        Log.d("app","File upload complete");
                        Log.d("app", serverResponseMessage);
                        updateStatusOnDatabase();
                        deleteTempFile(sourceFileUri);
                        //recursiveDelete(mDirectory1);

                    }else{
                        Log.e("app","Error, server returned: "+serverResponseCode+"/n"+serverResponseMessage);
                    }

                    // close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (Exception e) {

                    // dialog.dismiss();
                    e.printStackTrace();

                }
                // dialog.dismiss();

            }else{
                Log.d("app","file no finded");
                // End else block
                Log.d("app", sourceFile.getAbsolutePath());
            }


        } catch (Exception ex) {
            // dialog.dismiss();

            ex.printStackTrace();
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        //AudioRecorderActivity.uploadingFile=false;
        Log.d("app","finish task");

    }

    @Override
    protected void onPreExecute() {
        //AudioRecorderActivity.uploadingFile=true;
    }

    @Override
    protected void onProgressUpdate(Void... values) {

    }


    private void deleteTempFile(String filename) {
        File file = new File(filename);
        try {
            boolean v=file.getCanonicalFile().delete();
            Log.w("app", "File deleted: " + file +" state:" + v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStatusOnDatabase(){
        DataBaseAdapter data = DataBaseAdapter.getInstance(null);
        RecordedFiles r = new RecordedFiles(idRecordedFile,RecordedFiles.STATUS_UPLOAD_FINISHED);
        data.updateStatusRecordedFile(r);
    }


}