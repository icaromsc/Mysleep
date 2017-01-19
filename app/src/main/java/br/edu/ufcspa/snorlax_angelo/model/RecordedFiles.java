package br.edu.ufcspa.snorlax_angelo.model;

/**
 * Created by icaromsc on 18/01/2017.
 */

public class RecordedFiles {
    private Integer idRecordedFile;
    private Integer idRecording;
    private String filename;
    private String status_upload;



    public final static String STATUS_PENDING_UPLOAD="P";
    public final static String STATUS_UPLOAD_FINISHED="F";


    public RecordedFiles(Integer idRecording, String filename, String status_upload) {
        this.idRecording = idRecording;
        this.filename = filename;
        this.status_upload = status_upload;
    }

    public RecordedFiles(Integer idRecordedFile, Integer idRecording, String filename, String status_upload) {
        this.idRecordedFile = idRecordedFile;
        this.idRecording = idRecording;
        this.filename = filename;
        this.status_upload = status_upload;
    }

    public Integer getIdRecordedFile() {
        return idRecordedFile;
    }

    public void setIdRecordedFile(Integer idRecordedFile) {
        this.idRecordedFile = idRecordedFile;
    }

    public Integer getIdRecording() {
        return idRecording;
    }

    public void setIdRecording(Integer idRecording) {
        this.idRecording = idRecording;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStatus_upload() {
        return status_upload;
    }

    public void setStatus_upload(String status_upload) {
        this.status_upload = status_upload;
    }


    @Override
    public String toString() {
        return "RecordedFiles{" +
                "idRecordedFile=" + idRecordedFile +
                ", idRecording=" + idRecording +
                ", filename='" + filename + '\'' +
                ", status_upload='" + status_upload + '\'' +
                '}';
    }
}
