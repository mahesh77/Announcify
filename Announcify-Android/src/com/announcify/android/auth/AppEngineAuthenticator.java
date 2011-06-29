package com.announcify.android.auth;

import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.announcify.android.engine.EngineWrapper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;

public class AppEngineAuthenticator extends Authenticator {

    final static String BASE_URL = EngineWrapper.BASE_URL;
    final static String AUTH_URL = BASE_URL + "_ah/login";

    final static String TYPE = "ah";
    
    
    AuthenticationCallback callback;


    public AppEngineAuthenticator(Context context, AccountManager manager, Account account) {
        super(context, manager, account, Authenticator.getPreferences(context, TYPE));
    }


    @Override
    public void doGenerate(AuthenticationCallback callback) {
        generateToken(account, TYPE, callback);
    }
    
    @Override
    public void doSomething(AuthenticationCallback callback) {
        this.callback = callback;
        
        new CookieTask().execute(getToken());
    }
    
    public void signRequest(HttpPost request) {
        request.setHeader("Cookie", getToken());
    }
    

    private class CookieTask extends AsyncTask<String, Object, String> {

        protected String doInBackground(String... tokens) {
            String token = tokens[0];
            String cookie = null;

            try {
                DefaultHttpClient client = new DefaultHttpClient();

                URI uri = new URI(AUTH_URL + "?continue=" + URLEncoder.encode(BASE_URL, "UTF-8") + "&auth=" + token);
                HttpGet method = new HttpGet(uri);

                final HttpParams getParams = new BasicHttpParams();
                HttpClientParams.setRedirecting(getParams, false);
                method.setParams(getParams);

                HttpResponse response = client.execute(method);

                Header[] headers = response.getHeaders("Set-Cookie");
                if (response.getStatusLine().getStatusCode() != 302 || headers.length == 0) {
                    return null;
                }

                for (Header header : headers) {
                    if (header.getValue().indexOf("ACSID=") >= 0) {
                        String value = header.getValue();
                        String[] pairs = value.split(";");
                        cookie = pairs[0];
                    }
                }

                return cookie;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            if (result == null) {
                // TODO: random backoff

                return;
            } else {
                setToken(result);
                
                callback.run();
            }
        }
    }
}