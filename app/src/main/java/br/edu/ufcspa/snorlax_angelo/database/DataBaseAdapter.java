
package br.edu.ufcspa.snorlax_angelo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import br.edu.ufcspa.snorlax_angelo.model.RecordedFiles;
import br.edu.ufcspa.snorlax_angelo.model.Recording;
import br.edu.ufcspa.snorlax_angelo.model.User;


/**
 * Created by icaromsc on 17/01/2017.
 * thanks to unasus!!
 */

public class DataBaseAdapter {

    private static DataBaseAdapter mInstance = null;
    private SQLiteDatabase db;
    private DataBase helper;
    private String tag = "database";
    private Context context;
    private static String TB_RECORDINGS="recordings";
    private static String TB_USERS="users";
    private static String TB_RECORDED_FILES="recorded_files";

    private DataBaseAdapter(Context ctx) {
        helper = DataBase.getInstance(ctx);
        db = helper.getDatabase();
        context = ctx;
    }

    public static DataBaseAdapter getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DataBaseAdapter(ctx);
        }
        return mInstance;
    }




    public String listarTabelas() {
        String query = "SELECT name from sqlite_master where type='table'";
        String r = "";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                r += c.getString(0) + "\n";
                //r += c.getString(1) + "\n";
            } while (c.moveToNext());
        } else {
            r = "não há tabelas";
        }

        //db.close();

        return r;
    }

    public void insertUser(User user) {
            ContentValues cv = new ContentValues();
            cv.put("id_user", user.getIdUser());
            cv.put("id_user_google", user.getId_user_google());
            cv.put("id_user_facebook", user.getId_user_facebook());
            cv.put("name", user.getName());
            cv.put("email", user.getEmail());
        try {
               db.insert("users", null, cv);

            }catch (SQLiteConstraintException v){
               // Log.e(tag, "errro, usuario já existe no banco");
                v.printStackTrace();
            }
            catch (Exception e) {
                Log.e(tag, "erro ao inserir tb_user:" + e.getMessage());
                e.printStackTrace();
            }

        }

    public User getUser() {
        String query = "SELECT * from users";
        User user = new User(0,null,null,null,null,null);

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
                user= new User(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),null);
        } else {

        }

        //db.close();

        return user;
    }



    public int insertRecording(Recording rec) {
        ContentValues cv = new ContentValues();
        cv.put("date_start", String.valueOf(rec.getDateStart()));
        cv.put("status", Recording.STATUS_PROCESSING);
        try {
            db.insert(TB_RECORDINGS, null, cv);

        }catch (SQLiteConstraintException v){
            // Log.e(tag, "errro, usuario já existe no banco");
            v.printStackTrace();
        }
        catch (Exception e) {
            Log.e(tag, "erro ao inserir recording:" + e.getMessage());
            e.printStackTrace();
        }

        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name = '"+TB_RECORDINGS+"' ;", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_recording in table:" + lastID);
        }
        return lastID;

    }

    public void insertRecordedFile(RecordedFiles rec) {
        ContentValues cv = new ContentValues();
        cv.put("id_recording", rec.getIdRecording());
        cv.put("filename", rec.getFilename());
        cv.put("status", RecordedFiles.STATUS_PENDING_UPLOAD);
        try {
            db.insert(TB_RECORDED_FILES, null, cv);

        }catch (SQLiteConstraintException v){
            // Log.e(tag, "errro, usuario já existe no banco");
            v.printStackTrace();
        }
        catch (Exception e) {
            Log.e(tag, "erro ao inserir recorded_files:" + e.getMessage());
            e.printStackTrace();
        }


    }


    public void updateFinalizeRecording(Recording recording) {
        ContentValues cv = new ContentValues();
        cv.put("date_stop", String.valueOf(recording.getDateStop()));
        cv.put("status", Recording.STATUS_FINISHED);
        try {
            db.update(TB_RECORDINGS, cv, "id_recording=?", new String[]{"" + recording.getIdRecording()});
        } catch (Exception e) {
            Log.d(tag,"error updating recording:"+ e.getMessage());
        }
    }

    public void updateStatusRecordedFile(RecordedFiles rec) {
        ContentValues cv = new ContentValues();
        cv.put("status", rec.getStatus_upload());
        try {
            db.update(TB_RECORDED_FILES, cv, "id_recorded_file=?", new String[]{"" + rec.getIdRecordedFile()});
        } catch (Exception e) {
            Log.d(tag,"error updating recorded_file:"+ e.getMessage());
        }
    }


    public List<Recording> getRecordings() {
        Recording recording;
        ArrayList<Recording> lista = new ArrayList<>();
        Cursor c = null;
        String query = "SELECT * FROM ".concat(TB_RECORDINGS);
        c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                recording = new Recording(c.getInt(0),c.getString(1),c.getString(2),c.getString(3));
                //            user.setPhoto(c.getString(5), null);
                lista.add(recording);
            } while (c.moveToNext());
        }
        return lista;
    }


    public List<RecordedFiles> getRecordedFiles() {
        RecordedFiles rec;
        ArrayList<RecordedFiles> lista = new ArrayList<>();
        Cursor c = null;
        String query = "SELECT * FROM ".concat(TB_RECORDED_FILES);
        c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                rec = new RecordedFiles(c.getInt(0),c.getInt(1),c.getString(2),c.getString(3));
                lista.add(rec);
            } while (c.moveToNext());
        }
        return lista;
    }

    public List<RecordedFiles> getRecordedFilesToBeUploaded() {
        RecordedFiles rec;
        ArrayList<RecordedFiles> lista = new ArrayList<>();
        Cursor c = null;
        String query = "SELECT * FROM ".concat(TB_RECORDED_FILES).concat(" WHERE status = '").concat(RecordedFiles.STATUS_PENDING_UPLOAD).concat("';");
        Log.d(tag,"getRecToUp query: "+query);
        c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                rec = new RecordedFiles(c.getInt(0),c.getInt(1),c.getString(2),c.getString(3));
                lista.add(rec);
            } while (c.moveToNext());
        }
        return lista;
    }
















    }






/*
    public User verifyPass(String email, String pass) {
        int id = 0;
        char type = '0';
        String name = "zebra";
        User user = new User(0, null, null);
        try {
            if (email != "" && pass != "") {

                String query = "select id_user , nm_user , tp_user from tb_user where ds_email ='" + email + "' " + "AND ds_password ='" + pass + "' ;";
                Cursor c = db.rawQuery(query, null);
                if (c.moveToFirst()) {
                    Log.d(tag, "password correto");
                    id = Integer.parseInt(c.getString(0));
                    name = c.getString(1);
                    type = c.getString(2).charAt(0);
                    user = new User(id, type, name);
                    Log.d(tag, "dados: id:" + id + " type:" + type + " name:" + name);
                }
            }
        } catch (Exception e) {
            Log.d(tag, "erro query:" + e.getMessage());
        } finally {
//            db.close();
            return user;
        }
    }

    public int getUserID() {
        int id = -1;
        String query = "select id_user from tb_user ;";

        try {
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                Log.d(tag, "recuperou user");
                id = c.getInt(0);
            }
        } catch (Exception e) {
            Log.d(tag, "nao achou user");
        }

//            db.close();
        return id;

    }


    //INSERTS IN DATABASE
    public void insertTBUser(List<com.ufcspa.unasus.appportfolio.Model.basicData.User> users) {
        for (com.ufcspa.unasus.appportfolio.Model.basicData.User u : users) {
            ContentValues cv = new ContentValues();
            cv.put("nm_user", u.getNm_user());
            cv.put("id_user", u.getIdUser());
            cv.put("nu_identification", u.getNu_identification());
            cv.put("ds_email", u.getEmail());
            cv.put("nu_cellphone", u.getCellphone());
            try {
                db.insert("tb_user", null, cv);

            }catch (SQLiteConstraintException v){
                Log.e(tag, "errro, usuario já existe no banco");
                v.printStackTrace();
            }
            catch (Exception e) {
                Log.e(tag, "erro ao inserir tb_user:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertTBClass(List<com.ufcspa.unasus.appportfolio.Model.basicData.Class> classList) {
        for (com.ufcspa.unasus.appportfolio.Model.basicData.Class ac : classList) {
            ContentValues cv = new ContentValues();
            cv.put("id_class", ac.getId_class());
            cv.put("id_proposer", ac.getId_proposer());
            cv.put("ds_code", ac.getDs_code());
            cv.put("ds_description", ac.getDs_description());
            try {
                db.insert("tb_class", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_class:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertTBClassStudent(List<com.ufcspa.unasus.appportfolio.Model.basicData.ClassStudent> classList) {
        for (com.ufcspa.unasus.appportfolio.Model.basicData.ClassStudent ac : classList) {
            ContentValues cv = new ContentValues();
            cv.put("id_class_student", ac.getId_Class_Student());
            cv.put("id_class", ac.getId_Class());
            cv.put("id_student", ac.getId_Student());
            try {
                db.insert("tb_class_student", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_class_student:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertTBClassTutor(List<com.ufcspa.unasus.appportfolio.Model.basicData.ClassTutor> classList) {
        for (com.ufcspa.unasus.appportfolio.Model.basicData.ClassTutor ac : classList) {
            ContentValues cv = new ContentValues();
            cv.put("id_class_tutor", ac.getId_Class_Tutor());
            cv.put("id_class", ac.getId_Class());
            cv.put("id_tutort", ac.getId_Tutor());
            try {
                db.insert("tb_class_tutor", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_class_tutor:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertTBPortfolio(List<com.ufcspa.unasus.appportfolio.Model.basicData.Portfolio> port) {
        for (com.ufcspa.unasus.appportfolio.Model.basicData.Portfolio ac : port) {
            ContentValues cv = new ContentValues();
            cv.put("id_portfolio", ac.getId_Portfolio());
            cv.put("ds_title", ac.getDs_Title());
            cv.put("ds_description", ac.getDs_Description());
            if (ac.getNu_portfolio_version() != null) {
                cv.put("nu_portfolio_version", ac.getNu_portfolio_version());
            }

            try {
                db.insert("tb_portfolio", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_portfolio:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }


    public void insertTBPortfolioClass(List<com.ufcspa.unasus.appportfolio.Model.basicData.PortfolioClass> classList) {
        for (com.ufcspa.unasus.appportfolio.Model.basicData.PortfolioClass ac : classList) {
            ContentValues cv = new ContentValues();
            cv.put("id_portfolio_class", ac.getId_portfolio_class());
            cv.put("id_class", ac.getId_class());
            cv.put("id_portfolio", ac.getId_portfolio());
            try {
                db.insert("tb_portfolio_class", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_portfolio_class:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertTBPortfolioStudent(List<com.ufcspa.unasus.appportfolio.Model.basicData.PortfolioStudent> port) {
        for (com.ufcspa.unasus.appportfolio.Model.basicData.PortfolioStudent ac : port) {
            ContentValues cv = new ContentValues();
            cv.put("id_portfolio_student", ac.getId_portfolio_student());
            cv.put("id_portfolio_class", ac.getId_portfolio_class());
            cv.put("id_student", ac.getId_student());
            cv.put("id_tutor", ac.getId_tutor());
            cv.put("dt_first_sync", ac.getDt_first_sync());
            cv.put("nu_portfolio_version", ac.getNu_portfolio_version());
            try {
                db.insert("tb_portfolio_student", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_portfolio_student:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertTBActivity(List<com.ufcspa.unasus.appportfolio.Model.basicData.Activity> activities) {
        for (com.ufcspa.unasus.appportfolio.Model.basicData.Activity ac : activities) {
            ContentValues cv = new ContentValues();
            cv.put("ds_description", ac.getDs_description());
            cv.put("id_activity", ac.getId_activity());
            cv.put("id_portfolio", ac.getId_portfolio());
            cv.put("nu_order", ac.getNu_order());
            cv.put("ds_title", ac.getDs_title());
            try {
                db.insert("tb_activity", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir activity:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertTBActivityStudent(List<com.ufcspa.unasus.appportfolio.Model.basicData.ActivityStudent> activities) {
        for (com.ufcspa.unasus.appportfolio.Model.basicData.ActivityStudent ac : activities) {
            ContentValues cv = new ContentValues();
            cv.put("id_activity_student", ac.getIdActivityStudent());
            cv.put("id_portfolio_student", ac.getIdPortfolioStudent());
            cv.put("id_activity", ac.getIdActivity());
            cv.put("dt_conclusion", ac.getDt_conclusion());
            //cv.put("dt_fisrt_sync", ac.getDt_first_sync());
            try {
                db.insert("tb_activity_student", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir activity Student:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertTBSync(List<Sync> syncs) {
        for (Sync s : syncs) {
            ContentValues cv = new ContentValues();
            cv.put("id_device", s.getId_device());
            cv.put("id_activity_student", s.getId_activity_student());
            cv.put("tp_sync", s.getTp_sync());
            cv.put("nm_table", s.getNm_table());
            cv.put("co_id_table", s.getCo_id_table());
            cv.put("dt_sync", s.getDt_sync());
            cv.put("dt_read", s.getDt_read());
            try {
                db.insert("tb_sync", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir tb_sync:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public User insertUser(User u) {
        ContentValues cv = new ContentValues();
        cv.put("nm_user", u.getName());
        cv.put("id_user", u.getIdUser());
        cv.put("nu_identification", u.getIdCode());
        cv.put("ds_email", u.getEmail());
        cv.put("nu_cellphone", u.getCellphone());
        int lastID = 0;
        User user = new User(0, 'U', "NULO");
        try {
            db.insert("tb_user", null, cv);
            Log.d(tag, "inseriu no banco user " + u.getName() + "  no banco");
            Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_user'", null);

            if (cursor.moveToFirst()) {
                lastID = cursor.getInt(0);
                Log.d(tag, "last id_attachment in table:" + lastID);
                Cursor c = db.rawQuery("SELECT nm_user FROM tb_user where id_user=" + lastID, null);
                if (c.moveToFirst()) {
                    user = new User(lastID, 'U', c.getString(0));
                }
            }
        } catch (Exception e) {
            Log.e(tag, "erro ao inserir user:" + e.getMessage() + "\n");
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getUsersByIdPortfolioClass(int idPortfolioClass) {
        User user = new User(0, null, null);
        ArrayList<User> lista = new ArrayList<>();
        Cursor c = null;
        String query = "SELECT id_user, nm_user, nu_identification, ds_email, nu_cellphone, im_photo FROM tb_user JOIN tb_portfolio_student ON id_student=id_user WHERE id_portfolio_class ="+idPortfolioClass;
        c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                user = new User(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4));
    //            user.setPhoto(c.getString(5), null);
                lista.add(user);
            } while (c.moveToNext());
        }

        return lista;
    }

    public String listarTabelas() {
        String query = "SELECT name from sqlite_master where type='table'";
        String r = "";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                r += c.getString(0) + "\n";
                //r += c.getString(1) + "\n";
            } while (c.moveToNext());
        } else {
            r = "não há tabelas";
        }

//        db.close();

        return r;
    }





*/
/*

    *************************CRUD REFERENCIAS***********************************

*//*


    public boolean insertReference(Reference ref) {
        ContentValues cv = new ContentValues();
        //cv.put("id_reference",ref.getIdRef());
        cv.put("ds_url", ref.getDsUrl());
        cv.put("id_activity_student", ref.getIdActStudent());
        try {
            db.insert("tb_reference", null, cv);
//            db.close();
            Log.d(tag, "inseriu referencia no banco");
            return true;
        } catch (Exception e) {
            Log.e(tag, "erro ao inserir ref:" + e.getMessage());
            return false;
        }
    }

    public void deleteReference(int idReference) {
        try {
            db.delete("tb_reference", "id_reference=?", new String[]{"" + idReference});
            Log.d(tag, "removeu ref do banco");
        } catch (Exception e) {
            Log.e(tag, "erro ao delete ref:" + e.getMessage());
        }
    }

    public List getReferences(int idActivity) {
        List refs = new ArrayList<Reference>(5);
        String sql = "SELECT * FROM tb_reference WHERE id_activity_student =" + idActivity + ";";
        Cursor c = db.rawQuery(sql, null);
        Reference r;
        if (c.moveToFirst()) {
            do {
                try {
                    r = new Reference(c.getInt(0), c.getString(2), 0);
                    refs.add(r);
                    Log.d(tag, "reference:" + r.toString());
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add references
            } while (c.moveToNext());
//            c.close();
//            db.close();
        } else {
            Log.d(tag, "não retornoun nada");
        }
        return refs;
    }

    */
