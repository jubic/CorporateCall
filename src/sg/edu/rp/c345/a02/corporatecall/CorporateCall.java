package sg.edu.rp.c345.a02.corporatecall;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class CorporateCall extends ListActivity {

	// Variables
	DbAdapter dbAdapter;
	ArrayList<Contacts> contactList = new ArrayList<Contacts>();
	ContactsAdapter cAdapter;
	ListView listView;

	// Menu
	static final private int ADD_CONTACT = Menu.FIRST;
	static final private int REFRESH_CONTACT = Menu.FIRST + 1;

	// Context Menu
	static final private int CALL_CONTACT = Menu.FIRST + 2;
	static final private int TEXT_CONTACT = Menu.FIRST + 3;
	static final private int VIEW_CONTACT = Menu.FIRST + 4;
	static final private int EDIT_CONTACT = Menu.FIRST + 5;
	static final private int DELETE_CONTACT = Menu.FIRST + 6;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setElements();

		getContacts();

	}

	private void setElements() {
		dbAdapter = new DbAdapter(this);

		contactList = new ArrayList<Contacts>();
		cAdapter = new ContactsAdapter(this, contactList);

		listView = getListView();
		listView.setAdapter(cAdapter);

		// register it for the context menu
		registerForContextMenu(listView);

		// Get contacts from web service
		GetContactFromXML();
	}

	private void getContacts() {
		ArrayList<Contacts> dbContacts = new ArrayList<Contacts>();

		dbAdapter.open();

		Cursor c = dbAdapter.getAllContacts();

		if (c.moveToFirst()) {
			do {
				int columnId = c.getColumnIndex(DbAdapter.KEY_CONTACT_ID);
				int columnName = c.getColumnIndex(DbAdapter.KEY_CONTACT_NAME);
				int columnFunction = c
						.getColumnIndex(DbAdapter.KEY_CONTACT_FUNCTION);
				int columnCompany = c
						.getColumnIndex(DbAdapter.KEY_CONTACT_COMPANY);
				int columnNumber = c
						.getColumnIndex(DbAdapter.KEY_CONTACT_PHONE);

				int id = c.getInt(columnId);
				String name = c.getString(columnName);
				String function = c.getString(columnFunction);
				String company = c.getString(columnCompany);
				String number = c.getString(columnNumber);

				dbContacts
						.add(new Contacts(id, name, function, company, number));

				Log.d("Contact inserted", name);
			} while (c.moveToNext());
		}
		c.close();
		dbAdapter.close();
		contactList.clear();
		contactList.addAll(dbContacts);
	}

	// Create options menu (triggered by Menu key)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Create and add new menu items.
		MenuItem itemAdd = menu.add(0, ADD_CONTACT, Menu.NONE,
				R.string.add_contact);
		MenuItem itemRefresh = menu.add(0, REFRESH_CONTACT, Menu.NONE,
				R.string.refresh_contacts);

		itemAdd.setIcon(android.R.drawable.ic_menu_add);
		itemRefresh.setIcon(R.drawable.ic_menu_refresh);
		return true;

	}

	// Handle options menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case (ADD_CONTACT):
			// Go to AddContact activity
			Intent addContact = new Intent(getBaseContext(), AddContact.class);
			addContact.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(addContact);
			break;
		case (REFRESH_CONTACT):
			// Just calling the method, actually quite redundant.
			getContacts();
			break;
		}
		return true;
	}

	// Create context menu (Press and hold on List item)
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Actions");
		menu.add(0, CALL_CONTACT, Menu.NONE, R.string.call_contact);
		menu.add(0, TEXT_CONTACT, Menu.NONE, R.string.text_contact);
		menu.add(0, VIEW_CONTACT, Menu.NONE, R.string.view_contact);
		menu.add(0, EDIT_CONTACT, Menu.NONE, R.string.edit_contact);
		menu.add(0, DELETE_CONTACT, Menu.NONE, R.string.delete_contact);
	}

	// Handle context menu
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);

		// Get selected index
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int index = menuInfo.position;

		switch (item.getItemId()) {
		case (CALL_CONTACT):
			Intent callContact = new Intent(Intent.ACTION_CALL,
					Uri.parse("tel:" + contactList.get(index).getPhone()));
			callContact.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(callContact);

			return true;

		case (TEXT_CONTACT):
			Intent textContact = new Intent(Intent.ACTION_SENDTO,
					Uri.parse("smsto:" + contactList.get(index).getPhone()));
			textContact.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			textContact.putExtra("sms_number", contactList.get(index)
					.getPhone());

			startActivity(textContact);

			return true;

		case (VIEW_CONTACT):
			Intent viewContact = new Intent(getBaseContext(), ViewContact.class);
			viewContact.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Put all contact's data across activity
			viewContact.putExtras(contactList.get(index).getBundle());

			startActivity(viewContact);

			return true;

		case (EDIT_CONTACT):
			Intent editContact = new Intent(getBaseContext(), EditContact.class);
			editContact.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Put all contact's data across activity
			editContact.putExtras(contactList.get(index).getBundle());

			startActivity(editContact);

			return true;

		case (DELETE_CONTACT):

			DeleteContactFromXML(contactList.get(index).getPhone());

			contactList.remove(index);
			cAdapter.notifyDataSetChanged();

			return true;

		}
		return false;
	}

	private void GetContactFromXML() {
		// Get the XML
		URL url;

		try {

			// Construct the full URL to get the XML file
			String StringUrl = "http://sit.rp.edu.sg/c345/getcontactlist.php?apikey=e805edb9bd95c94fbc4af49d996556bca334d983";

			url = new URL(StringUrl);

			URLConnection connection;
			connection = url.openConnection();

			// Starts a HTTP connection
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();

			// Standard response for successful HTTP requests
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();

				// A factory API that enables applications to obtain a parser
				// that produces DOM object trees from XML documents
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				// Parse the RSS feed
				Document dom = db.parse(in);
				Element docEle = dom.getDocumentElement();

				// Get contact information by the Tag Name - item
				NodeList nl = docEle.getElementsByTagName("item");

				// Retrieve the child elements in *current_conditions*
				if (nl != null && nl.getLength() > 0) {

					// If there are more than 1 *item*, it will go
					// through each of the XML tree of *item* and
					// retrieve the content from there.

					for (int i = 0; i < nl.getLength(); i++) {
						Element entry = (Element) nl.item(i);

						// Retrieve the child elements by its various tag name

						Element name_data = (Element) entry
								.getElementsByTagName("name").item(0);
						Element func_data = (Element) entry
								.getElementsByTagName("function").item(0);
						Element comp_data = (Element) entry
								.getElementsByTagName("company").item(0);
						Element phone_data = (Element) entry
								.getElementsByTagName("phone").item(0);

						// Extract the String content of the child elements
						String name = name_data.getAttributeNode("data")
								.getValue();
						String func = func_data.getAttributeNode("data")
								.getValue();
						String comp = comp_data.getAttributeNode("data")
								.getValue();
						String phone = phone_data.getAttributeNode("data")
								.getValue();

						Log.d("Contact loaded in xml", name);
						dbAdapter.open();
						Contacts contactObj = new Contacts(0, name, func, comp,
								phone);
						dbAdapter.insertContact(name, func, comp, phone);
						contactList.add(contactObj);
						dbAdapter.close();

					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private void DeleteContactFromXML(String _numb) {
		// Get the XML
		URL url;

		try {

			String encodedNumb = URLEncoder.encode(_numb);

			// Get the city name and construct the full URL to get the XML file
			String StringUrl = "http://sit.rp.edu.sg/c345/delete_contact.php?apikey=e805edb9bd95c94fbc4af49d996556bca334d983&phone="
					+ encodedNumb;

			// Log.d("URL encoded", encodedCityName);
			url = new URL(StringUrl);

			URLConnection connection;
			connection = url.openConnection();

			// Starts a HTTP connection
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();

			// Standard response for successful HTTP requests
			if (responseCode == HttpURLConnection.HTTP_OK) {

				dbAdapter.open();
				dbAdapter.removeContact(_numb);
				dbAdapter.close();
				Log.d("Contact removed", encodedNumb);

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}