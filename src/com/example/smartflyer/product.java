package com.example.smartflyer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class product extends ListActivity {
	
	JSONParser jParser = new JSONParser();
    
    ArrayList<HashMap<String, String>> productsList;
    
    private static String url_all_products = "http://smart-flyer.valexandru.com/android_connect/get_company_details.php";
    
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "produse";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "nume";
    
    JSONArray products = null;
    String pid;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);
        
        
        
        productsList = new ArrayList<HashMap<String, String>>();
        
        new LoadAllProducts().execute();
        
        ListView lv = this.getListView();
        
        Intent i = getIntent();
        
        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_PID);
        
    }
    
    class LoadAllProducts extends AsyncTask<String, String, String> {
    	 
        /**
         * Before starting background thread Show Progress Dialog
         * */
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", pid));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
 
            // Check your log cat for JSON reponse
            
            
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);
                    
                    Log.d("All: ", json.toString());
                    
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
                        
                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);
 
                        // adding HashList to ArrayList
                        productsList.add(map);
                    }
                } else {
                	/*
                    // no products found
                    // Launch Add New product Activity
                    
                	Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        
        protected void onPostExecute(String file_url) {
            
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                	
                    ListAdapter adapter = new SimpleAdapter(
                            product.this, productsList,
                            R.layout.list_item, new String[] { TAG_PID,
                                    TAG_NAME},
                            new int[] { R.id.pid, R.id.name });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }
}