/*

        *************************CRUD COMENTARIOS***********************************

    *//*

    public int insertCommentOLD(Comentario c) {
        ContentValues cv = new ContentValues();
        cv.put("id_activity_student", c.getIdActivityStudent());
        cv.put("id_author", c.getIdAuthor());
        cv.put("tx_comment", c.getTxtComment());
        cv.put("tx_reference", c.getTxtReference());
        cv.put("tp_comment", c.getTypeComment());
        cv.put("dt_comment", c.getDateComment());
        cv.put("dt_send", c.getDateSend());
        db.insert("tb_comment", null, cv);
        try {
//            db.close();
            Log.d(tag, "inseriu comentario no banco");
        } catch (Exception e) {
            Log.e(tag, "erro ao inserir:" + e.getMessage());
        }
        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_comment'", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_comment id table:" + lastID);
        }
        return lastID;
    }



    public int insertComment(Comentario c) {
        ContentValues cv = new ContentValues();
        cv.put("id_activity_student", c.getIdActivityStudent());
        cv.put("id_author", c.getIdAuthor());
        cv.put("tx_comment", c.getTxtComment());
        cv.put("tp_comment", c.getTypeComment());
        cv.put("dt_comment", c.getDateComment());
        cv.put("dt_send", c.getDateSend());
        db.insert("tb_comment", null, cv);
        try {
//            db.close();
            Log.d(tag, "inseriu comentario no banco");
        } catch (Exception e) {
            Log.e(tag, "erro ao inserir:" + e.getMessage());
        }
        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_comment'", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_comment id table:" + lastID);
        }
        return lastID;
    }


    // atualizado para versão de 07/06/2016
    public int insertSpecificComment(Comentario c) {
        ContentValues cv = new ContentValues();



        cv.put("id_activity_student", c.getIdActivityStudent());
        cv.put("id_author", c.getIdAuthor());
        cv.put("id_comment_version", c.getId_comment_version());
        cv.put("tx_comment", c.getTxtComment());
        cv.put("tp_comment", c.getTypeComment());
        cv.put("dt_comment", c.getDateComment());
        cv.put("dt_send", c.getDateSend());

        db.insert("tb_comment", null, cv);
        try {
            Log.d(tag + " insertSpecificComment", "inseriu comentario no banco:"+c);
        } catch (Exception e) {
            Log.e(tag + " insertSpecificComment", "erro ao inserir:" + e.getMessage());
        }
        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_comment'", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_comment id table:" + lastID);
        }
        return lastID;
    }




    public int insertSpecificCommentOLD(Comentario c, int idNote) {
        ContentValues cv = new ContentValues();
        cv.put("id_activity_student", c.getIdActivityStudent());
        cv.put("id_author", c.getIdAuthor());
        cv.put("tx_comment", c.getTxtComment());
        cv.put("tx_reference", c.getTxtReference());
        cv.put("tp_comment", c.getTypeComment());
        cv.put("dt_comment", c.getDateComment());
        cv.put("dt_send", c.getDateSend());
        cv.put("nu_comment_activity", idNote);
        db.insert("tb_comment", null, cv);
        try {
//            db.close();
            Log.d(tag + " insertSpecificComment", "inseriu comentario no banco");
        } catch (Exception e) {
            Log.e(tag + " insertSpecificComment", "erro ao inserir:" + e.getMessage());
        }
        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_comment'", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_comment id table:" + lastID);
        }
        return lastID;

    }
    public boolean isFirstSpecificCommentOLD(int idActSt,int nu_comment_activity) {
        int id = -1;
        String sql = "select * from tb_comment WHERE tp_comment ='O' AND id_activity_student=" + idActSt + " AND nu_comment_activity= " + nu_comment_activity;
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            return false;
        } else {
            return true;
        }

    }

    public boolean isFirstSpecificComment(int idActSt,int nu_comment_activity) {
        int id = -1;
        String sql = "select * from tb_comment_version cv JOIN tb_version_activity va ON va.id_version_activity_srv=cv.id_version_activity WHERE va.id_activity_student=" + idActSt + " AND nu_comment_activity= " + nu_comment_activity;
        Log.d(tag,"is first specific comment sql:"+sql);
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount()>0) {
            return false;
        } else {
            return true;
        }

    }







    */
