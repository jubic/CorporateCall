package sg.edu.rp.c345.a02.corporatecall;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddContact extends Activity {

	// Variables
	DbAdapter dbAdapter;
	ContactsAdapter cAdapter;
	ArrayList<Contacts> contactList;
	EditText nameET, funcET, compET, numbET;
	Button submitBtn;
	Intent returnToMain;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contact);

		setElements();

		submitBtn.setOnClickListener(saveItemClick);
	}

	private void setElements() {
		// TODO Auto-generated method stub
		contactList = new ArrayList<Contacts>();
		dbAdapter = new DbAdapter(this);
		cAdapter = new ContactsAdapter(this, contactList);

		nameET = (EditText) findViewById(R.id.nameET);
		funcET = (EditText) findViewById(R.id.funcET);
		compET = (EditText) findViewById(R.id.compET);
		numbET = (EditText) findViewById(R.id.numbET);

		submitBtn = (Button) findViewById(R.id.addBtn);
	}

	private OnClickListener saveItemClick = new OnClickListener() {
		public void onClick(View v) {
			insertContact();
		}
	};

	private void insertContact() {
		// Get EditText & Read the value inside
		String name = nameET.getText().toString();
		String func = funcET.getText().toString();
		String comp = compET.getText().toString();
		String numb = numbET.getText().toString();

		// Don't proceed if EditText is empty
		if (name.compareTo("") == 0 || func.compareTo("") == 0
				|| comp.compareTo("") == 0 || numb.compareTo("") == 0) {
			return;
		}

		AddContactIntoXML(name, func, comp, numb);
		cAdapter.notifyDataSetChanged();
		dbAdapter.open();
		dbAdapter.insertContact(name, func, comp, numb);
		dbAdapter.close();

		Intent returnToMain = new Intent(getBaseContext(), CorporateCall.class);
		returnToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(returnToMain);

	}

	private void AddContactIntoXML(String _name, String _func, String _comp,
			String _numb) {
		// Get the XML
		URL url;

		try {

			String encodedName = URLEncoder.encode(_name);
			String encodedFunc = URLEncoder.encode(_func);
			String encodedComp = URLEncoder.encode(_comp);
			String encodedNumb = URLEncoder.encode(_numb);

			// Get the city name and construct the full URL to get the XML file
			String StringUrl = "http://sit.rp.edu.sg/c345/submitcontact.php?apikey=e805edb9bd95c94fbc4af49d996556bca334d983&name="
					+ encodedName
					+ "&function="
					+ encodedFunc
					+ "&company="
					+ encodedComp + "&phone=" + encodedNumb;

			// Log.d("URL encoded", encodedCityName);
			url = new URL(StringUrl);

			URLConnection connection;
			connection = url.openConnection();

			// Starts a HTTP connection
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();

			// Standard response for successful HTTP requests
			if (responseCode == HttpURLConnection.HTTP_OK) {

				Log.d("Contact inserted into web service", encodedName);
				Contacts contactObj = new Contacts(0, encodedName, encodedFunc,
						encodedComp, encodedNumb);
				contactList.add(contactObj);

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}