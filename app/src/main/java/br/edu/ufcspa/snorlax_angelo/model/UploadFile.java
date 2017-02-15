package br.edu.ufcspa.snorlax_angelo.model;

/**
 * Created by icaromsc on 15/02/2017.
 */

public class UploadFile {
    private Integer idRecordedFile;
    private String filename;

    public UploadFile(Integer idRecordedFile, String filename) {
        this.idRecordedFile = idRecordedFile;
        this.filename = filename;
    }

    public Integer getIdRecordedFile() {
        return idRecordedFile;
    }

    public void setIdRecordedFile(Integer idRecordedFile) {
        this.idRecordedFile = idRecordedFile;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "UploadFile{" +
                "idRecordedFile=" + idRecordedFile +
                ", filename='" + filename + '\'' +
                '}';
    }
}