/* ---------    ****************************          ---------
       --------- CRUD COMENTARIOS ESPECIFICOS NOVA VERSÃO --------
       --------- *************************************** ---------- *//*



    public int insertSpecificCommenNEW(Comentario c) {
        ContentValues cv = new ContentValues();
        cv.put("id_activity_student", c.getIdActivityStudent());
        cv.put("id_author", c.getIdAuthor());
        cv.put("id_comment_version", c.getIdAuthor());
        cv.put("tx_comment", c.getTxtComment());
        cv.put("tp_comment", c.getTypeComment());
        cv.put("dt_comment", c.getDateComment());
        cv.put("dt_send", c.getDateSend());

        db.insert("tb_comment", null, cv);
        try {
            Log.d(tag + " insertSpecificComment", "inseriu comentario no banco");
        } catch (Exception e) {
            Log.e(tag + " insertSpecificComment", "erro ao inserir:" + e.getMessage());
        }
        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_comment'", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_comment id table:" + lastID);
        }
        return lastID;
    }

    public int insertObservationByVersion(Observation o){
        ContentValues cv = new ContentValues();
        if(o.getId_comment_version()>0) {
            cv.put("id_comment_version", o.getId_comment_version());
        }

        cv.put("id_version_activity", o.getId_version_activity());
        cv.put("tx_reference", o.getTx_reference());
        cv.put("nu_comment_activity", o.getNu_comment_activity());
        cv.put("nu_initial_pos",o.getNu_initial_position());
        cv.put("nu_size", o.getNu_size());
        if(o.getId_comment_version_srv()>0){
            cv.put("id_comment_version_srv", o.getId_comment_version_srv());
        }
        else{
            cv.put("id_comment_version_srv", o.getId_comment_version());
        }




        try {
            db.insert("tb_comment_version", null, cv);
            Log.d(tag + " insert", "inseriu observação no banco");
        } catch (Exception e) {
            Log.e(tag + " insert", "erro ao inserir observação:" + e.getMessage());
        }
        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_comment_version'", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_comment id table:" + lastID);
        }
        return lastID;
    }





    public int getIdObservationByNuCommentActivy(int id_activity_student, int nu_comment_activity) {
        String query = "select id_comment_version from tb_comment_version cv JOIN tb_version_activity va ON va.id_version_activity_srv=cv.id_version_activity WHERE va.id_activity_student=" + id_activity_student + " AND nu_comment_activity= " + nu_comment_activity;
        Cursor c = db.rawQuery(query, null);
        int id=-1;
        if (c.moveToFirst()) {
            id = c.getInt(0);
        }else {
            id = 0;
        }
        return id;
    }

    public int getIdObservationSrvByNuCommentActivy(int id_activity_student, int nu_comment_activity) {
        String query = "select id_comment_version_srv from tb_comment_version cv JOIN tb_version_activity va ON va.id_version_activity_srv=cv.id_version_activity WHERE va.id_activity_student=" + id_activity_student + " AND nu_comment_activity= " + nu_comment_activity;
        Cursor c = db.rawQuery(query, null);
        int id=-1;
        if (c.moveToFirst()) {
            id = c.getInt(0);
        }else {
            id = 0;
        }
        return id;
    }

    public String getIdObservationTextByNuCommentActivy(int id_activity_student, int nu_comment_activity) {
        String query = "select distinct tx_reference from tb_comment_version cv JOIN tb_version_activity va ON va.id_version_activity_srv=cv.id_version_activity WHERE va.id_activity_student=" + id_activity_student + " AND nu_comment_activity= " + nu_comment_activity;
        Cursor c = db.rawQuery(query, null);
        String r= new String();
        //Observation obs= new Observation();
        if (c.moveToFirst()) {
            r  = c.getString(0);
            Log.d("tx_observation",r);
        }

        return r;
    }



    public List<Observation> getObservationsByVersion(int idversion, int nu_comment_activity){
        ArrayList<Observation>  obs= new ArrayList<Observation>();
        String sql = "SELECT * from tb_comment_version WHERE nu_comment_activity =" + nu_comment_activity + " AND id_version_activity="+idversion+";";
        //Log.e(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);

        if (c.moveToFirst()) {
            do {
                try {
                    Observation o= new Observation();
                    o.setId_comment_version(c.getInt(0));
                    o.setId_version_activity(c.getInt(1));
                    o.setTx_reference(c.getString(2));
                    o.setNu_comment_activity(c.getInt(3));
                    o.setNu_initial_position(c.getInt(4));
                    o.setNu_size(c.getInt(5));
                    o.setId_comment_version_srv(6);
                    obs.add(o);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add comment
            } while (c.moveToNext());
            c.close();
//            db.close();
        } else {
            Log.d(tag + " get", "não retornou nenhuma observação");
        }
        //Log.d(tag, "listou notas no banco n:" + comentarios.size());
        return obs;
    }



    public List<Observation> getObservation(int idversion){
        ArrayList<Observation>  obs= new ArrayList<Observation>();
        String sql = "SELECT * from tb_comment_version WHERE id_version_activity="+idversion+";";
        //Log.e(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);

        if (c.moveToFirst()) {
            do {
                try {
                    Observation o= new Observation();
                    o.setId_comment_version(c.getInt(0));
                    o.setId_version_activity(c.getInt(1));
                    o.setTx_reference(c.getString(2));
                    o.setNu_comment_activity(c.getInt(3));
                    o.setNu_initial_position(c.getInt(4));
                    o.setNu_size(c.getInt(5));
                    o.setId_comment_version_srv(c.getInt(6));
                    obs.add(o);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add comment
            } while (c.moveToNext());
            c.close();
//            db.close();
        } else {
            Log.d(tag + " get", "não retornou nenhuma observação");
        }
        //Log.d(tag, "listou notas no banco n:" + comentarios.size());
        return obs;
    }

    public List<Observation> getObservationALL(){
        ArrayList<Observation>  obs= new ArrayList<Observation>();
        String sql = "SELECT * from tb_comment_version;";
        //Log.e(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);

        if (c.moveToFirst()) {
            do {
                try {
                    Observation o= new Observation();
                    o.setId_comment_version(c.getInt(0));
                    o.setId_version_activity(c.getInt(1));
                    o.setTx_reference(c.getString(2));
                    o.setNu_comment_activity(c.getInt(3));
                    o.setNu_initial_position(c.getInt(4));
                    o.setNu_size(c.getInt(5));
                    o.setId_comment_version_srv(c.getInt(6));
                    obs.add(o);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add comment
            } while (c.moveToNext());
            c.close();
//            db.close();
        } else {
            Log.d(tag + " get", "não retornou nenhuma observação");
        }
        //Log.d(tag, "listou notas no banco n:" + comentarios.size());
        return obs;
    }











    public int getPersonalComment(int idActSt){
        int id=-1;
        String sql = "select * from tb_comment WHERE tp_comment ='P' AND id_activity_student=" + idActSt + ";";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            c.moveToFirst();
            id=(c.getInt(0));
//            c.close();
        }
        return id;
    }

    public int insertPersonalComment(Comentario c) {
        ContentValues cv = new ContentValues();
        cv.put("id_activity_student", c.getIdActivityStudent());
        cv.put("id_author", c.getIdAuthor());
        cv.put("tx_comment", c.getTxtComment());
        cv.put("tp_comment", "P");
        cv.put("dt_comment", c.getDateComment());
        db.insert("tb_comment", null, cv);
        try {
//            db.close();
            Log.d(tag, "inseriu comentario no banco");
        } catch (Exception e) {
            Log.e(tag, "erro ao inserir:" + e.getMessage());
        }
        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_comment'", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_comment id table:" + lastID);
        }
        return lastID;
    }



    public void updatePersonalComment(Comentario c) {
        ContentValues cv = new ContentValues();
        cv.put("id_author", c.getIdAuthor());
        cv.put("tx_comment", c.getTxtComment());
        cv.put("dt_comment", c.getDateComment());
        db.update("tb_comment", cv, "tp_comment ='P' AND id_activity_student =" + c.getIdActivityStudent(), null);
        try {
//            db.close();
            Log.d(tag, "inseriu comentario no banco");
        } catch (Exception e) {
            Log.e(tag, "erro ao inserir:" + e.getMessage());
        }
    }


    public Comentario getCommentById(int id) {
        String sql = "select * from tb_comment WHERE id_comment =" + id + ";";
        Cursor c = db.rawQuery(sql, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            Comentario comm = new Comentario();
            comm.setIdComment(c.getInt(0));
            comm.setIdActivityStudent(c.getInt(1));
            comm.setIdAuthor(c.getInt(2));
            comm.setTxtComment(c.getString(3));
            comm.setTxtReference(c.getString(4));
            comm.setTypeComment(c.getString(5));
            comm.setIdNote(c.getInt(6));
            comm.setIdCommentSrv(c.getInt(7));
            comm.setDateComment(c.getString(8));
            comm.setDateSend(c.getString(9));
//            c.close();
            return comm;
        } else {
            return null;
        }
    }

    public void updateComment(Comentario c) {
        ContentValues cv = new ContentValues();
        cv.put("id_activity_student", c.getIdActivityStudent());
        cv.put("id_author", c.getIdAuthor());
        cv.put("tx_comment", c.getTxtComment());
        cv.put("tx_reference", c.getTxtReference());
        cv.put("dt_comment", c.getDateComment());
        try {
            db.update("tb_comment", cv, "id_comment=?", new String[]{"" + c.getIdComment()});
        } catch (Exception e) {
            Log.d(tag, e.getMessage());
        }
    }


    public List<Comentario> listCommentsOLD(int idActStu, String typeComment, int idNote) {
        ArrayList<Comentario> comentarios = new ArrayList<Comentario>();
        //String sql = "select * from tb_comment WHERE id_activity_student =" + idActStu;
        String sql = "SELECT\n" +
                "\tc.id_comment,\n" +
                "\tc.id_activity_student,\n" +
                "\tc.id_author,\n" +
                "\tc.tx_reference,\n" +
                "\tc.tx_comment,\n" +
                "\tc.dt_comment,\n" +
                "\tac.id_attachment,\n" +
                "\tc.dt_send\n" +
                "\tFROM tb_comment c \n" +
                "\t\tLEFT JOIN  tb_attach_comment ac on ac.id_comment = c.id_comment\n" +
                "\tWHERE 1=1 AND c.id_activity_student = " + idActStu;

        StringBuilder stBuild = new StringBuilder(sql);
        if (typeComment.equalsIgnoreCase("G") || typeComment.equalsIgnoreCase("O") || typeComment.equalsIgnoreCase("P")) {
            //sql+=" AND tp_comment='"+typeComment+"' ";
            stBuild.append(" AND tp_comment='" + typeComment + "' ");
            if (typeComment.equalsIgnoreCase("O")) {
                //sql+=" AND nu_comment_activity="+idNote;
                stBuild.append(" AND id_version_comment=" + idNote);
            }
        }
        //sql+=" ORDER BY dt_comment ASC;";
        stBuild.append(" ORDER BY dt_send ASC");
        sql = stBuild.toString();
        //Log.e(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);
        Comentario cmm;
        if (c.moveToFirst()) {
            do {
                try {
                    cmm = new Comentario();
                    cmm.setIdComment(c.getInt(0));
                    cmm.setIdActivityStudent(c.getInt(1));
                    cmm.setIdAuthor(c.getInt(2));
                    cmm.setTxtReference(c.getString(3));
                    cmm.setTxtComment(c.getString(4));
                    cmm.setDateComment(c.getString(5));
                    cmm.setIdAttach(c.getInt(6));
                    cmm.setDateSend(c.getString(7));
                    comentarios.add(cmm);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add comment
            } while (c.moveToNext());
            c.close();
//            db.close();
        } else {
            Log.d(tag, "não retornou nada");
        }
        Log.d(tag, "listou comentarios no banco :" + comentarios.toString());
        return comentarios;
    }

    //Atualizado para nova versão banco 07/06/2016
    //LISTA COMENTARIOS GERAIS E PESSOAIS
    public List<Comentario> listGComments(int idActStu,String typeComment) {
        ArrayList<Comentario> comentarios = new ArrayList<Comentario>();
        //String sql = "select * from tb_comment WHERE id_activity_student =" + idActStu;
        String sql = "SELECT\n" +
                "\tc.id_comment,\n" +
                "\tc.id_activity_student,\n" +
                "\tc.id_author,\n" +
                "\tc.id_comment_version,\n" +
                "\tc.tx_comment,\n" +
                "\tc.dt_comment,\n" +
                "\tac.id_attachment,\n" +
                "\tc.dt_send\n" +
                "\tFROM tb_comment c \n" +
                "\t\tLEFT JOIN  tb_attach_comment ac on ac.id_comment = c.id_comment\n" +
                "\tWHERE 1=1 AND c.id_activity_student = " + idActStu;

        StringBuilder stBuild = new StringBuilder(sql);
        if (typeComment.equalsIgnoreCase("P")||typeComment.equalsIgnoreCase("G") ) {
            //sql+=" AND tp_comment='"+typeComment+"' ";
            stBuild.append(" AND tp_comment='" + typeComment + "' ");
        }

        //stBuild.append(" AND c.id_comment_version = 0 OR c.id_comment_version = null"); // lista comentarios que não estao ligados a uma versao(gerais ou pessoais)

        //sql+=" ORDER BY dt_comment ASC;";
        stBuild.append(" ORDER BY dt_send ASC");
        sql = stBuild.toString();
        Log.d(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);
        Comentario cmm;
        if (c.moveToFirst()) {
            do {
                try {
                    cmm = new Comentario();
                    cmm.setIdComment(c.getInt(0));
                    cmm.setIdActivityStudent(c.getInt(1));
                    cmm.setIdAuthor(c.getInt(2));
                    cmm.setId_comment_version(c.getInt(3));
                    cmm.setTxtComment(c.getString(4));
                    cmm.setDateComment(c.getString(5));
                    cmm.setIdAttach(c.getInt(6));
                    cmm.setDateSend(c.getString(7));
                    comentarios.add(cmm);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add comment
            } while (c.moveToNext());
            c.close();
//            db.close();
        } else {
            Log.d(tag, "não retornoun nada");
        }
        Log.d(tag, "listou comentarios no banco :" + comentarios.toString());
        return comentarios;
    }


    public List<Comentario> listObsComments(int idActStu,int idNote) {
        ArrayList<Comentario> comentarios = new ArrayList<Comentario>();
        //String sql = "select * from tb_comment WHERE id_activity_student =" + idActStu;
        String sql = "SELECT\n" +
                "\tc.id_comment,\n" +
                "\tc.id_activity_student,\n" +
                "\tc.id_author,\n" +
                "\tc.id_comment_version,\n" +
                "\tc.tx_comment,\n" +
                "\tc.dt_comment,\n" +
                "\tac.id_attachment,\n" +
                "\tc.dt_send\n" +
                "\tFROM tb_comment c \n" +
                "\t\tLEFT JOIN  tb_attach_comment ac on ac.id_comment = c.id_comment\n" +
                "\t\tLEFT JOIN  tb_comment_version cv on cv.id_comment_version_srv = c.id_comment_version" +
                "\tWHERE 1=1 AND c.id_activity_student = " + idActStu;

        StringBuilder stBuild = new StringBuilder(sql);
        if (idNote>0) {
            //sql+=" AND nu_comment_activity="+idNote;
            stBuild.append(" AND cv.nu_comment_activity=" + idNote);
        }


        //sql+=" ORDER BY dt_comment ASC;";
        stBuild.append(" ORDER BY dt_send ASC");
        sql = stBuild.toString();
        Log.d(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);
        Comentario cmm;
        if (c.moveToFirst()) {
            do {
                try {
                    cmm = new Comentario();
                    cmm.setIdComment(c.getInt(0));
                    cmm.setIdActivityStudent(c.getInt(1));
                    cmm.setIdAuthor(c.getInt(2));
                    cmm.setId_comment_version(c.getInt(3));
                    cmm.setTxtComment(c.getString(4));
                    cmm.setDateComment(c.getString(5));
                    cmm.setIdAttach(c.getInt(6));
                    cmm.setDateSend(c.getString(7));
                    comentarios.add(cmm);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add comment
            } while (c.moveToNext());
            c.close();
//            db.close();
        } else {
            Log.d(tag, "não retornoun nada");
        }
        Log.d(tag, "listou comentarios no banco :" + comentarios.toString());
        return comentarios;
    }





    public List<Comentario> listCommentsTESTE() {
        ArrayList<Comentario> comentarios = new ArrayList<Comentario>();
        //String sql = "select * from tb_comment WHERE id_activity_student =" + idActStu;
        String sql = "SELECT\n" +
                "\tc.id_comment,\n" +
                "\tc.id_activity_student,\n" +
                "\tc.id_author,\n" +
                "\tc.id_comment_version,\n" +
                "\tc.tx_comment,\n" +
                "\tc.dt_comment,\n" +
                "\tac.id_attachment,\n" +
                "\tc.dt_send\n" +
                "\tFROM tb_comment c \n" +
                "\t\tLEFT JOIN  tb_attach_comment ac on ac.id_comment = c.id_comment\n";

        StringBuilder stBuild = new StringBuilder(sql);
//        if (typeComment.equalsIgnoreCase("G") || typeComment.equalsIgnoreCase("O") || typeComment.equalsIgnoreCase("P")) {
//            //sql+=" AND tp_comment='"+typeComment+"' ";
//            stBuild.append(" AND tp_comment='" + typeComment + "' ");
//        }

        //sql+=" ORDER BY dt_comment ASC;";
        stBuild.append(" ORDER BY dt_send ASC");
        sql = stBuild.toString();
        Log.e(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);
        Comentario cmm;
        if (c.moveToFirst()) {
            do {
                try {
                    cmm = new Comentario();
                    cmm.setIdComment(c.getInt(0));
                    cmm.setIdActivityStudent(c.getInt(1));
                    cmm.setIdAuthor(c.getInt(2));
                    cmm.setId_comment_version(c.getInt(3));
                    cmm.setTxtComment(c.getString(4));
                    cmm.setDateComment(c.getString(5));
                    cmm.setIdAttach(c.getInt(6));
                    cmm.setDateSend(c.getString(7));
                    comentarios.add(cmm);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add comment
            } while (c.moveToNext());
            c.close();
//            db.close();
        } else {
            Log.d(tag, "não retornoun nada");
        }
        Log.d(tag, "listou comentarios no banco :" + comentarios.toString());
        return comentarios;
    }




    public LinkedList<Integer> listSpecificComments(int idActStu) {
        LinkedList<Integer> comentarios = new LinkedList<>();
        String sql = "SELECT DISTINCT cv.nu_comment_activity from tb_comment_version cv " +
                "   INNER JOIN tb_version_activity va ON cv.id_version_activity = va.id_version_activity_srv WHERE va.id_activity_student =" + idActStu;
        //Log.e(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);
        Integer id;
        if (c.moveToFirst()) {
            do {
                try {
                    id = c.getInt(0);
                    //Log.e(tag, "listSpecificComments idnow:" + id);
                    comentarios.add(id);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add comment
            } while (c.moveToNext());
            c.close();
//            db.close();
        } else {
            Log.d(tag + " listSpecific comments", "não retornou nada");
        }
        Log.d(tag, "listou notas no banco n:" + comentarios.size());
        return comentarios;
    }
    public LinkedList<Observation> listSpecificCommentsObjects(int idActStu,int idVersionActivity) {
        LinkedList<Observation> comentarios = new LinkedList<>();
        String sql = "SELECT DISTINCT cv.id_comment_version,cv.nu_comment_activity,cv.tx_reference,cv.nu_initial_pos , cv.nu_size from tb_comment_version cv " +
                "   INNER JOIN tb_version_activity va ON cv.id_version_activity = va.id_version_activity WHERE va.id_activity_student =" + idActStu ; //+ " AND cv.id_version_activity="+idVersionActivity;
        Log.e(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);
        Integer id;
        if (c.moveToFirst()) {
            do {
                try {
                    Observation ob = new Observation();
                    ob.setId_comment_version(c.getInt(0));
                    ob.setNu_comment_activity(c.getInt(1));
                    ob.setTx_reference(c.getString(2));
                    ob.setNu_initial_position(c.getInt(3));
                    ob.setNu_size(4);
                    ob.setId_comment_version_srv(5);
                    Log.d(tag, "obs:" + ob.toString());

                    //Log.e(tag, "listSpecificComments idnow:" + id);
                    comentarios.add(ob);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add comment
            } while (c.moveToNext());
            c.close();
        } else {
            Log.d(tag + " listSpecific comments", "não retornou nada");
        }
        Log.d(tag, "listou notas no banco n:" + comentarios.toString());
        return comentarios;
    }




    public List<Integer> listSpecificCommentsOLD(int idActStu) {
        ArrayList<Integer> comentarios = new ArrayList<Integer>();
        String sql = "SELECT DISTINCT nu_comment_activity from tb_comment WHERE id_activity_student =" + idActStu + " AND tp_comment='O'";
        //Log.e(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);
        Integer id;
        if (c.moveToFirst()) {
            do {
                try {
                    id = c.getInt(0);
                    Log.e(tag, "listSpecificComments idnow:" + id);
                    comentarios.add(id);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
                //add comment
            } while (c.moveToNext());
            c.close();
//            db.close();
        } else {
            Log.d(tag + " listSpecific comments", "não retornou nada");
        }
        Log.d(tag, "listou notas no banco n:" + comentarios.size());
        return comentarios;
    }







    public List<Integer> listNotesSpecificCommentsOLD(int version) {
        ArrayList<Integer> v = new ArrayList<>();
        String sql = "SELECT DISTINCT nu_comment_activity from tb_comment as tbc " +
                "LEFT JOIN tb_comment_version as tbcv on tbcv.id_comment = tbc.id_comment " +
                "LEFT JOIN tb_version_activity as tbva on tbva.id_version_activity = tbcv.id_version_activity" +
                " WHERE tbva.id_version_activity =" + version;
        //Log.e(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);
        Integer id;
        if (c.moveToFirst()) {
            do {
                try {
                    id = c.getInt(0);
                    v.add(id);
                } catch (Exception e) {
                }
            } while (c.moveToNext());
            c.close();
        } else {
            Log.d(tag + " listSpecific comments", "não retornou nada");
        }
        return v;
    }

    public List<Observation> listNotesSpecificComments(int version) {
        String sql = "SELECT DISTINCT nu_comment_activity,tx_reference from tb_comment_version as tbcv " +
                "LEFT JOIN tb_version_activity as tbva on tbva.id_version_activity_srv = tbcv.id_version_activity" +
                " WHERE tbva.id_version_activity =" + version;
        //Log.e(tag, "sql listComments:" + sql);
        Cursor c = db.rawQuery(sql, null);
        LinkedList<Observation> obs= new LinkedList<Observation>();

        if (c.moveToFirst()) {
            do {
                try {
                    Observation ob = new Observation();

                    ob.setNu_comment_activity(c.getInt(0));
                    ob.setTx_reference(c.getString(1));
                    obs.add(ob);
                } catch (Exception e) {
                }
            } while (c.moveToNext());
            c.close();
            Log.d(tag, "list bolinhas:"+obs.toString());
        } else {
            Log.d(tag + " listSpecific comments", "não retornou nada");
        }
        return obs;
    }




    */
