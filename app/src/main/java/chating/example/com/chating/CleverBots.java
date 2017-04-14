package chating.example.com.chating;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

class CleverBot extends AsyncTask<String,String,String> {

    BufferedReader reader;
    String temp_key;
    DatabaseReference root;
    String output="Sorry!!\nI dont understand your language";
    String message,api,cleverbot,msg;

    public CleverBot(String message){
        this.message=message;
    }

    @Override
    public String doInBackground(String[] params) {
        msg = message.replaceAll(" ","%20");
        api= "https://www.cleverbot.com/getreply?key=CC1p9KFsGrl6ioNTftAGoU1eN-w&input=";
        cleverbot=api+msg;

        try {
            URL url = new URL(cleverbot);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream stream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            int code = urlConnection.getResponseCode();
            Log.d("res", Integer.toString(code));

            String finalJson = buffer.toString();

            JSONObject parent_object = new JSONObject(finalJson);
            String results_array = parent_object.getString("output");

            output=results_array;
            Log.d("res", results_array);
            urlConnection.disconnect();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
        return output;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        root = FirebaseDatabase.getInstance().getReference().child("CleverBot");
        Map<String, Object> map = new HashMap<String, Object>();
        temp_key = root.push().getKey();
        root.updateChildren(map);

        DatabaseReference msg_root = root.child(temp_key);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("name", "CleverBot");
        map2.put("msg", result);
        msg_root.updateChildren(map2);
    }
}