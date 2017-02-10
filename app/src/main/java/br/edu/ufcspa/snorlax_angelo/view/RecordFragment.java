package br.edu.ufcspa.snorlax_angelo.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.edu.ufcspa.snorlax_angelo.AppLog;
import br.edu.ufcspa.snorlax_angelo.database.DataBaseAdapter;
import br.edu.ufcspa.snorlax_angelo.model.RecordedFiles;
import br.edu.ufcspa.snorlax_angelo.model.Recording;
import br.edu.ufcspa.snorlax_angelo.view.UploadFileAsync;
import ufcspa.edu.br.snorlax_angelo.R;




public class RecordFragment extends Fragment {


    private long record_size = 60000; //1 minute
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "Snore_angELO";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 44100;
    static boolean uploadingFile;

    String fileToBeUploaded;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    FileInputStream inAudioData = null;
    private static final long BYTES_HORA = 320197200;
    String filename;
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private Thread processingThread = null;
    private boolean isRecording = false;
    private boolean isProcessing = false;
    String fileToprocess = "";
    private int tempNumber=0;


    int serverResponseCode = 0;
    private ProgressDialog dialog = null;

    String upLoadServerUri = "http://angelo.inf.ufrgs.br/snorlax/UploadToServer.php";

    private Chronometer cronometro;
    private Button btn_gravacao;
    private TextView txt_status;
    private TextView txt_cap2;

    private AlertDialog alerta;
    private AlertDialog.Builder builder;
    private RelativeLayout recording_message;
    private SeekBar seekBar;

    View myView;
    private int idRecording = 0;










    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        myView= inflater.inflate(R.layout.fragment_record, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
        recording_message = (RelativeLayout) myView.findViewById(R.id.record_message_layout);
        cronometro = (Chronometer) getView().findViewById(R.id.cronometro);


        btn_gravacao = ((Button) getView().findViewById(R.id.btn_gravacao));
        btn_gravacao.setOnClickListener(btnClick);

        txt_status = ((TextView)getView().findViewById(R.id.txt_status));

        builder = new AlertDialog.Builder(getView().getContext());
        builder.setTitle("Snore | angELO");
        builder.setMessage("Gravação finalizada e salva na pasta Snore_angELO!");
        builder.setCancelable(false);
        //define um botão como positivo
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });                                      //:)
        alerta = builder.create();



    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(!isRecording) {
                if(getActivity()!=null) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                AppLog.logString("Start Recording");
                recording_message.setVisibility(View.VISIBLE);
                startRecording();
                cronometro.setBase(SystemClock.elapsedRealtime());
                cronometro.start();
                btn_gravacao.setText(getString(R.string.btn_stop));
                txt_status.setText(getString(R.string.recording));
                Toast.makeText(getView().getContext(), "Gravação Iniciada!", Toast.LENGTH_SHORT).show();
            }
            else{
                AppLog.logString("Stop Recording");
                recording_message.setVisibility(View.INVISIBLE);
                stopRecording();
                cronometro.stop();

                alerta.show();

                cronometro.setBase(SystemClock.elapsedRealtime());

                btn_gravacao.setText(getString(R.string.btn_start));
                txt_status.setText(getString(R.string.start_capture));
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }

        }
    };



    private String getTempFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }
        String filename=createFilename();
        File tempFile = new File(filepath,filename+".raw");

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + filename+".raw");
    }

    private String createFilename(){
        tempNumber++;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        strDate+="_temp_"+tempNumber;
        return strDate;
    }

    private String getFinalTempFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private void startRecording(){
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

        recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                getAudioData();
            }
        },"AudioRecorderActivity Thread");

        recordingThread.start();

    }




    /* RECORDING THREAD */
    private void getAudioData(){
        fileToprocess = "";
        isProcessing = true;
        idRecording = saveRecordingOnDatabase();
        while(isRecording) {
            fileToprocess = writeAudioDataToFile(record_size);
            saveTempRecordingFile(fileToprocess,idRecording);
        }
        isProcessing = false;
    }

    private String writeAudioDataToFile(long record_size){
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;
        Long minuteIni;
        Long minuteAtu;
        boolean limiteTime = true;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;

        Log.d("app","*** INICIANDO GRAVAÇÃO");
        if(null != os){
            minuteIni = cronometro.getDrawingTime();
            while(isRecording && limiteTime){
                read = recorder.read(data, 0, bufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {

                        minuteAtu = cronometro.getDrawingTime();
                        if ((minuteAtu - minuteIni) > record_size){
                            Log.d("app","*** UM MINUTO: " + cronometro.getDrawingTime());
                            limiteTime = false;
                        }
                        else{
                            os.write(data);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return filename;
    }

    private void stopRecording(){
        if(null != recorder){
            isRecording = false;

            recorder.stop();
            recorder.release();
            // update on SQLITE recording status and
            updateRecording(idRecording,getActualDate());
            recorder = null;
            recordingThread = null;

            processingThread = null;
            listRecordings();

        }

    }

    private void deleteFinalTempFile() {
        File file = new File(getFinalTempFilename());
        file.delete();
    }

    private void deleteTempFile(String filename) {
        File file = new File(filename);
        file.delete();
    }


    private void saveTempRecordingFile(String filename,int idRecording){
        Log.d("database","salvando temp recording file[ filename: "+filename+" idRec:"+idRecording+" ]");
        RecordedFiles rec = new RecordedFiles(idRecording,filename,null);
        DataBaseAdapter data = DataBaseAdapter.getInstance(getActivity());
        data.insertRecordedFile(rec);
    }


    private String getActualDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }


    private void updateRecording(int idRecording,String date){
        DataBaseAdapter data = DataBaseAdapter.getInstance(getActivity());
        Recording recording = new Recording(idRecording,null,date,null);
        if(data.updateFinalizeRecording(recording))
            Log.d("database","updated recording[ id: "+idRecording+" stopDate:"+date+" ]");
        else
            Log.e("database","error updating recording");
    }


    private int saveRecordingOnDatabase(){
        DataBaseAdapter data = DataBaseAdapter.getInstance(getActivity());
        return data.insertRecording(new Recording(0,getActualDate(),null,null));
    }

    private void listRecordings() {
        DataBaseAdapter data = DataBaseAdapter.getInstance(getActivity());
        ArrayList<RecordedFiles> recordedFilesArrayList = (ArrayList<RecordedFiles>) data.getRecordedFiles();
        ArrayList<Recording> recordingArrayList = (ArrayList<Recording>) data.getRecordings();

        if (recordedFilesArrayList.size() > 0) {
            for (RecordedFiles f : recordedFilesArrayList) {
                Log.d("database", "recorded file: " + f.toString());
            }
        }
        if (recordingArrayList.size()>0){
            for(Recording r : recordingArrayList){
                Log.d("database", "recording: " + r.toString());
            }
        }
    }





}