/*

    *************************CRUD ANEXOS***********************************

*//*

    public int insertAttachment(Attachment attach) {
        ContentValues cv = new ContentValues();
        cv.put("tp_attachment", attach.getTpAttachment());
        cv.put("nm_file", attach.getNmFile());
        cv.put("nm_system", attach.getNmSystem());
        cv.put("id_attachment_srv", attach.getidAttachmentSrv());

        try {
            db.insert("tb_attachment", null, cv);
            Log.d(tag, "conseguiu salvar id anexo no comentario do bd");
        } catch (Exception e) {
            Log.e(tag, "erro ao salvar na tabela tb_attach_comment:" + e.getMessage());
        }
        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_attachment'", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_attachment in table:" + lastID);
        }
        return lastID;
    }

    public int insertAttachmentDownload(Attachment attach) {
        ContentValues cv = new ContentValues();
        cv.put("tp_attachment", attach.getTpAttachment());
        cv.put("nm_file", attach.getNmFile());
        cv.put("nm_system", Environment.getExternalStorageDirectory()+"/Android/data/com.ufcspa.unasus.appportfolio/files/images" + File.separator + attach.getNmSystem());
        cv.put("id_attachment_srv", attach.getidAttachmentSrv());

        try {
            db.insert("tb_attachment", null, cv);
            Log.d(tag, "conseguiu salvar id anexo no comentario do bd");
        } catch (Exception e) {
            Log.e(tag, "erro ao salvar na tabela tb_attach_comment:" + e.getMessage());
        }
        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_attachment'", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_attachment in table:" + lastID);
        }
        return lastID;
    }

    public Attachment getAttachmentByID(int id_attachment) {
        Attachment attch = new Attachment();
        String sql = "SELECT * FROM tb_attachment WHERE id_attachment =" + id_attachment;
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                attch.setIdAttachment(c.getInt(0));
                attch.setTpAttachment(c.getString(1));
                attch.setNmFile(c.getString(2));
                attch.setNmSystem(c.getString(3));
                attch.setIdAttachmentSrv(c.getInt(4));
            } else {
                Log.e(tag, "não encontrou a id relacionada a este anexo no banco");
            }
        } catch (Exception e) {
            Log.wtf(tag, "erro ao tentar buscar anexo no banco:" + e.getMessage());
        }
        return attch;
    }

    public int getAttachmentByPath(String filePath) {
        String path = filePath;
        if (path.contains("_video.")) {
            path = path.replace("_video.", ".");
        }
        String sql = "SELECT id_attachment FROM tb_attachment WHERE nm_system = '" + path + "'";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            } else {
                Log.e(tag, "não encontrou a id relacionada a este anexo no banco");
            }
        } catch (Exception e) {
            Log.wtf(tag, "erro ao tentar buscar anexo no banco:" + e.getMessage());
        }
        return -1;
    }


    public void insertAttachComment(int idComment, int idAttach) {
        ContentValues cv = new ContentValues();
        cv.put("id_attachment", idAttach);
        cv.put("id_comment", idComment);
        try {
            db.insert("tb_attach_comment", null, cv);
            Log.d(tag, "conseguiu salvar id anexo no comentario do bd");
        } catch (Exception e) {
            Log.e(tag, "erro ao salvar na tabela tb_attach_comment:" + e.getMessage());
        }
    }



    */
/*

    *************************CRUD ACTIVITY STUDENT***********************************

*//*


    public void updateActivityStudent(ActivityStudent activityStudent, String dateLastAccess, int idCurrentVersionActivity) {
        ContentValues cv = new ContentValues();
        cv.put("tx_activity", activityStudent.getTxtActivity());
        cv.put("dt_last_access", dateLastAccess);
        try {
            db.update("tb_version_activity", cv, "id_version_activity=?", new String[]{String.valueOf(idCurrentVersionActivity)});
            Log.d(tag, "conseguiu salvar alteração da atividade");
        } catch (Exception e) {
            Log.e(tag, "erro ao atualizar acStudent:" + e.getMessage());
        }
    }

    public int getLastIDVersionActivity(int idActivityStudent) {
        String query = "SELECT MAX(id_version_activity) FROM tb_version_activity WHERE id_activity_student=" + idActivityStudent + " AND NOT dt_submission='0000-00-00 00:00:00'";
        Cursor c = null;
        try {
            c = db.rawQuery(query, null);
            if (c.moveToFirst())
                return c.getInt(0);
            else
                return 0;
        } catch (Exception e) {
            Log.e(tag, "erro ao buscar last id version:" + e.getMessage());
        }
        return 0;
    }

    public int getIDVersionAtual(int idActivityStudent) {
        String query = "SELECT id_version_activity FROM tb_version_activity WHERE id_activity_student=" + idActivityStudent + " AND dt_submission='0000-00-00 00:00:00'";
        Cursor c = null;
        c = db.rawQuery(query, null);
        if (c.moveToFirst())
            return c.getInt(0);
        else
            return 0;
    }

    public int getLastIDVersionActivitySrv(int idActivityStudent) {
        String query = "SELECT MAX(id_version_activity_srv) FROM tb_version_activity WHERE id_activity_student=" + idActivityStudent;
        Cursor c = null;
        try {
            c = db.rawQuery(query, null);
            if (c.moveToFirst())
                return c.getInt(0);
            else
                return 0;
        } catch (Exception e) {
            Log.e(tag, "erro ao buscar last id version:" + e.getMessage());
        }
        return 0;
    }

    public ActivityStudent listLastVersionActivityStudent(int idActivityStudent) {
        ActivityStudent acStudent = new ActivityStudent();
        String query = "select tx_activity from tb_version_activity WHERE id_version_activity=(SELECT MAX(id_version_activity) FROM tb_version_activity WHERE id_activity_student=" + idActivityStudent + ") ORDER BY dt_submission;";
        Cursor c = null;
        Log.d(tag, "query lista act Stu:" + query);
        try {
            c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                acStudent.setIdActivityStudent(idActivityStudent);
                acStudent.setTxtActivity(c.getString(0));
                Log.d(tag, "há texto da atividade no banco:" + acStudent.getTxtActivity());
            } else {
                Log.d(tag, "não há texto da atividade no banco");
                acStudent.setIdActivityStudent(idActivityStudent);
                acStudent.setTxtActivity("");
            }
            c.close();
        } catch (Exception e) {
            Log.e(tag, "erro ao listar atividade do estudante:" + e.getMessage());
        }


//        db.close();
        return acStudent;
    }

    public String getTextFromCurrentVersion(int idVersionActivity) {
        Cursor c;
        try {
            c = db.rawQuery("SELECT tx_activity FROM tb_version_activity WHERE id_version_activity = ?", new String[]{String.valueOf(idVersionActivity)});
            if (c.moveToFirst()) {
                return c.getString(0);
            }
            c.close();
        } catch (Exception e) {
            Log.e(tag, "erro ao listar atividade do estudante:" + e.getMessage());
        }

        return null;
    }

    public int insertVersionActivity(VersionActivity v){
        ContentValues cv = new ContentValues();
        cv.put("tx_activity", v.getTx_activity());
        cv.put("dt_last_access", v.getDt_last_access());
        cv.put("dt_submission", v.getDt_submission());
        cv.put("id_activity_student", v.getId_activity_student());
        cv.put("id_version_activity_srv", v.getId_version_activit_srv());
        int result=-1;
        try {
            db.insert("tb_version_activity",null,cv);
            Log.d(tag, "conseguiu salvar versão da atividade");
            Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_version_activity'", null);
            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
                Log.d(tag, "last id_version_activity in table:" + result);
            }

        } catch (Exception e) {
            Log.e(tag, "erro ao salvar versao:" + e.getMessage());
        }

        return result;
    }

    public ArrayList<VersionActivity> getAllVersionsFromActivityStudent(int id_activity_student) {
        Cursor c = null;
        ArrayList<VersionActivity> versionActivities = new ArrayList<VersionActivity>();
        String query = "SELECT id_version_activity, " +
                "id_activity_student, " +
                "tx_activity, " +
                "dt_last_access, " +
                "dt_submission, " +
                "dt_verification, " +
                "id_version_activity_srv " +
                "FROM tb_version_activity " +
                "WHERE id_activity_student = " + id_activity_student;
        if (!Singleton.getInstance().portfolioClass.getPerfil().equals("T")){
            String temp = query + " AND dt_submission='0000-00-00 00:00:00'";
            Cursor cursor = db.rawQuery(temp, null);
            if (cursor.moveToFirst()) {
                versionActivities.add(cursorToVersionActivity(cursor));
            }
            query = query + " AND NOT dt_submission='0000-00-00 00:00:00' ORDER BY dt_submission DESC";
            cursor.close();
        } else {
            query = query + " AND NOT dt_submission='0000-00-00 00:00:00' ORDER BY dt_submission DESC";
        }
        c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                versionActivities.add(cursorToVersionActivity(c));
            } while (c.moveToNext());
        }
        return versionActivities;
    }

    public VersionActivity getVersionActivitiesByID(int id) {
        String query = "SELECT id_version_activity, " +
                "id_activity_student, " +
                "tx_activity, " +
                "dt_last_access, " +
                "dt_submission, " +
                "dt_verification, " +
                "id_version_activity_srv " +
                "FROM tb_version_activity " +
                "WHERE id_version_activity = " + id;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            return cursorToVersionActivity(c);
        }
        return null;
    }

    private VersionActivity cursorToVersionActivity(Cursor c) {
        VersionActivity aux = new VersionActivity();

        aux.setId_version_activity(c.getInt(0));
        aux.setId_activity_student(c.getInt(1));
        aux.setTx_activity(c.getString(2));
        aux.setDt_last_access(c.getString(3));
        aux.setDt_submission(c.getString(4));
        aux.setDt_verification(c.getString(5));
        aux.setId_version_activit_srv(c.getInt(6));

        return aux;
    }



    public User getTutorPerfil(int idActStudent){

        User u = new User();
        String query = "SELECT \n" +
                "\t\tps.id_tutor,\n" +
                "\t\tu.nm_user,\n" +
                "\t\tu.nu_cellphone,\n" +
                "\t\tu.im_photo\n" +
                "\t\t\n" +
                "\tFROM tb_activity_student as acs\n" +
                "\tINNER JOIN tb_portfolio_student as ps ON  ps.id_portfolio_student = acs.id_portfolio_student\n" +
                "\tINNER JOIN tb_user as u ON u.id_user = ps.id_tutor\n" +
                "\tWHERE acs.id_activity_student = "+idActStudent;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            u.setIdUser(cursor.getInt(0));
            u.setName(cursor.getString(1));
            u.setCellphone(cursor.getString(2));
            u.setPhoto(cursor.getString(3),null);
        }
        cursor.close();
        return u;
    }

    private int getIdVersionFromIdVersionSrv(int idSrv) {
        String query = "SELECT id_version_activity FROM tb_version_activity WHERE id_version_activity_srv = " + idSrv;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return -1;
    }
    public int getIdVersionSrvFromIdVersion(int id) {
        String query = "SELECT id_version_activity_srv FROM tb_version_activity WHERE id_version_activity = " + id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return -1;
    }

    private int getIdCommentFromIdCommentSrv(int idSrv) {
        String query = "SELECT id_comment FROM tb_comment WHERE id_comment_srv = " + idSrv;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return -1;
    }

    public int getIdCommentVersionSrv(int idCV) {
        String query = "SELECT id_comment_version_srv FROM tb_comment_version WHERE id_comment_version = " + idCV;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return -1;
    }




    // TÁ ERRADO
    public int insertCommentVersion(CommentVersion cVersion) {
        ContentValues cv = new ContentValues();
        cv.put("id_version_activity", getIdVersionFromIdVersionSrv(cVersion.getId_version_activity()));
        cv.put("id_comment", getIdCommentFromIdCommentSrv(cVersion.getId_comment()));
        cv.put("fl_active", String.valueOf(cVersion.getFl_active()));


        int result = -1;

        try {
            db.insert("tb_comment_version", null, cv);
            Log.d(tag, "conseguiu salvar na tb_comment_version");
            Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_comment_version'", null);
            if (cursor.moveToFirst())
                result = cursor.getInt(0);
        } catch (Exception e) {
            Log.e(tag, "erro ao salvar na tb_comment_version:" + e.getMessage());
        }

        return result;
    }

    public int insertCommentVersionWhenUserComment(CommentVersion cVersion) {
        ContentValues cv = new ContentValues();
        cv.put("id_version_activity", cVersion.getId_version_activity());
        cv.put("id_comment", cVersion.getId_comment());
        cv.put("fl_active", String.valueOf(cVersion.getFl_active()));

        int result = -1;

        try {
            db.insert("tb_comment_version", null, cv);
            Log.d(tag, "conseguiu salvar na tb_comment_version");
            Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_comment_version'", null);
            if (cursor.moveToFirst())
                result = cursor.getInt(0);
        } catch (Exception e) {
            Log.e(tag, "erro ao salvar na tb_comment_version:" + e.getMessage());
        }

        return result;
    }

    public LinkedHashMap<Integer, LinkedList<Comentario>> getCommentVersion(LinkedList<Integer> idList) {
        LinkedHashMap<Integer, LinkedList<Comentario>> aux = new LinkedHashMap<>();

        for (Integer id : idList) {
            String query = "SELECT id_version_activity, id_comment FROM tb_comment_version WHERE id_comment_version = " + id;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                int id_version_activity = c.getInt(0);
                int id_comment = c.getInt(1);

                Comentario comment = getCommentById(id_comment);

                if (comment != null) {
                    if (!aux.containsKey(id_version_activity))
                        aux.put(id_version_activity, new LinkedList<Comentario>());

                    aux.get(id_version_activity).add(comment);
                }
            }
        }

        return aux;
    }

    public boolean cvIsSyncronized(int id_comment_version) {
        String query = "SELECT * FROM tb_comment_version WHERE id_comment_version_srv = " + id_comment_version +" AND fl_srv='S'";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            return true;
        }else{
            return false;
        }
    }


    public boolean isSync(String table,int id){
        String query = "SELECT * FROM tb_sync WHERE nm_table = '" + table +"' AND co_id_table="+id;
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()){
            return false;
        }else{
            return true;
        }
    }



    public LinkedHashMap<Integer, LinkedList<Observation>> getObservationByVersions(LinkedList<Integer> ids) {
        LinkedHashMap<Integer, LinkedList<Observation>> aux = new LinkedHashMap<Integer, LinkedList<Observation>>();
        LinkedList obs = new LinkedList<Observation>();
        if (ids != null && ids.size() != 0) {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM tb_comment_version WHERE id_comment_version IN (");
            query.append(ids.get(0));

            for (int i = 1; i < ids.size(); i++) {
                query.append(", " + ids.get(i));
            }

            query.append(");");
            Cursor c = db.rawQuery(query.toString(), null);
            if (c.moveToFirst()) {
                do {
                    try {
                        Observation o= new Observation();
                        o.setId_comment_version(c.getInt(0));
                        o.setId_version_activity(c.getInt(1));
                        o.setTx_reference(c.getString(2));
                        o.setNu_comment_activity(c.getInt(3));
                        o.setNu_initial_position(c.getInt(4));
                        o.setNu_size(c.getInt(5));
                        o.setId_comment_version_srv(c.getInt(6));
                        o.setFlSRV(c.getString(7));
                        //obs.add(o);
                        if (o != null) {
                            if (!aux.containsKey(o.getId_version_activity()))
                                aux.put(o.getId_version_activity(), new LinkedList<Observation>());

                            aux.get(o.getId_version_activity()).add(o);
                        }

//                        aux.put(o.getId_version_activity(),);
                    } catch (Exception v) {
                        Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                    }
                    //add comment
                } while (c.moveToNext());
                c.close();
//            db.close();
            } else {
                Log.d(tag + " get", "não retornou nenhuma observação");
            }
            //Log.d(tag, "listou notas no banco n:" + comentarios.size());
            //return obs;
        }
        return aux;

    }






    // TÁ ERRADO
    public LinkedList<Comentario> getCommentVersion(int idVersion) {
        String query = "SELECT " +
                "\tc.id_comment,\n" +
                "\tc.id_activity_student,\n" +
                "\tc.id_author,\n" +
                "\tc.tx_reference,\n" +
                "\tc.tx_comment,\n" +
                "\tc.dt_comment,\n" +
                "\tc.tp_comment,\n" +
                "\tc.nu_comment_activity,\n" +
                "\tc.nu_comment_activity\n" +
                "\tFROM tb_comment_version cv \n" +
                "\t\t JOIN  tb_comment c on cv.id_comment = c.id_comment\n" +
                "	WHERE 1=1 AND c.id_comment_srv IS NULL AND cv.id_version_activity =" + idVersion;
        LinkedList<Comentario> comentarios = new LinkedList<Comentario>();
        Cursor c = db.rawQuery(query, null);
        Comentario cmm;
        if (c.moveToFirst()) {
            do {
                try {
                    cmm = new Comentario();
                    cmm.setIdComment(c.getInt(0));
                    cmm.setIdActivityStudent(c.getInt(1));
                    cmm.setIdAuthor(c.getInt(2));
                    cmm.setTxtReference(c.getString(3));
                    cmm.setTxtComment(c.getString(4));
                    cmm.setDateComment(c.getString(5));
                    cmm.setTypeComment(c.getString(6));
                    cmm.setIdNote(c.getInt(7));
                    comentarios.add(cmm);
                } catch (Exception v) {
                    Log.e(tag, "erro ao pegar dados do banco:" + v.getMessage());
                }
            } while (c.moveToNext());
        }
        return comentarios;
    }


    public List<PortfolioClass> listarPortfolio(int idClass, char userType, int idUser) {
        String query = "SELECT * FROM tb_portfolio";

        String queryNova = "select \n" +
                "\t a.id_portfolio_student as id_portfolio_student,\n" +
                "\t c.ds_code as code,\n" +
                "\t d.nm_user as nome_aluno,\n" +
                "\t p.ds_title as portfolio_title\n" +
                "from\n" +
                " tb_portfolio_student a\n" +
                "\tjoin tb_portfolio_class b on b.id_portfolio_class = a.id_portfolio_class\n" +
                "\tjoin tb_class c on c.id_class = b.id_class \n" +
                "\tjoin tb_user d on d.id_user = a.id_student\n" +
                "\tjoin tb_user e on e.id_user = a.id_tutor\n" +
                "\tjoin tb_portfolio p on p.id_portfolio = b.id_portfolio\n" +
                "where 1=1 \n";
        if (userType == 'S')
            queryNova += "AND a.id_student=" + idUser;
        else if (userType == 'T')
            queryNova += "AND a.id_tutor=" + idUser;

        queryNova += " AND " + "b.id_class=" + idClass + ";";

        ArrayList<PortfolioClass> portfolios = new ArrayList<PortfolioClass>();
        Cursor c = db.rawQuery(queryNova, null);
        if (c.moveToFirst()) {
            do {
                portfolios.add(cursorToPortfolio(c));
            } while (c.moveToNext());
        }

//        db.close();
        return portfolios;
    }

    private PortfolioClass cursorToPortfolio(Cursor c) {
        PortfolioClass pc = new PortfolioClass(Integer.parseInt(c.getString(0)), c.getString(1), c.getString(2), c.getString(3));
        Log.d(tag, "portfolio populado:" + pc.toString());
        return pc;
    }

    */
