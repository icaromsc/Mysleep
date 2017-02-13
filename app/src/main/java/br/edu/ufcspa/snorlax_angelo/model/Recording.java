package br.edu.ufcspa.snorlax_angelo.model;

import java.util.Date;

/**
 * Created by icaromsc on 18/01/2017.
 */

public class Recording {
    private Integer idRecording;
    private String dateStart;
    private String dateStop;
    private String status;




    public final static String STATUS_PROCESSING="P";
    public final static String STATUS_UPLOADING ="U";
    public final static String STATUS_FINISHED ="F";
    /*
    *  flags used in status
    *
    * */

    public Recording(Integer idRecording, String dateStart, String dateStop, String status) {
        this.idRecording = idRecording;
        this.dateStart = dateStart;
        this.dateStop = dateStop;
        this.status = status;
    }





    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateStop() {
        return dateStop;
    }

    public void setDateStop(String dateStop) {
        this.dateStop = dateStop;
    }

    public Integer getIdRecording() {
        return idRecording;
    }

    public void setIdRecording(Integer idRecording) {
        this.idRecording = idRecording;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Recording{" +
                "idRecording=" + idRecording +
                ", dateStart='" + dateStart + '\'' +
                ", dateStop='" + dateStop + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}


