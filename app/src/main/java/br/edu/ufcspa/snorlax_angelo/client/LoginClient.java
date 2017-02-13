package br.edu.ufcspa.snorlax_angelo.client;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ufcspa.snorlax_angelo.database.DataBaseAdapter;
import br.edu.ufcspa.snorlax_angelo.model.User;

/**
 * Created by icaromsc on 13/02/2017.
 */

public class LoginClient extends HttpClient{

    User user;

    public LoginClient(Context context, User u) {
        super(context);
        this.user=u;
    }


    public void postJson(JSONObject jsonBody){
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL + "login_app.php", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("result").equals(0)){
                        Log.e("Erro", "JSON Post erro");
                    }else {
                        Integer idServer=Integer.parseInt(response.getString("result"));
                        Log.e("Response JSON", "JSON Post com sucesso:"+idServer+ response.toString());
                        user.setIdUser(idServer);
                        DataBaseAdapter data = DataBaseAdapter.getInstance(context);
                        //data.updateStatusPrimMesSync(1);
                        data.insertUser(user);
                        Log.e("Response JSON", "JSON Post concluido");
                        data=null;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("erro:",volleyError.toString());
                volleyError.printStackTrace();

            }
        });
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }

    public void send(){
        Log.d("json","inicializando comunicacao...");
        Log.d("json","json a ser encaminhado:"+user.toJson().toString());
        postJson(user.toJson());
    }

}