/*
    *************************MÉTODO DESCOBRIR PERFIL DO USUÀRIO***********************************
    *//*


    public char verifyUserType(int idUser) {
        String query = "select \n" +
                "\tcase \n" +
                "\t\twhen sum(perfil) = 1 then 'T'\n" +
                "\t\twhen sum(perfil) = 2 then 'S'\n" +
                "\t\twhen sum(perfil) = 3 then 'U'\n" +
                "\tend as perfil\n" +
                "from (\n" +
                "\tselect 1 as perfil, count(id_portfolio_student) as nr from tb_portfolio_student where id_tutor = " + idUser + "\n" +
                "\tunion select 2, count(id_portfolio_student) from tb_portfolio_student where id_student = " + idUser + ")\n" +
                "where nr > 0";
        char userType = 'S';

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            userType = cursor.getString(0).charAt(0);
        }

//        db.close();
        return userType;
    }

    */
/*

    *************************CRUD TURMAS***********************************

*//*

    public List<Team> getClasses(int idUser, char userType) {
        String query = "select distinct\n" +
                "\tf.id_class,\n" +
                "\tf.id_proposer,\n" +
                "    f.ds_code,\n" +
                "\tf.ds_description,\n" +
                "\tf.st_status,\n" +
                "\tf.dt_start,\n" +
                "\tf.dt_finish\n" +
                "from \n" +
                "\ttb_portfolio_student a\n" +
                "\tjoin tb_user b on b.id_user = a.id_tutor\n" +
                "    join tb_user c on c.id_user = a.id_student\n" +
                "    join tb_portfolio_class d on d.id_portfolio_class = a.id_portfolio_class\n" +
                "    join tb_portfolio e on e.id_portfolio = d.id_portfolio\n" +
                "    join tb_class f on f.id_class = d.id_class\n" +
                "where 1=1 ";

        if (userType == 'S')
            query += "AND a.id_student=" + idUser;
        else if (userType == 'T')
            query += "AND a.id_tutor=" + idUser;

        List<Team> array_team = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                array_team.add(cursorToTeam(cursor));
            } while (cursor.moveToNext());
        }

//        db.close();
        return array_team;
    }

    private Team cursorToTeam(Cursor cursor) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Team team = null;
        try {
            String dateBegin = cursor.getString(5);
            String dateEnd = cursor.getString(6);

            if (dateBegin == null)
                dateBegin = "1900-01-01";

            if (dateEnd == null)
                dateEnd = "1900-01-01";

            team = new Team(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4).charAt(0), dateFormat.parse(dateBegin), dateFormat.parse(dateEnd));
        } catch (ParseException e) {
            Log.d(tag, "Data em formato errado! (cursorToTeam())");
        }

//        db.close();
        return team;
    }

    */
/*

    *************************OBTER ATIVIDADES***********************************

*//*


    public ArrayList<Activity> getActivities(int userId, int portfolioStudentId, char userType) {
        String query = "select \n" +
                "\ta.id_activity,\n" +
                "\ta.id_portfolio,\n" +
                "\ta.nu_order,\n" +
                "\ta.ds_title,\n" +
                "\ta.ds_description\n" +
                "from \n" +
                "\ttb_activity_student ac\n" +
                "    join tb_activity a on a.id_activity = ac.id_activity\n" +
                "    join tb_portfolio p on p.id_portfolio = a.id_portfolio\n" +
                "    join tb_portfolio_student ps on ps.id_portfolio_student = ac.id_portfolio_student    \n" +
                "    join tb_portfolio_class pc on pc.id_portfolio_class = ps.id_portfolio_class\n" +
                "    join tb_class c on c.id_class = pc.id_class\n" +
                "    join tb_user s on s.id_user = ps.id_student\n" +
                "where 1 = 1\n" +
                "\tand ps.id_portfolio_student = " + portfolioStudentId;
        if (userType == 'S')
            query += "\nand s.id_user = " + userId + ";";
        else if (userType == 'T')
            query += "\nand ps.id_tutor=" + userId + ";";

        ArrayList<Activity> array_activity = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        do {
            array_activity.add(cursorToActivity(cursor));
        } while (cursor.moveToNext());

//        db.close();
        return array_activity;
    }

    public String getActivityTitleByIdActivityStudent (int idActivityStudent) {
        String query = "SELECT ds_title FROM tb_activity JOIN tb_activity_student ON tb_activity.id_activity=tb_activity_student.id_activity WHERE id_activity_student="+idActivityStudent;
        Cursor cursor = db.rawQuery(query,null);
        String ds_title = null;

        if (cursor.moveToFirst()){
            ds_title = cursor.getString(0);
        }

        return ds_title;
    }

    */
/*

        *************************MÉTODO PARA SELECIONAR ALUNOS E AS ATIVIDADES QUE POSSUEM***********************************

    *//*

    public ArrayList<StudFrPortClass> selectListActivitiesAndStudents(int idPortfolioClass, String perfil, int idUsuario) {
        String query = "select \n" +
                "tas.id_activity_student,\n" +
                "u.id_user,\n" +
                "a.ds_title,\n" +
                "a.ds_description,\n" +
                "u.nm_user as nm_student,\n" +
                "tas.id_portfolio_student,\n" +
                "tas.id_activity,\n" +
                "u.im_photo,\n" +
                "u.nu_cellphone,\n" +
                "a.nu_order\n" +
                "FROM\n" +
                "\ttb_activity_student as tas\n" +
                "\tjoin tb_activity a on tas.id_activity = a.id_activity\n" +
                "\tjoin tb_portfolio_student ps on ps.id_portfolio_student = tas.id_portfolio_student\n" +
                "\tjoin tb_user u on u.id_user = ps.id_student\n" +
                "WHERE \n" +
                "\tps.id_portfolio_class=" + idPortfolioClass;
        if (perfil.equalsIgnoreCase("S"))
            query += " AND u.id_user = " + idUsuario;
        ArrayList<StudFrPortClass> students = new ArrayList<>();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            HashMap<Integer, StudFrPortClass> hash = new LinkedHashMap<Integer, StudFrPortClass>();
            int lastid;
            int idUser = 0;
            int cont = -1;
            do {
                lastid = idUser;
                idUser = c.getInt(1);

                String nameStudent = c.getString(4);
                String photo = c.getString(7);
                String cellphone = c.getString(8);
                Activity a = new Activity(c.getInt(0), c.getInt(6), c.getString(2), c.getString(3), c.getInt(9));
                a.setId_portfolio(c.getInt(5));

                if (lastid == idUser) {
                    students.get(cont).setNameStudent(nameStudent);
                    students.get(cont).add(a);
                } else {
                    StudFrPortClass student = new StudFrPortClass();
                    student.setNameStudent(nameStudent);
                    student.add(a);
                    student.setPhoto(photo);
                    student.setCellphone(cellphone);
                    students.add(student);
                    cont++;
                }
//

            } while (c.moveToNext());

        } else {
            Log.d(tag, "Nao retornou nada na consulta");
        }
        return students;
    }
    public ArrayList<StudFrPortClass> selectListActivitiesAndStudentsByStudent(int idPortfolioClass, String perfil, int idUsuario) {
        String query = "select \n" +
                "tas.id_activity_student,\n" +
                "u.id_user,\n" +
                "a.ds_title,\n" +
                "a.ds_description,\n" +
                "u.nm_user as nm_student,\n" +
                "tas.id_portfolio_student,\n" +
                "tas.id_activity,\n" +
                "u.im_photo,\n" +
                "u.nu_cellphone,\n" +
                "a.nu_order\n" +
                "FROM\n" +
                "\ttb_activity_student as tas\n" +
                "\tjoin tb_activity a on tas.id_activity = a.id_activity\n" +
                "\tjoin tb_portfolio_student ps on ps.id_portfolio_student = tas.id_portfolio_student\n" +
                "\tjoin tb_user u on u.id_user = ps.id_student\n" +
                "WHERE \n" +
                "\tps.id_portfolio_class=" + idPortfolioClass;
//        if (perfil.equalsIgnoreCase("S"))
            query += " AND u.id_user = " + idUsuario;
        ArrayList<StudFrPortClass> students = new ArrayList<>();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            HashMap<Integer, StudFrPortClass> hash = new LinkedHashMap<Integer, StudFrPortClass>();
            int lastid;
            int idUser = 0;
            int cont = -1;
            do {
                lastid = idUser;
                idUser = c.getInt(1);

                String nameStudent = c.getString(4);
                String photo = c.getString(7);
                String cellphone = c.getString(8);
                Activity a = new Activity(c.getInt(0), c.getInt(6), c.getString(2), c.getString(3), c.getInt(9));
                a.setId_portfolio(c.getInt(5));

                if (lastid == idUser) {
                    students.get(cont).setNameStudent(nameStudent);
                    students.get(cont).add(a);
                } else {
                    StudFrPortClass student = new StudFrPortClass();
                    student.setNameStudent(nameStudent);
                    student.add(a);
                    student.setPhoto(photo);
                    student.setCellphone(cellphone);
                    students.add(student);
                    cont++;
                }
//

            } while (c.moveToNext());

        } else {
            Log.d(tag, "Nao retornou nada na consulta");
        }
        return students;
    }

    */
