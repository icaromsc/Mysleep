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

/**
 * Created by icaromsc on 13/02/2017.
 */

public class LoginClient extends HttpClient{

    public LoginClient(Context context) {
        super(context);
    }


    public void postJson(JSONObject jsonBody){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL + "login_app.php", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("result").equals(0)){
                        Log.e("Erro", "JSON Post erro");
                    }else {
                        Log.e("Response JSON", "JSON Post com sucesso:"+response.getString("result")+ response.toString());
                        DataBaseAdapter data = DataBaseAdapter.getInstance(context);
                        //data.updateStatusPrimMesSync(1);
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
            }
        });
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }

}
