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

public class Json extends AsyncTask<String, Integer, String[]> {

	// sorteoLoteria[0] -> Loteria Navidad y sorteoLoteria[1] -> Loteria nino
	final static String[] sorteoLoteria = new String[] {
			"http://api.elpais.com/ws/LoteriaNavidadPremiados?n=",
			"http://api.elpais.com/ws/LoteriaNinoPremiados?n=" };

	
	public static String getPremio(JSONObject jsonLoteria,
			String pDineroApostado) {
		DecimalFormat df = new DecimalFormat("0.00");
		String premioGanado = null;
		double dineroGanado = 0.0;
		try {
			if (!jsonLoteria.toString().equals("{\"error\":1}")) {
				premioGanado = jsonLoteria.get("premio").toString();
				dineroGanado = Double.parseDouble(pDineroApostado)
						* Double.parseDouble(premioGanado) / 20.0;
				return df.format(dineroGanado);
			} else {
				premioGanado = "Error de peticion al servidor";
				return "0,00";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("LOG_EXCEPTION", "JSONException - getPremioGanado");
			e.printStackTrace();
		}
		return "0,00";

	}

	/**
	 * @int 0 -> LoteriaNavidad 1 -> LoteriaNino
	 */
	public static String getTimeStamp(JSONObject jsonLoteria) {
		String timestamp = "";
		Log.d("LOG_getTimeStamp", jsonLoteria.toString());
		try {
			if (!jsonLoteria.toString().equals("{\"error\":1}")) {
				timestamp = jsonLoteria.get("timestamp").toString();
			} else {
				timestamp = "error:1";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("LOG_EXCEPTION", "JSONException - getPremioGanado");
			e.printStackTrace();
		}
		return timestamp;
	}

	@Override
	protected String[] doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		String pTipoLoteria = arg0[0];
		String pNumeroPremiado = arg0[1];
		String pDineroApostado = arg0[2];

		int tipoLoteria = 0;
		if (pTipoLoteria.equals("0")) {
			tipoLoteria = 0;
		} else if (pTipoLoteria.equals("1")) {
			tipoLoteria = 1;
		}
		String encodeParamUrl = null;
		InputStream is = null;
		String result = "";
		JSONObject jsonObject = null;
		HttpGet httpget;
		String str_final_url = null;

		// Encode URL PARAMETERS. If not, HttpGet will crash
		try {
			String param_url = pNumeroPremiado;
			encodeParamUrl = URLEncoder.encode(param_url, "UTF-8");
			str_final_url = sorteoLoteria[tipoLoteria] + encodeParamUrl;

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			Log.d("LOG_EXCEPTION", "Unsuported Encoding Exception");
			e1.printStackTrace();
		}
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
		result = result.replaceAll("busqueda=", "");
		// Convert string to object
		try {
			jsonObject = new JSONObject(result);

		} catch (JSONException e) {
			// return null;
		}
		
		return new String[] { getPremio(jsonObject, pDineroApostado),
				getTimeStamp(jsonObject) };
	}

}