/*

        ************************* retorna uma lista com as turmas que o usuário ***************
        * ********************** esta cadastro e seu papel nela(tutor ou aluno) ***************
        *********************** perfil S- Student T-tutor ***********************************

    *//*

    public List<PortfolioClass> selectListClassAndUserType(int idUser) {
        //retorna uma lista com as turmas que o usuário esta cadastro e seu papel nela(tutor ou aluno);
        // perfil S- Student T-tutor
        String query = "select distinct \n" +
                "\tps.id_portfolio_class,\n" +
                "\tc.ds_code,\n" +
                "\tc.ds_description,\n" +
                "\tp.ds_title,\n" +
                "\tp.ds_description,\n" +
                "\tcase \n" +
                "\t\twhen id_student = " + idUser + " then 'S'\n" +
                "\t\twhen id_tutor = " + idUser + " then 'T' \n" +
                "\tend as perfil\n" +
                "from \n" +
                "\ttb_portfolio_student as ps \n" +
                "\tjoin tb_portfolio_class pc on pc.id_portfolio_class = ps.id_portfolio_class\n" +
                "\tjoin tb_class c on c.id_class = pc.id_class \n" +
                "\tjoin tb_portfolio p on p.id_portfolio = pc.id_portfolio\n" +
                "WHERE\n" +
                "\t( id_tutor = " + idUser + " OR id_student = " + idUser + " )";
        Cursor c = db.rawQuery(query, null);
        ArrayList lista = new ArrayList<PortfolioClass>();
        if (c.moveToFirst()) {
            do {
                int idPortClass = c.getInt(0);
                String classCode = c.getString(1);
                String portTitle = c.getString(3);
                String perfil = c.getString(5);
                PortfolioClass p = new PortfolioClass(classCode, idPortClass, perfil, portTitle);
                lista.add(p);
                Log.d(tag, "port class:" + p.toString());
            } while (c.moveToNext());
        } else {
            Log.d(tag, "Nao retornou nada na consulta");
        }

        return lista;
    }

    private Activity cursorToActivity(Cursor cursor) {
        Activity activity = new Activity(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4));
        return activity;
    }

    public void saveAttachmentActivityStudent(String path, String type, int idActivityStudent) {
        ContentValues values = new ContentValues();

        values.put("id_activity_student", idActivityStudent);
        values.put("ds_local_path", path);
        values.put("ds_type", type);

        db.insert("tb_attachment", null, values);
    }

    public int getActivityStudentID(int idActivity, int idPortfolioStudent) {
        String query = "SELECT  id_activity_student FROM tb_activity_student WHERE \n" +
                "\tid_portfolio_student = " + idPortfolioStudent + " and id_activity = " + idActivity + ";";
        Log.d(tag, "query getActStuID:" + query);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            Log.d(tag, "idAcStudent:" + cursor.getInt(0));
            return cursor.getInt(0);
        }
        //db.close();
        cursor.close();
        return -1;
    }


    public boolean deleteAttachment(Attachment attachment) {
        String query = "id_attachment = " + attachment.getIdAttachment() + " AND\n" +
                "NOT EXISTS (SELECT NULL FROM tb_attach_comment WHERE tb_attach_comment.id_attachment = tb_attachment.id_attachment) AND\n" +
                "NOT EXISTS (SELECT NULL FROM tb_attach_activity WHERE tb_attach_activity.id_attachment = tb_attachment.id_attachment);";
        return db.delete("tb_attachment", query, null) > 0;
    }

    public ArrayList<Attachment> getAttachments() {
        String query = "SELECT * FROM tb_attachment;";

        ArrayList<Attachment> array_attachment = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        do {
            if (cursor.getCount() != 0)
                array_attachment.add(cursorToAttachment(cursor));
        } while (cursor.moveToNext());

        return array_attachment;
    }

    public ArrayList<Attachment> getAttachmentsFromActivityStudent(int idActivityStudent) {
        String query = "SELECT * FROM tb_attachment WHERE id_activity_student = " + idActivityStudent + ";";

        ArrayList<Attachment> array_attachment = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        do {
            if (cursor.getCount() != 0)
                array_attachment.add(cursorToAttachment(cursor));
        } while (cursor.moveToNext());

        return array_attachment;
    }

    public ArrayList<Attachment> getAttachmentsFromActivityStudent(int idActivityStudent, String type) {
        String query = "SELECT * FROM tb_attachment WHERE id_activity_student = " + idActivityStudent + " and ds_type = '" + type + "';";

        ArrayList<Attachment> array_attachment = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);

        do {
            if (cursor.getCount() != 0)
                array_attachment.add(cursorToAttachment(cursor));
        } while (cursor.moveToNext());

        return array_attachment;
    }

    public ArrayList<Attachment> getAttachmentsFromStudent() {
        String query = "SELECT * FROM tb_attachment;";

        ArrayList<Attachment> array_attachment = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);

        do {
            if (cursor.getCount() != 0)
                array_attachment.add(cursorToAttachment(cursor));
        } while (cursor.moveToNext());

        return array_attachment;
    }

    private Attachment cursorToAttachment(Cursor cursor) {
        Attachment attachment = new Attachment(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
        return attachment;
    }


    public int insertAttachActivity(int lastIdAttach, int idActivityStudent) {
        ContentValues cv = new ContentValues();
        cv.put("id_attachment", lastIdAttach);
        cv.put("id_activity_student", idActivityStudent);

        try {
            db.insert("tb_attach_activity", null, cv);
        } catch (Exception e) {
            Log.e(tag, "erro ao salvar na tabela tb_attach_activity:" + e.getMessage());
        }

        Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name='tb_attach_activity'", null);
        int lastID = 0;
        if (cursor.moveToFirst()) {
            lastID = cursor.getInt(0);
            Log.d(tag, "last id_comment id table:" + lastID);
        }
        return lastID;
    }


    */
/*
        ************************* CRUD TB_SYNC ***************************
    *//*



    public void insertIntoTBSyncOLD(Sync sync) {
        ContentValues cv = new ContentValues();
        cv.put("id_device", sync.getId_device());
        cv.put("nm_table", sync.getNm_table());
        cv.put("co_id_table", sync.getCo_id_table());
        cv.put("id_activity_student", sync.getId_activity_student());
        //cv.put("dt_send", sync.getDt_sync());
        try {
            db.insert("tb_sync", null, cv);
        } catch (Exception e) {
            Log.e(tag, "erro ao salvar na tabela tb_attach_activity:" + e.getMessage());
        }
    }

    public void insertIntoTBSync(Sync sync) {
        ContentValues cv = new ContentValues();
        cv.put("nm_table", sync.getNm_table());
        cv.put("co_id_table", sync.getCo_id_table());
        cv.put("id_activity_student", sync.getId_activity_student());
        //cv.put("dt_send", sync.getDt_sync());
        try {
            db.insert("tb_sync", null, cv);
        } catch (Exception e) {
            Log.e(tag, "erro ao salvar na tabela tb_attach_activity:" + e.getMessage());
        }
    }



    public ArrayList getSyncs() {
        String query = "SELECT id_sync, co_id_table, nm_table from tb_sync";
        ArrayList syncs = new ArrayList<Sync>();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                Sync s = new Sync(c.getInt(0), "-1", c.getInt(1), c.getString(2));
                syncs.add(s);
                Log.d("Syncs",s.toString());
            } while (c.moveToNext());
        } else {

        }

//        db.close();

        return syncs;
    }

    public void updateTableBySync(Object updateObj, String nameTable, int colunmID) {
        switch (nameTable) {
            case "tb_comment":
                updateComment((Comentario) updateObj);
                break;
            default:
                Log.d("banco updateTableBySync", "Nome de tabela inválido");

        }

    }

    public int getActivityNotification(int id_activity_student) {
        String query = "SELECT COUNT(*) \n" +
                "FROM tb_activity_student as tbas \n" +
                "\tJOIN tb_notice tbs on tbs.id_activity_student = tbas.id_activity_student \n" +
                "WHERE\n" +
                "\ttbs.id_activity_student = " + id_activity_student;
        int result = 0;

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            int num = c.getInt(0);
            result += num;
        }

        c.close();

        return result;
    }

    public int getPortfolioClassNotification(int id_portfolio_class) {
        String query = "SELECT DISTINCT id_portfolio_student \n" +
                "FROM tb_portfolio_student as tbps" +
                " WHERE tbps.id_portfolio_class = " + id_portfolio_class;
        int result = 0;

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst())
            do {
                int id = c.getInt(0);
                result += getNumNotifications(id);
            } while (c.moveToNext());

        c.close();

        return result;
    }

    private int getNumNotifications(int id_portfolio_student) {
        String query = "SELECT COUNT(*) \n" +
                "FROM tb_notice as tbn\n" +
                "\tJOIN tb_activity_student tbas on tbas.id_activity_student = tbn.id_activity_student\n" +
                " WHERE tbas.id_portfolio_student = " + id_portfolio_student;
        // ADICIONAR CLAUSULAS WHERE PARA VEFIFICAR SE O TIPO DE COMENTÁRIO É R (RECEBIMENTO) E SE A DATA DE READ É NULL
        int result = 0;

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            int val = c.getInt(0);
            result = val;
        }

        c.close();

        return result;
    }

    public int getAllNotifications(){
        String query = "SELECT COUNT(*) FROM tb_notice";

        int result = 0;

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            int val = c.getInt(0);
            result = val;
        }

        c.close();

        return result;
    }

    public void deleteAllNotifications(ArrayList<Integer> ids) {
        if (ids != null && ids.size() != 0) {
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM tb_notice WHERE id_notice IN (");
            query.append(ids.get(0));

            for (int i = 1; i < ids.size(); i++) {
                query.append(", " + ids.get(i));
            }

            query.append(");");

            try {
                db.execSQL(query.toString());
                Log.d(tag, "removeu NOTICES");
            } catch (Exception e) {
                Log.e(tag, "erro ao delete NOTICES:" + e.getMessage());
            }
        }
    }

    public ArrayList<Integer> getNonCommentsNotifications(int idActivityStudent) {
        String query = "SELECT id_notice FROM tb_notice WHERE id_activity_student = " + idActivityStudent + " AND nm_table != 'tb_comment'";
        ArrayList<Integer> aux = new ArrayList<>();

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                aux.add(c.getInt(0));
            } while (c.moveToNext());
        }

        c.close();

        return aux;
    }

    public ArrayList<Integer> getCommentNotificationIds(int id_activity_student) {
        String query = "SELECT tbs.co_id_table_srv FROM tb_activity_student as tbas\n" +
                "JOIN tb_notice tbs on tbs.id_activity_student = tbas.id_activity_student\n" +
                "WHERE tbs.id_activity_student = " + id_activity_student + " AND tbs.nm_table = 'tb_comment'";
        ArrayList<Integer> result = new ArrayList<>();

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                result.add(c.getInt(0));
            } while (c.moveToNext());
        }

        c.close();

        return result;
    }

    public int getAllGeneralCommentsNotifications(int idActivityStudent) {
        int result = 0;
        for (Integer id : getCommentNotificationIds(idActivityStudent)) {
            String query = "SELECT COUNT(*) FROM tb_comment tbc WHERE \n" +
                    "\ttbc.tp_comment = 'G' AND\n" +
                    "\ttbc.id_comment_srv = " + id;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                result += c.getInt(0);
            }
            c.close();
        }

        return result;
    }

    public ArrayList<Integer> getGeneralCommentsNotifications(int idActivityStudent) {
        String query = "SELECT tbs.co_id_table_srv, tbs.id_notice FROM tb_activity_student as tbas\n" +
                "JOIN tb_notice tbs on tbs.id_activity_student = tbas.id_activity_student\n" +
                "WHERE tbs.id_activity_student = " + idActivityStudent + " AND tbs.nm_table = 'tb_comment'";
        ArrayList<Integer> result = new ArrayList<>();

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                String sql = "SELECT COUNT(*) FROM tb_comment tbc WHERE \n" +
                        "\ttbc.tp_comment = 'G' AND\n" +
                        "\ttbc.id_comment_srv = " + c.getInt(0);
                Cursor newCursor = db.rawQuery(sql, null);
                if (newCursor.moveToFirst()) {
                    if (newCursor.getInt(0) > 0)
                        result.add(c.getInt(1));
                }
                newCursor.close();

            } while (c.moveToNext());
        }

        c.close();

        return result;
    }

    public ArrayList<Integer> getSpecificCommentsNotificationsID(int idActivityStudent, int idVersionActivitySrv) {
        String query = "SELECT tbs.co_id_table_srv, tbs.id_notice FROM tb_activity_student as tbas\n" +
                "JOIN tb_notice tbs on tbs.id_activity_student = tbas.id_activity_student\n" +
                "WHERE tbs.id_activity_student = " + idActivityStudent + " AND tbs.nm_table = 'tb_comment'";
        ArrayList<Integer> result = new ArrayList<>();

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                String sql = "SELECT COUNT(*) from tb_comment as tbc " +
                        "JOIN tb_comment_version as tbcv on tbcv.id_comment_version_srv = tbc.id_comment_version " +
                        "JOIN tb_version_activity as tbva on tbva.id_version_activity_srv = tbcv.id_version_activity" +
                        " WHERE tbva.id_version_activity_srv = " + idVersionActivitySrv + " AND tbc.id_comment_srv = " + c.getInt(0) + " AND tbc.tp_comment = 'O'";

                Cursor newCursor = db.rawQuery(sql, null);
                if (newCursor.moveToFirst()) {
                    if (newCursor.getInt(0) > 0)
                        result.add(c.getInt(1));
                }
                newCursor.close();
            } while (c.moveToNext());
        }

        c.close();
        return result;
    }

    public boolean hasSpecificComment(int idActivityStudent) {
        for (Integer id : getCommentNotificationIds(idActivityStudent)) {
            String query = "SELECT COUNT(*) FROM tb_comment tbc WHERE \n" +
                    "\ttbc.tp_comment = 'O' AND\n" +
                    "\ttbc.id_comment_srv = " + id;

            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                if (c.getInt(0) > 0)
                    return true;
            }
            c.close();
        }

        return false;
    }

    public int getSpecificCommentNotifications(int idActivityStudent, int idVersionActivity) {
        String query = "SELECT tbs.co_id_table_srv FROM tb_activity_student as tbas\n" +
                "JOIN tb_notice tbs on tbs.id_activity_student = tbas.id_activity_student\n" +
                "WHERE tbs.id_activity_student = " + idActivityStudent + " AND tbs.nm_table = 'tb_comment'";
        Integer result = 0;

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                String sql = "SELECT COUNT(*) from tb_comment as tbc " +
                        "JOIN tb_comment_version as tbcv on tbcv.id_comment_version_srv = tbc.id_comment_version " +
                        "JOIN tb_version_activity as tbva on tbva.id_version_activity_srv = tbcv.id_version_activity" +
                        " WHERE tbva.id_version_activity_srv = " + idVersionActivity + " AND tbc.id_comment_srv = " + c.getInt(0) + " AND tbc.tp_comment = 'O'";

                Cursor newCursor = db.rawQuery(sql, null);
                if (newCursor.moveToFirst()) {
                    result += newCursor.getInt(0);
                }
                newCursor.close();
            } while (c.moveToNext());
        }

        c.close();
        return result;
    }

     */
