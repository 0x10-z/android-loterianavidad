package com.ocioz.loterianavidad;


import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import com.ocioz.loterianavidad.R;
import com.startapp.android.publish.StartAppAd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConsultActivity extends Activity {

	Button atrButton;
	final int TAMANO_LISTA = 20;
	ListView lvListaNumero;
	TextView txtNumeroLoteria;
	TextView txtApostado;
	Json json;
	String dineroGanado = "";
	String timestamp = "";
	CharSequence textTime = "";
	private StartAppAd startAppAd = new StartAppAd(this);
	private SharedPreferences sharedPref;
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consulta_activity);

		atrButton = (Button) findViewById(R.id.btnAtras);
		lvListaNumero = (ListView) findViewById(R.id.listItemConsultados);
		Log.e("Log_Con_47", "Vista creada correctamente");

		// Lista donde almacenaremos los premios y el Adapter
		final ArrayList<Boleto> numberListArray = getNumerosGuardados();
		final ArrayAdapter<String> todoItemsAdapter = new ArrayAdapter<String>(
				this, R.layout.custom_list);
		String[] res = null;
		for (Boleto boleto : numberListArray) {
			json = new Json();
			json.execute("0", boleto.getNumero(), boleto.getApostado());
			try {
				res = json.get();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				Log.e("LOG_ASYNC_CON", "ExecutionException", e);
				e.printStackTrace();
			}
			// timestamp = json.getTimeStamp("0", boleto.getNumero(),
			// boleto.getApostado());
			// dineroGanado = json.getPremioGanado("0", boleto.getNumero(),
			// boleto.getApostado());
			timestamp = getTimestampDate(res[1]);
			todoItemsAdapter.add(boleto.getNumero() + " - Premio: " + res[0]);
		}
		todoItemsAdapter.notifyDataSetChanged();
		lvListaNumero.setAdapter(todoItemsAdapter);
		textTime = "Numeros consultados a fecha de: " + timestamp;
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(this, textTime, duration);
		toast.show();

		atrButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				// myIntent.putExtra("key", value); //Optional parameters
				setResult(RESULT_OK, intent);
				finish();
				// try {
				// for (int i = 0; i <
				// jsonArray.length(); i++)
				// {
				// JSONObject jsonObject = jsonArray.getJSONObject(i);
				// prem = jsonObject.getString("premio");
				// }
				// } catch (JSONException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// Toast toast2 = Toast.makeText(context, prem, duration);
				// toast2.show();

			}
		});

	}

	public ArrayList<Boleto> getNumerosGuardados() {
		ArrayList<Boleto> listaNumeros = new ArrayList<Boleto>();
		sharedPref = getSharedPreferences("Boletos", Context.MODE_PRIVATE);
		Log.e("LOG_107_con", String.valueOf(sharedPref.getAll().size()));
		Map<String, ?> keys = sharedPref.getAll();

		for (Map.Entry<String, ?> entry : keys.entrySet()) {

			Log.d("map values", entry.getKey() + ": "
					+ entry.getValue().toString());
			listaNumeros.add(new Boleto(entry.getKey(), entry.getValue()
					.toString()));
		}

		return listaNumeros;
	}

	public String getTimestampDate(String pTimestamp) {
		if (pTimestamp.equals("error:1")) {
			return "Error:1";
		} else {
			Timestamp stamp = new Timestamp(Long.valueOf(pTimestamp) * 1000L);
			Date date = new Date(stamp.getTime());
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
					"dd/MM/yyyy");
			String fecha = sdf.format(date);
			return fecha;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		startAppAd.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		startAppAd.onPause();
	}
}
