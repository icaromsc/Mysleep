package br.edu.ufcspa.snorlax_angelo.client;

import android.content.Context;

/**
 * Created by icaromsc on 13/02/2017.
 */

public class HttpClient {
    protected Context context;
    protected static final String URL="http://angelo.inf.ufrgs.br/snorlax/";

    public HttpClient(Context context){
        this.context=context;
    }
}