/*
        ************************* CRUD TB_DEVICE ***************************
    *//*


    public void insertIntoTbDevice(Device d) {
        ContentValues cv = new ContentValues();
        cv.put("id_device", d.get_id_device());
        cv.put("id_user", d.get_id_user());
        cv.put("tp_device", d.get_tp_device());
        cv.put("fl_first_login", "T");
        try {
            db.insert("tb_device", null, cv);
        } catch (Exception e) {
            Log.e(tag, "erro ao salvar na tabela tb_device:" + e.getMessage());
        }
    }


    public Device getDevice() {
        String query = "SELECT * from tb_device";
        Device device = new Device();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            device = new Device(c.getString(0), c.getInt(1), c.getInt(2), c.getString(3), c.getString(4)*/
/*, c.getString(5)*//*
);
        } else {
            Log.e(tag, "não há registros na tabela tb_device");
        }
        return device;
    }

    */
/*
       ************************* DEBUG ***************************
       *  Log.d("lista", "tam tb_class:" + source.getCountTbPortfolioStudent());
           Log.d("lista", "tam tb_class_student:" + source.getCountTbPortfolioStudent());
           Log.d("lista", "tam tb_class_tutor:" + source.getCountTbPortfolioStudent());
           Log.d("lista", "tam tb_portfolio:" + source.getCountTbPortfolioStudent());
           Log.d("lista", "tam tb_portfolio_class:" + source.getCountTbPortfolioStudent());
           Log.d("lista", "tam tb_portfolio_student:" + source.getCountTbPortfolioStudent());
           Log.d("lista", "tam tb_activity:" + source.getCountTbPortfolioStudent());
           Log.d("lista", "tam tb_activity_student:" + source.getCountTbPortfolioStudent());
   *//*

    public String getCountTbClass() {
        String query = "SELECT * FROM tb_class";
        String result = null;
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                result = c.getInt(0) + ", " + c.getInt(1) + ", " + c.getString(2) + ", " + c.getString(3) + ", " + c.getString(4) + ", ... \n";
            } while (c.moveToNext());
        } else {
            Log.e(tag, "não há registros na tabela tb_class");
        }

        return result;
    }

    public String getCountTbActivityStudent() {
        String query = "SELECT * FROM tb_activity_student";
        String result = null;
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                result = c.getInt(0) + ", " + c.getInt(1) + ", " + c.getInt(2) + ", " + c.getString(3) + ", " + c.getString(4) + ", ... \n";
            } while (c.moveToNext());
        } else {
            Log.e(tag, "não há registros na tabela tb_activity_student");
        }

        return result;
    }

    public String getCountTbPortfolioStudent() {
        String query = "SELECT * FROM tb_portfolio_student";
        String result = null;
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                result = c.getInt(0) + ", " + c.getInt(1) + ", " + c.getInt(2) + ", " + c.getString(3) + ", " + c.getString(4) + ", ... \n";
            } while (c.moveToNext());
        } else {
            Log.e(tag, "não há registros na tabela tb_portfolio_student");
        }

        return result;
    }


    public void updateDeviceBasicDataSync() {
        ContentValues cv = new ContentValues();
        cv.put("fl_basic_data", "T");
        Device device = getDevice();
        try {
            db.update("tb_device", cv, null, null);
            Log.e(tag, "Conseguiu alterar tb_device fl_basic_data");
        } catch (Exception e) {
            Log.e(tag, "Erro ao alterar tb_device fl_basic_data");
        }
    }

    public boolean updateTBUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put("ds_email", user.getEmail());
        if (user.getPassword() != null)
            cv.put("ds_password", user.getPassword());
        if (user.getCellphone() != null)
            cv.put("nu_cellphone", user.getCellphone());
        if (user.getPhoto() != null)
            cv.put("im_photo", user.getPhoto());
        try {
            db.update("tb_user", cv, "id_user = ?", new String[]{String.valueOf(user.getIdUser())});
            Log.e(tag, "Conseguiu alterar tb_user");
            return true;
        } catch (Exception e) {
            Log.e(tag, "Erro ao alterar tb_user");
            return false;
        }
    }

    public void updateTBUser(LinkedList<com.ufcspa.unasus.appportfolio.Model.basicData.User> users) {
        for (com.ufcspa.unasus.appportfolio.Model.basicData.User u : users) {
            ContentValues cv = new ContentValues();
            cv.put("ds_email", u.getEmail());
            cv.put("nu_cellphone", u.getCellphone());
            cv.put("im_photo", u.getPhoto());
            try {
                db.update("tb_user", cv, "id_user = ?", new String[]{String.valueOf(u.getIdUser())});
                Log.e(tag, "Conseguiu alterar tb_user");
            } catch (Exception e) {
                Log.e(tag, "Erro ao alterar tb_user");
            }
        }
    }

    public int getStatus(Device d) {
        Device device = d;
        if (device.get_id_device() == null)
            return -1;
        if (device.getFl_basic_data() != null)
            return 1;
        else
            return 0;
    }

    public User getUser() {
        User user = new User(0, null, null);

        String query = "SELECT id_user FROM tb_device";
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            int id_user = c.getInt(0);

            query = "SELECT nm_user, nu_identification, ds_email, nu_cellphone, im_photo FROM tb_user WHERE id_user = " + id_user;
            c = db.rawQuery(query, null);

            if (c.moveToFirst()) {
                user = new User(id_user, c.getString(0), c.getString(1), c.getString(2), c.getString(3));
                user.setPhoto(c.getString(4), null);
            }
        }

        return user;
    }

    */
/*
        ************************* CRUD FULL DATA ***************************
    *//*

    public void insertVersionActivity(LinkedList<VersionActivity> versionActs) {
        for (VersionActivity va : versionActs) {

            ContentValues cv = new ContentValues();
            cv.put("id_activity_student", va.getId_activity_student());
            cv.put("tx_activity", va.getTx_activity());
            cv.put("dt_last_access", va.getDt_last_access());
            cv.put("dt_submission", va.getDt_submission());
            cv.put("dt_verification", va.getDt_verification());
            cv.put("id_version_activity_srv", va.getId_version_activit_srv());


            String query = "SELECT * FROM tb_version_activity WHERE id_version_activity_srv=" + va.getId_version_activit_srv();
            Cursor c = db.rawQuery(query, null);

            if (c.moveToFirst()) {
                db.update("tb_version_activity", cv, "id_version_activity_srv=" + va.getId_version_activit_srv(), null);
                continue;
            }
            try {
                db.insert("tb_version_activity", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_version_activity:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    // VERSÃO ANTIGA DE INSERT COMMENTS, ATÉ 07/06/2016
    public void insertCommentsOLD(LinkedList<Comentario> comentarios) {
        for (Comentario c : comentarios) {
            ContentValues cv = new ContentValues();
            cv.put("id_activity_student", c.getIdActivityStudent());
            cv.put("id_author", c.getIdAuthor());
            cv.put("tx_comment", c.getTxtComment());
            cv.put("tx_reference", c.getTxtReference());
            cv.put("tp_comment", c.getTypeComment());
            cv.put("nu_comment_activity", c.getIdNote());
            cv.put("id_comment_srv", c.getIdCommentSrv());
            cv.put("dt_comment", c.getDateComment());
            cv.put("dt_send", c.getDateSend());

            try {
                db.insert("tb_comment", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_comment:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertComments(LinkedList<Comentario> comentarios) {

        Log.d(tag, " full data response> cometarios a serem inseridos:\n"+comentarios);
        for (Comentario c : comentarios) {
            ContentValues cv = new ContentValues();
            cv.put("id_activity_student", c.getIdActivityStudent());
            cv.put("id_comment_version", c.getId_comment_version());
            cv.put("id_author", c.getIdAuthor());
            cv.put("tx_comment", c.getTxtComment());
            cv.put("tp_comment", c.getTypeComment());
            cv.put("id_comment_srv", c.getIdCommentSrv());
            cv.put("dt_comment", c.getDateComment());
            cv.put("dt_send", c.getDateSend());

            try {
                db.insert("tb_comment", null, cv);
                Log.d(tag, "inserindo comment no sqlite:" + c);
            } catch (Exception e) {
                Log.e(tag, "erro ao inserir na tb_comment:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }





    public void insertAttachment(LinkedList<Attachment> anexos) {
        for (Attachment a : anexos) {
            ContentValues cv = new ContentValues();
            cv.put("tp_attachment", a.getTpAttachment());
            cv.put("nm_file", a.getNmFile());
            cv.put("nm_system", a.getNmSystem());
            cv.put("id_attachment_srv", a.getidAttachmentSrv());

            try {
                db.insert("tb_attachment", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_attachment:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertAttachmentActivity(int id_activity_student, int id_attachment) {
        ContentValues cv = new ContentValues();
        cv.put("id_attachment", id_attachment);
        cv.put("id_activity_student", id_activity_student);

        try {
            db.insert("tb_attach_activity", null, cv);

        } catch (Exception e) {
            Log.d(tag, "erro ao inserir na tb_attach_activity:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertAttachmentComment(LinkedList<AttachmentComment> anexosComentario) {
        for (AttachmentComment ac : anexosComentario) {
            ContentValues cv = new ContentValues();
            cv.put("id_attachment", ac.getId_attachment());
            cv.put("id_comment", ac.getId_comment());

            try {
                db.insert("tb_attach_comment", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_attach_comment:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void insertReference(LinkedList<Reference> references) {
        for (Reference r : references) {
            ContentValues cv = new ContentValues();
            cv.put("id_activity_student", r.getIdActStudent());
            cv.put("ds_url", r.getDsUrl());
            cv.put("id_reference_srv", r.getIdRefSrv());

            try {
                db.insert("tb_reference", null, cv);
            } catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_reference:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertNotifications(LinkedList<Notification> notifications) {
        for (Notification n : notifications) {
            ContentValues cv = new ContentValues();
            cv.put("id_notice", n.getId_notice());
            cv.put("id_author", n.getId_author());
            cv.put("id_destination", n.getId_destination());
            cv.put("id_activity_student", n.getId_activity_student());
            cv.put("nm_table", n.getNm_table());
            cv.put("co_id_table_srv", n.getCo_id_table_srv());
            cv.put("dt_notice", n.getDt_notice());
            cv.put("dt_read", n.getDt_read());

            try {
                db.insert("tb_notice", null, cv);
            }
            catch (SQLiteConstraintException v){
                Log.d(tag, "id notification já existe:" + v.getMessage());
                v.printStackTrace();
            }

            catch (Exception e) {
                Log.d(tag, "erro ao inserir na tb_notice:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }




    */
