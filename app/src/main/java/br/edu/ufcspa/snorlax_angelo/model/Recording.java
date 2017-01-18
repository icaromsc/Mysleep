package br.edu.ufcspa.snorlax_angelo.model;

import java.util.Date;

/**
 * Created by icaromsc on 18/01/2017.
 */

public class Recording {
    private Integer idRecording;
    private Date dateStart;
    private Date dateStop;
    private String status;

    public Recording(Date dateStart, Date dateStop, String status) {
        this.dateStart = dateStart;
        this.dateStop = dateStop;
        this.status = status;
    }

    public Integer getIdRecording() {
        return idRecording;
    }

    public void setIdRecording(Integer idRecording) {
        this.idRecording = idRecording;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateStop() {
        return dateStop;
    }

    public void setDateStop(Date dateStop) {
        this.dateStop = dateStop;
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
                ", dateStart=" + dateStart +
                ", dateStop=" + dateStop +
                ", status='" + status + '\'' +
                '}';
    }
}
