package com.a3dx2.clock.service;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WebServiceCaller<K> extends AsyncTask<Void, Void, K> {

    private final static Logger LOGGER = Logger.getLogger("com.a3dx2.clock");
    private final static RestTemplate REST_TEMPLATE = new RestTemplate();

    static {
        REST_TEMPLATE.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    private String url;
    private Class<K> resultClass;
    private WebServiceResultHandler<K> resultHandler;

    public WebServiceCaller(String url, Class<K> theClass, WebServiceResultHandler<K> resultHandler) {
        this.url = url;
        this.resultClass = theClass;
        this.resultHandler = resultHandler;
    }

    @Override
    protected K doInBackground(Void... webServiceCalls) {
        try {
            return REST_TEMPLATE.getForObject(url, resultClass);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    protected void onPostExecute(K result) {
        resultHandler.handleResult(result);
    }

}
