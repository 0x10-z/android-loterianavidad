package com.ocioz.loterianavidad;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.ocioz.loterianavidad.R;
import com.searchboxsdk.android.StartAppSearch;
import com.startapp.android.publish.StartAppAd;

import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	final Context context = this;
	private ArrayList<Boleto> listaBoletosActuales;
	SharedPreferences sharedPref;
	Button btnAdd;
	Button btnCon;
	ListView lvNumberList;
	TextView tvNumbLoteria;
	TextView tvAposLoteria;
	Json json = new Json();
	final int TAMANO_LISTA = 20;
	Editor editorPrefs = null;
	final String LOG_TAG = "LOG_ERROR";
	boolean isOnline = false;
	private Boleto boletoAdd;
	private Boleto boletoRemove;
	private ArrayList<Boleto> numberListArray;
	private ArrayAdapter<String> todoItemsAdapter;
	private StartAppAd startAppAd = new StartAppAd(this);
	private ConsultarEstado estado;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StartAppAd.init(this, "112386200", "212313501");
		StartAppSearch.init(this, "112386200", "212313501");
		listaBoletosActuales = new ArrayList<Boleto>();
		setContentView(R.layout.activity_main);
		StartAppSearch.showSearchBox(this);
		estado = new ConsultarEstado();
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnCon = (Button) findViewById(R.id.btnAtras);
		lvNumberList = (ListView) findViewById(R.id.lstItems);
		tvNumbLoteria = (TextView) findViewById(R.id.txtTareaNumero);
		tvAposLoteria = (TextView) findViewById(R.id.txtTareaApostado);

		if (isNetworkAvailable()) {
			int duration = Toast.LENGTH_LONG;
			estado.execute();
			String[] es = null;
			try {
				es = estado.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Toast toast = Toast.makeText(context, es[0], duration);
			toast.show();
		}
		// Lista donde almacenaremos los premios y el Adapter
		numberListArray = getNumerosGuardados();
		// todoItemsAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1);
		todoItemsAdapter = new ArrayAdapter<String>(this, R.layout.custom_list);

		// Rellenamos el adapter
		actualizarAdapter();
		Log.d(LOG_TAG,
				"Las pref. guardadas tienen un numero de "
						+ String.valueOf(sharedPref.getAll().size()));
		lvNumberList.setAdapter(todoItemsAdapter);
		editorPrefs = sharedPref.edit();

		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String numero = "";
				for (int i = 0; i < tvNumbLoteria.getText().toString().length(); i++) {
					if (!(tvNumbLoteria.getText().toString().charAt(i) == '0')) {
						numero = numero
								+ tvNumbLoteria.getText().toString().charAt(i);
					}
				}
				boletoAdd = new Boleto(numero, tvAposLoteria.getText()
						.toString());

				// Solo recoge los numeros si la longitud del boleto es 5 y el
				// dinero no es 0
				if (Integer.parseInt(boletoAdd.getNumero()) > 0
						&& Integer.parseInt(boletoAdd.getNumero()) < 84999
						&& boletoAdd.getApostado().length() != 0) {

					if (boletoAdd.getNumero().charAt(0) == '0') {

					}
					editorPrefs.putString(boletoAdd.getNumero(),
							boletoAdd.getApostado());
					editorPrefs.commit();

					Log.d(LOG_TAG, "Las preferencias tienen un tamaño de: "
							+ String.valueOf(sharedPref.getAll().size()));
					// Añadimos el boleto al adapter
					numberListArray.add(boletoAdd);
					actualizarAdapter();
					todoItemsAdapter.notifyDataSetChanged();

					tvNumbLoteria.setText("");
					tvAposLoteria.setText("");
					// hide keyboard
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(tvNumbLoteria.getWindowToken(),
							0);
				} else {
					// Pop up de que falta anadir el numero de loteria o lo
					// apostado
					AlertDialog.Builder dialogo1 = new AlertDialog.Builder(
							context);
					dialogo1.setTitle("Importante");
					dialogo1.setMessage("No ha introducido un boleto de 5 numeros o una apuesta permitida");
					dialogo1.setCancelable(false);
					dialogo1.setPositiveButton("Confirmar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialogo1,
										int id) {

								}
							});
					dialogo1.show();
				}
			}
		});

		btnCon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int varControlListaNoVacia = 0;
				Context context = getApplicationContext();
				boolean estaVacia = false;

				if (isNetworkAvailable()) {
					Intent myIntent = myIntent = new Intent(MainActivity.this,
							ConsultActivity.class);
					for (int i = 0; i < numberListArray.size(); i++) {
						if (!numberListArray.get(i).equals("")) {
							Log.e("LOG_ERROR_137",
									"ha entrando dentro del for y del if");

							varControlListaNoVacia = 1;
							myIntent.putExtra(numberListArray.get(i)
									.getNumero(), numberListArray.get(i)
									.getApostado());
						} else {
							// el boleto esta vacio
						}
					}
					if (varControlListaNoVacia != 0) {
						Log.e("LOG_ERROR_151", "Iniciando la activity");
						MainActivity.this.startActivityForResult(myIntent, 0);
					} else {

					}

				}
			}
		});
		lvNumberList.setDividerHeight(3);
		lvNumberList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					final int index, long arg3) {

				if (!lvNumberList.getItemAtPosition(index).equals("")) {
					String pos = lvNumberList.getItemAtPosition(index)
							.toString();
					boletoRemove = new Boleto(numberListArray.get(index)
							.getNumero(), numberListArray.get(index)
							.getApostado());

					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								// Yes button clicked
								editorPrefs.remove(boletoRemove.getNumero());
								editorPrefs.commit();
								numberListArray.remove(index);
								actualizarAdapter();
								todoItemsAdapter.notifyDataSetChanged();

							case DialogInterface.BUTTON_NEGATIVE:
								// No button clicked
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage(
							"¿Estas seguro de que deseas eliminar el elemento?")
							.setPositiveButton("Si", dialogClickListener)
							.setNegativeButton("No", dialogClickListener)
							.show();

					Log.v("long clicked", "pos: " + pos);

					// TODO Auto-generated method stub
				} else {
					// si esta vacio el boleto no hace nada
				}
				return false;
			}
		});

	}

	private void actualizarAdapter() {
		// TODO Auto-generated method stub
		todoItemsAdapter.clear();
		for (Boleto boleto : numberListArray) {
			todoItemsAdapter.add(boleto.getNumero() + " ("
					+ boleto.getApostado() + "€)");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_resumen:
			// About option clicked.
			Intent intent = new Intent(MainActivity.this, Resumen.class);
			startActivity(intent);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public ArrayList<Boleto> getNumerosGuardados() {
		ArrayList<Boleto> listaNumeros = new ArrayList<Boleto>();
		sharedPref = getSharedPreferences("Boletos", Context.MODE_PRIVATE);
		Map<String, ?> keys = sharedPref.getAll();

		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			Log.d("map values", entry.getKey() + ": "
					+ entry.getValue().toString());
			listaNumeros.add(new Boleto(entry.getKey(), entry.getValue()
					.toString()));
		}

		return listaNumeros;
	}

	public void updatePrefKey() {
		editorPrefs.clear();
		editorPrefs.commit();

	}

	public boolean prefAreEmpty() {
		return this.getPreferences(Context.MODE_PRIVATE).getAll().isEmpty();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isNetworkAvailable()) {
			isOnline = true;
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nf = cn.getActiveNetworkInfo();
		if (nf != null && nf.isConnected() == true) {
			return true;
		} else {
			Toast.makeText(this, "Network Not Available", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
	}

	@Override
	public void onBackPressed() {
		startAppAd.onBackPressed();
		super.onBackPressed();
	}

	@Override
	public void onPause() {
		super.onPause();
		startAppAd.onPause();
	}

}
