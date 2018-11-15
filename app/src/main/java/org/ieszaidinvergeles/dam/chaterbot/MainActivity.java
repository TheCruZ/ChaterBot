package org.ieszaidinvergeles.dam.chaterbot;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.ieszaidinvergeles.dam.chaterbot.HTTP.HTTP;
import org.ieszaidinvergeles.dam.chaterbot.HTTP.HTTPR;
import org.ieszaidinvergeles.dam.chaterbot.SQLite.Manager;
import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBot;
import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBotFactory;
import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBotSession;
import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBotType;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

//https://github.com/pierredavidbelanger/chatter-bot-api

public class MainActivity extends AppCompatActivity {

    private Button btSend;
    private EditText etTexto;
    private ScrollView svScroll;
    private TextView tvTexto;

    private ChatterBot bot;
    private ChatterBotFactory factory;
    private ChatterBotSession botSession;
    private Manager m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        m = new Manager(this);

        List<Message> msgs= m.getLastMessages();
        for(Message m : msgs){
            addMessage(m);
        }
        Log.v("TAGG","m:"+msgs.size());
        if(msgs.size()>0)
            addMessage(new Message("system","Last messages restored",System.currentTimeMillis()));

        if (startBot()) {
            setEvents();
        }
    }

    private void init() {
        btSend = findViewById(R.id.btSend);
        etTexto = findViewById(R.id.etTexto);
        svScroll = findViewById(R.id.svScroll);
        tvTexto = findViewById(R.id.tvTexto);

    }

    private void setEvents() {
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message me = new Message("you", etTexto.getText().toString().trim(), System.currentTimeMillis());
                btSend.setEnabled(false);
                etTexto.setText("");
                addMessage(me);
                new botResponse().execute(me);
            }
        });
    }

    class botResponse extends AsyncTask<Message, Message, Message> {

        @Override
        protected Message doInBackground(Message... men) {
            m.insert(men[0]);
            Message response;
            try {
                response = new Message("bot",translate( botSession.think(men[0].getFrom()+"> "+translate(men[0].getMessage(),"es","en")),"en","es"), System.currentTimeMillis());
                m.insert(response);
            } catch (final Exception e) {
                response = new Message(getString(R.string.exception), e.toString(), System.currentTimeMillis());
            }
            return response;
        }

        @Override
        protected void onPostExecute(Message mensaje) {
            addMessage(mensaje);
            btSend.setEnabled(true);
            hideKeyboard();
        }
    }

    private void addMessage(Message m) {
        etTexto.requestFocus();
        tvTexto.append(m.toString() + "\n");
        svScroll.fullScroll(View.FOCUS_DOWN);
    }

    private boolean startBot() {
        boolean result = true;
        String initialMessage;
        factory = new ChatterBotFactory();
        try {
            bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            botSession = bot.createSession();
            initialMessage = getString(R.string.messageConnected) + "\n";
        } catch (Exception e) {
            initialMessage = getString(R.string.messageException) + "\n" + getString(R.string.exception) + " " + e.toString();
            result = false;
        }
        addMessage(new Message("system","bot "+initialMessage,System.currentTimeMillis()));
        return result;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String translate(String text, String from, String to){
        HTTPR R = HTTP.postHtml("https://www.bing.com/ttranslate","&text="+text+"&from="+from+"&to="+to,HTTP.CONTENT_TYPE_URLENCODED);
        JSONObject reader = null;
        try {
            reader = new JSONObject(R.getContent());
            return reader.getString("translationResponse");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Error";
    }


}