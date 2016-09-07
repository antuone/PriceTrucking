package antuone.pricetrucking;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import antuone.pricetrucking.ActivityMain.City;

/**
 * Created by Anton Likhachev asikuo@gmail.com on 02.09.16.
 */
public class AdapterC extends ArrayAdapter<City> {

    List<City> data;

    public AdapterC(Context context, List<City> data) {
        super(context, R.layout.support_simple_spinner_dropdown_item, data);
        this.data = data;
    }

    public void findCities(String _s) {

        URL url = null;
        HttpURLConnection urlConnection = null;
        try {


            url = new URL("http://perm.dostavkagruzov.com/apitest/api.php?service=city&s=" + _s);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            JSONParser parser = new JSONParser();
            String json = IOUtils.toString(in);

            JSONArray jsonArray = (JSONArray) parser.parse(json);
            data.clear();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = (JSONObject)jsonArray.get(i);
                data.add(new City((String) obj.get("fullName"), (String) obj.get("kladr")));
            }

            //Toast.makeText(getContext(), ((City)data.get(data.size()-1)).name, Toast.LENGTH_LONG).show();
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    //data = (List<City>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    findCities(constraint.toString());
                    filterResults.values = data;
                    filterResults.count = data.size();
                }

                return filterResults;

            }
        };
        return filter;
    }
}