/*
        ************************* CRUD FULL DATA SEND ***************************
    *//*

    public List<User> getUsersByIDs(LinkedList<Integer> ids) {
        StringBuilder sb = new StringBuilder("select " +
                "id_user," +
                "nm_user," +
                "nu_identification," +
                "ds_email," +
                "nu_cellphone," +
                "im_photo" +
                " from tb_user where id_user = " + ids.get(0));
        Log.d(tag + " get users by ids", " query:" + sb.toString());
        String query = sb.toString();
        Cursor c = db.rawQuery(query, null);
        LinkedList lista = new LinkedList<User>();
        if (c.moveToFirst()) {
            do {
                User user = new User();
                user.setIdUser(c.getInt(0));
                user.setName(c.getString(1));
                user.setIdCode(c.getString(2));
                user.setEmail(c.getString(3));
                user.setCellphone(c.getString(4));
                user.setPhoto(c.getString(5), null);
                lista.add(user);
            } while (c.moveToNext());
        } else {
            Log.d(tag, "Nao retornou nada na consulta");
        }
        return lista;
    }

    public List<Comentario> getCommentsByIDsOLD(LinkedList<Integer> ids) {
        StringBuilder sb = new StringBuilder("select " +
                "id_comment," +
                "id_activity_student," +
                "id_author," +
                "tx_comment," +
                "tx_reference," +
                "tp_comment," +
                "dt_comment," +
                "nu_comment_activity" +
                " from tb_comment where id_comment in ( ");
        for (int id :ids){
            if(id==ids.getLast()){
                sb.append(""+id+" );");
                break;
            }else{
                sb.append(""+id+" , ");
            }
        }
        Log.d(tag+" get comments by ids"," query:"+sb.toString());
        String query = sb.toString();
        Cursor c = db.rawQuery(query, null);
        LinkedList lista = new LinkedList<Comentario>();
        if (c.moveToFirst()) {
            do {
                Comentario comm = new Comentario();
                comm.setIdComment(c.getInt(0));
                comm.setIdActivityStudent(c.getInt(1));
                comm.setIdAuthor(c.getInt(2));
                comm.setTxtComment(c.getString(3));
                comm.setTxtReference(c.getString(4));
                comm.setTypeComment(c.getString(5));
                comm.setDateComment(c.getString(6));
                comm.setIdNote(c.getInt(7));
                lista.add(comm);
            } while (c.moveToNext());
        } else {
            Log.d(tag, "Nao retornou nada na consulta");
        }
        return lista;
    }

    public List<Comentario> getCommentsByIDs(LinkedList<Integer> ids) {
        StringBuilder sb = new StringBuilder("select " +
                "id_comment," +
                "id_activity_student," +
                "id_author," +
                "tx_comment," +
                "tp_comment," +
                "dt_comment," +
                "id_comment_version," +
                "id_comment_srv" +
                " from tb_comment where id_comment in ( ");
        for (int id :ids){
            if(id==ids.getLast()){
                sb.append(""+id+" );");
                break;
            }else{
                sb.append(""+id+" , ");
            }
        }
        Log.d(tag+" get comments by ids"," query:"+sb.toString());
        String query = sb.toString();
        Cursor c = db.rawQuery(query, null);
        LinkedList lista = new LinkedList<Comentario>();
        if (c.moveToFirst()) {
            do {
                Comentario comm = new Comentario();
                comm.setIdComment(c.getInt(0));
                comm.setIdActivityStudent(c.getInt(1));
                comm.setIdAuthor(c.getInt(2));
                comm.setTxtComment(c.getString(3));
                comm.setTypeComment(c.getString(4));
                comm.setDateComment(c.getString(5));
                comm.setIdNote(c.getInt(6));
                comm.setId_comment_version(c.getInt(6));
                comm.setIdCommentSrv(c.getInt(7));
                lista.add(comm);
            } while (c.moveToNext());
        } else {
            Log.d(tag, "Nao retornou nada na consulta");
        }
        return lista;
    }



    //nao finalizado ainda
    public List<VersionActivity> getVersionActivitiesByIDs(LinkedList<Integer> ids) {
        StringBuilder sb = new StringBuilder("select * from tb_version_activity where id_version_activity in ( ");
        for (int id :ids){
            if(id==ids.getLast()){
                sb.append(""+id+" );");
                break;
            }else{
                sb.append("" + id + " , ");
            }
        }
        Log.d(tag + "get comments by ids", " query:" + sb.toString());
        String query = sb.toString();
        Cursor c = db.rawQuery(query, null);
        LinkedList lista = new LinkedList<VersionActivity>();
        if (c.moveToFirst()) {
            do {
//                c.moveToFirst();
                VersionActivity versionActivity = new VersionActivity();
                versionActivity.setId_version_activity(c.getInt(0));
                versionActivity.setId_activity_student(c.getInt(1));
                versionActivity.setTx_activity(c.getString(2));
//                versionActivity.setTx_activity(versionActivity.getTx_activity().replaceAll("\\/", "/"));
//                versionActivity.setTx_activity(versionActivity.getTx_activity().replaceAll("\n", ""));
//                versionActivity.setTx_activity(versionActivity.getTx_activity().replaceAll("<br\\/>", "<br/>"));
                if (c.getString(3) != null)
                    versionActivity.setDt_last_access(c.getString(3));
                else
                    versionActivity.setDt_last_access("2000-01-01 00:00:00");

                if (c.getString(4) != null)
                    versionActivity.setDt_submission(c.getString(4));
                else
                    versionActivity.setDt_submission("");

                if (c.getString(5) != null)
                    versionActivity.setDt_verification(c.getString(5));
                else
                    versionActivity.setDt_verification("");

                versionActivity.setId_version_activit_srv(c.getInt(6));

                lista.add(versionActivity);
//                c.close();
            } while (c.moveToNext());
        } else {
            Log.d(tag, "Nao retornou nada na consulta");
        }
        return lista;
    }

    public void updateCommentBySendFullData(LinkedList<HolderIDS> holderIDS) {
        ContentValues cv = new ContentValues();
        for (HolderIDS ids : holderIDS) {
            cv.put("id_comment_srv", ids.getIdSrv());
            cv.put("dt_send", ids.getDate());
            if(ids.getIdcvSrv()>0){
                cv.put("id_comment_version", ids.getIdcvSrv());
            }
            try {
                db.update("tb_comment", cv, "id_comment=?", new String[]{"" + ids.getId()});
            } catch (Exception e) {
                Log.d(tag, e.getMessage());
            }
        }
    }

    public void updateCommentVersionBySendFullData(LinkedList<HolderIDS> holderIDS) {
        ContentValues cv = new ContentValues();
        for (HolderIDS holder : holderIDS) {
            cv.put("id_comment_version_srv", holder.getIdSrv());
            cv.put("fl_srv","S");
            try {
                db.update("tb_comment_version", cv, "id_comment_version=?", new String[]{"" + holder.getId()});
            } catch (Exception e) {
                Log.d(tag, e.getMessage());
            }
        }
    }

    public void updateLastObservation(int id_comment_version) {
        ContentValues cv = new ContentValues();
        cv.put("id_comment_version_srv", id_comment_version);
        try {
            db.update("tb_comment_version", cv, "id_comment_version=?", new String[]{"" +id_comment_version});
        } catch (Exception e) {
            Log.d(tag, e.getMessage());
        }
    }




    public void updateVersionsBySendFullData(LinkedList<HolderIDS> holderIDS) {
        ContentValues cv = new ContentValues();
        for (HolderIDS ids : holderIDS) {
            cv.put("id_version_activity_srv", ids.getIdSrv());
            cv.put("dt_submission", ids.getDate());
            try {
                db.update("tb_version_activity", cv, "id_version_activity=?", new String[]{"" + ids.getId()});
            } catch (Exception e) {
                Log.d(tag, e.getMessage());
            }
        }
    }

    public void updateVersionsBySendFullData(int idVersionSrv, String dtSubmission, int idVersion) {
        ContentValues cv = new ContentValues();
        cv.put("id_version_activity_srv", idVersionSrv);
        cv.put("dt_submission", dtSubmission);
        try {
            db.update("tb_version_activity", cv, "id_version_activity=?", new String[]{"" + idVersion});
        } catch (Exception e) {
            Log.d(tag, e.getMessage());
        }

    }
    public void updateVersions(VersionActivity va) {
        ContentValues cv = new ContentValues();
//        cv.put("id_activity_student", va.getId_activity_student());
        cv.put("tx_activity", va.getTx_activity());
//        cv.put("dt_last_access", va.getDt_last_access());
//        cv.put("dt_submission", va.getDt_submission());
//        cv.put("dt_verification", va.getDt_verification());
//        cv.put("id_version_activity_srv", va.getId_version_activit_srv());


        String query = "SELECT * FROM tb_version_activity WHERE id_version_activity_srv=" + va.getId_version_activit_srv();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            db.update("tb_version_activity", cv, "id_version_activity_srv=" + va.getId_version_activit_srv(), null);
        }
    }
    public int getIDVersionSrvByLocalID(int idVersion){
        StringBuilder sb = new StringBuilder("SELECT id_version_activity_srv FROM tb_version_activity where id_version_activity="+idVersion);
        Cursor c = db.rawQuery(sb.toString(), null);
        if (c.moveToFirst()) {
            return c.getInt(0);
        }else{
            return -1;
        }
    }




    public void deleteSync(LinkedList<Integer> ids) {
        StringBuilder sb = new StringBuilder("DELETE FROM tb_sync where id_sync in (");
        for (int id : ids) {
            if (id == ids.getLast()) {
                sb.append("" + id + " );");
                break;
            } else {
                sb.append("" + id + " , ");
            }
        }
        Log.d(tag + " DELETE tb_sync", " query:" + sb.toString());
        String query = sb.toString();

        try {
            db.execSQL(query);
            Log.d(tag, "Deletou da tb_sync com sucesso");
        } catch (Exception e) {
            Log.d(tag, "Nao deletou da tb_sync");
        }
    }

    public void deleteAllNotificationsFromSync() {
        String query = "DELETE FROM tb_sync WHERE nm_table = 'tb_notice'";
        try {
            db.execSQL(query);
        } catch (Exception e) {
            Log.d(tag, "Nao deletou da Notificações da tb_sync");
        }
    }

    public void deleteTBUserFromSync() {
        String query = "DELETE FROM tb_sync WHERE nm_table = 'tb_user'";
        try {
            db.execSQL(query);
        } catch (Exception e) {
            Log.d(tag, "Nao deletou a TB_USER da tb_sync");
        }
    }

    public void deleteCommentsFromTBSync(LinkedList<HolderIDS> holderIDS) {
        StringBuilder sb = new StringBuilder("DELETE FROM tb_sync where nm_table = 'tb_comment' AND id_sync in (");
        for (HolderIDS id : holderIDS) {
            if (id == holderIDS.getLast()) {
                sb.append("" + id.getId() + " );");
                break;
            } else {
                sb.append("" + id.getId() + " , ");
            }
        }
        Log.d(tag + " DELETE tb_sync", " query:" + sb.toString());
        String query = sb.toString();

        try {
            db.execSQL(query);
        } catch (Exception e) {
            Log.d(tag, "Nao deletou da tb_sync");
        }
    }

    public void cleanDataBase() {
        context.deleteDatabase("db_portfolio_alpha_atual.sqlite");
    }

    public LinkedHashMap<Integer, LinkedList<Attachment>> getAttachments(LinkedList<Integer> integers) {
        LinkedHashMap<Integer, LinkedList<Attachment>> aux = new LinkedHashMap<>();

        for (Integer id : integers) {
            String query = "SELECT id_attachment, id_activity_student FROM tb_attach_activity WHERE id_attach_activity = " + id;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                int idAttachment = c.getInt(0);
                int idActivityStudent = c.getInt(1);

                if (!aux.containsKey(idActivityStudent))
                    aux.put(idActivityStudent, new LinkedList<Attachment>());

                String queryAttachment = "SELECT * FROM tb_attachment WHERE id_attachment = " + idAttachment;
                Cursor cAttachment = db.rawQuery(queryAttachment, null);

                if (cAttachment.moveToFirst())
                    aux.get(idActivityStudent).add(cursorToAttachment(cAttachment));
            }
        }

        return aux;
    }

    public HashMap<String, String> getAllAttachmentsNames(int idActivityStudent) {

        HashMap<String, String> aux = new HashMap<>();

        String query = "SELECT tba.nm_system FROM tb_attachment as tba " +
                "JOIN tb_attach_activity as tbaa on tbaa.id_attachment = tba.id_attachment " +
                "WHERE tbaa.id_activity_student = " + idActivityStudent;
        Cursor c = db.rawQuery(query, null);

        if (c.getCount()>0) {
            while (c.moveToNext()) {
                String nmSystem = c.getString(0);
                String key = "";
                String[] a = nmSystem.split("/");
                if (a.length > 0)
                    key = a[a.length - 1];

                if (!aux.containsKey(key))
                    aux.put(key, nmSystem);
            }
        }

        return aux;

    }
    public void updateIdAttachmentSrvById(int idAttachment, int idAttachmentSrv) {
        ContentValues cv = new ContentValues();
        cv.put("id_attachment_srv", idAttachmentSrv);
        try {
            db.update("tb_attachment", cv, "id_attachment = " + idAttachment, null);
            Log.e(tag, "Conseguiu alterar tb_attachment id_attachment_srv");
        } catch (Exception e) {
            Log.e(tag, "Erro ao alterar tb_attachment id_attachment_srv");
        }
    }

    */
/*
        ************************* CRUD POLICY ***************************
    *//*


    public void insertTBPolicy(List<Policy> policies) {
        for (Policy p : policies) {
            ContentValues cv = new ContentValues();
            cv.put("id_policy", p.getIdPolicy());
            cv.put("tx_policy", p.getTxPolicy());
            try {
                db.insert("tb_policy", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir tb_policy:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void insertTBPolicyUser(List<PolicyUser> policyUsers) {
        for (PolicyUser p : policyUsers) {
            ContentValues cv = new ContentValues();
            cv.put("id_policy_user", p.getIdPolicyUser());
            cv.put("id_policy", p.getIdPolicy());
            cv.put("id_user", p.getIdUser());
            cv.put("fl_accept", p.getFlAccept());
            try {
                db.insert("tb_policy_user", null, cv);

            } catch (Exception e) {
                Log.d(tag, "erro ao inserir tb_policy_user:" + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void updateFlAccept(int idPolicyUser) {
        ContentValues cv = new ContentValues();
        cv.put("fl_accept", "S");
        try {
            db.update("tb_policy_user", cv, "id_policy_user = " + idPolicyUser, null);
            Log.e(tag, "Conseguiu alterar tb_policy_user fl_accept");
        } catch (Exception e) {
            Log.e(tag, "Erro ao alterar tb_policy_user fl_accept");
        }
    }

    public Policy getPolicy() {
        Policy policy = new Policy(0, null);

        String query = "SELECT id_policy FROM tb_policy";
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            int id_policy = c.getInt(0);

            query = "SELECT tx_policy FROM tb_policy WHERE id_policy = " + id_policy;
            c = db.rawQuery(query, null);

            if (c.moveToFirst()) {
                policy = new Policy(id_policy, c.getString(0));
            }
        }

        return policy;
    }
    public PolicyUser getPolicyUserByUserId(int idUser) {
        PolicyUser policyUser = new PolicyUser(0, 0, 0, null);

        String query = "SELECT id_policy_user FROM tb_policy_user";
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            int id_policy_user = c.getInt(0);

            query = "SELECT * FROM tb_policy_user WHERE id_user = " + idUser;
            c = db.rawQuery(query, null);

            if (c.moveToFirst()) {
                policyUser = new PolicyUser(id_policy_user, c.getInt(1), c.getInt(2), c.getString(3));
            }
        }

        return policyUser;
    }
    public Policy getPolicyByUserID(int idUser) {//talvez seja melhor fazer lista, pensar nisso
        Policy policy = new Policy(0, null);

        String query = "SELECT id_policy_user FROM tb_policy_user";
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            int id_policy_user = c.getInt(0);

            query = "SELECT tx_policy FROM tb_policy INNER JOIN tb_policy_user ON tb_policy.id_policy = tb_policy_user.id_policy WHERE tb_policy_user.id_user = " + idUser;
            c = db.rawQuery(query, null);

            if (c.moveToFirst()) {
                policy = new Policy(id_policy_user, c.getString(0));
            }
        }

        return policy;
    }


*/

