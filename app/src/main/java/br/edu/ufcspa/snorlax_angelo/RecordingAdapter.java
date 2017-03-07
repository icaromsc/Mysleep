package br.edu.ufcspa.snorlax_angelo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.edu.ufcspa.snorlax_angelo.model.Recording;
import ufcspa.edu.br.snorlax_angelo.R;

/**
 * Created by icaromsc on 14/02/2017.
 */

public class RecordingAdapter extends BaseAdapter {

    private final List<Recording> recs;
    private Context context;
    private static LayoutInflater inflater=null;
    public RecordingAdapter(List<Recording> recs, Context ctx) {
        this.recs = recs;
        this.context= ctx;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return recs.size();
    }

    @Override
    public Object getItem(int position) {
        return recs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recs.get(position).getIdRecording();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        view =inflater.inflate(R.layout.record_item, parent, false);
        Recording recording = recs.get(position);
        TextView title = (TextView)
                view.findViewById(R.id.txt_item_title);
        TextView horario = (TextView)
                view.findViewById(R.id.txt_item_horario);
        TextView txDuracao = (TextView)
                view.findViewById(R.id.txt_item_duracao);
        TextView status = (TextView)
                view.findViewById(R.id.txt_item_status);


        String duracao = getDuration(recording.getDateStart(),recording.getDateStop());

        title.setText(getTitle(recording.getDateStart()));
        horario.setText("horário:"+getHour(recording.getDateStart()));
        txDuracao.setText("duração:"+duracao);
        status.setText(getStatus(recording.getStatus()));
        return view;

    }

    public String getDuration(String dateStart,String dateStop) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Date myDate1 = null;
        Date myDate2 = null;
        try {
            myDate1 = (Date)formatter.parse(dateStart);
            myDate2 = (Date)formatter.parse(dateStop);
            long start = myDate1.getTime();
            long stop = myDate2.getTime();
            DateFormat result = new SimpleDateFormat("HH:mm:ss");
            String strDate = result.format((stop-start));
            return strDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }


    public String getHour(String dateStop){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Date myDate1 = null;
        try {
            myDate1 = (Date)formatter.parse(dateStop);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long stop = myDate1.getTime();
        DateFormat result = new SimpleDateFormat("HH:mm");
        String strDate = result.format(stop);
        return strDate;
    }

    public String getStatus(String status){
        if (status.equals("U")){
            return "Upload em andamento";
        }else{
            return "Upload Finalizado";
        }
    }

    public String getTitle(String title){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Date myDate1 = null;
        try {
            myDate1 = (Date)formatter.parse(title);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long stop = myDate1.getTime();
        DateFormat result = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = result.format(stop);
        return strDate;
    }






}
