package com.ocioz.loterianavidad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class ConsultarEstado extends AsyncTask<Void, Integer, String[]> {

	
	public static String getEstado(JSONObject jsonLoteria) {
		String estado;
		try {
			if (!jsonLoteria.toString().equals("{\"error\":1}")) {
				estado = jsonLoteria.get("status").toString();
				if(estado.equals("0")){
					return "El sorteo no ha comenzado aun\nTodas las consultas que se hagan de los numeros actuales no tendran validez";
				}else if(estado.equals("1")){
					return "El sorteo ha empezado. La lista de numeros se ira cargando poco a poco";
				}else if(estado.equals("2")){
					return "El sorteo ha terminado y la lista de numeros y premios se esta comprobando";
				}else if(estado.equals("3")){
					return "El sorteo ha terminado y existe lista oficial";
				}else if(estado.equals("4")){
					return "El sorteo ha terminado y existe lista oficial publicada por ONLAE";
				}
			} else {
				return "Error";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("LOG_EXCEPTION", "JSONException - getPremioGanado");
			e.printStackTrace();
		}
		return "Error 2";

	}

	@Override
	protected String[] doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		
		
		InputStream is = null;
		JSONObject jsonObject = null;
		HttpGet httpget;
		String str_final_url = "http://api.elpais.com/ws/LoteriaNavidadPremiados?s=1";
		String result = null;

		// HTTP
		try {
			HttpClient httpclient = new DefaultHttpClient(); // for port 80
			Log.d("LOG", str_final_url);
			// requests!
			httpget = new HttpGet(str_final_url);
			HttpResponse response = httpclient.execute(httpget); // HERE APP
																	// CRASH
			HttpEntity entity = response.getEntity();

			is = entity.getContent();

		} catch (IOException e) {

			Log.d("LOG_EXCEPTION", "IOException");
		} catch (IllegalArgumentException e) {

			Log.d("LOG_EXCEPTION", "URL no valida");
		} catch (Exception e) {
			// return null;
			Log.d("LOG_EXCEPTION", "Exception", e);
		}

		// Read response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.d("LOG RESULT", result);
		} catch (Exception e) {
			Log.d("LOG_EXCEPTION", "Reader response to string");
			// return null;
		}
		result = result.replaceAll("info=", "");
		// Convert string to object
		try {
			jsonObject = new JSONObject(result);

		} catch (JSONException e) {
			// return null;
		}
		
		return new String[] {getEstado(jsonObject)};
	}

}
