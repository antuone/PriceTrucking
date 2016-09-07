package antuone.pricetrucking;

import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Anton Likhachev asikuo@gmail.com on 02.09.16.
 */

public class ActivityMain extends AppCompatActivity {
    EditText weight;
    EditText volume;
    City cityFrom;
    City cityIn;

    String result = "";
    TextView tw;
    public static class City {
        public String name;
        public String kladr;
        City(String name, String kladr) {
            this.name = name;
            this.kladr = kladr;
        }
        @Override
        public String toString () {
            return name;
        }
    }

    class buttonOnClickListener implements View.OnClickListener {
        buttonOnClickListener() {
        }
        public void onClick (View v){
            //askingPrice();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (cityFrom == null || cityIn == null ||
                            weight.getText().length() < 1 ||
                            volume.getText().length() < 1) {
                        Toast.makeText(ActivityMain.this, R.string.warning, Toast.LENGTH_LONG).show();
                        return;
                    }

                    URL url = null;
                    HttpURLConnection urlConnection = null;

                    try {
                        String url_string =
                                "http://dostavkagruzov.com/api/api.php?service=calculatorSimple"
                                        + "&from=" + cityFrom.kladr
                                        + "&to=" + cityIn.kladr
                                        + "&cityFrom=" + cityFrom.name
                                        + "&cityTo=" + cityIn.name
                                        + "&weight=" + weight.getText().toString()
                                        + "&volume=" + volume.getText().toString();

                        url = new URL(url_string);

                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                        JSONParser parser = new JSONParser();
                        JSONObject obj = (JSONObject) parser.parse(IOUtils.toString(in));
                        JSONObject obj2 = (JSONObject) obj.get("data");
                        JSONObject obj3 = (JSONObject) obj2.get("calculator");

                        tw.setText((String) obj3.get("sum") + " \u20BD");

                        urlConnection.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        tw.setText("Сервер не дал резултата");
                    }

                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }, 0);
        }
    }

    private String askingPrice() {

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {


            }
        });
        th.start();
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tw = (TextView)findViewById(R.id.textView4);
        weight = (EditText) findViewById(R.id.editText2);
        volume = (EditText) findViewById(R.id.editText3);


        final AdapterC adapter = new AdapterC(this, new ArrayList<City>(50));
        final AdapterC adapter2 = new AdapterC(this, new ArrayList<City>(50));

        AutoCompleteTextView autoComplete = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        autoComplete.setAdapter(adapter);
        autoComplete.setThreshold(4);
        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityFrom = adapter.getItem(position);
                //Toast.makeText(getApplicationContext(), cityFrom.name, Toast.LENGTH_LONG).show();
            }
        });

        AutoCompleteTextView autoComplete2 = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView2);
        autoComplete2.setAdapter(adapter2);
        autoComplete2.setThreshold(4);
        autoComplete2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityIn = adapter2.getItem(position);
            }
        });

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new buttonOnClickListener());

    }


}
