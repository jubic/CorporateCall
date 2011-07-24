package sg.edu.rp.c345.a02.corporatecall;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewContact extends Activity {

	Contacts contactInfo;
	TextView detailsTV;
	int callLogCount, callCount, totalCallCount, callDate;
	String callDuration, totalCallDuration;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.individual_contact_details);

		// Get Intents From CorporateCall
		contactInfo = new Contacts(getIntent().getExtras());

		// Get View From Layout
		detailsTV = (TextView) findViewById(R.id.individual_contact_detailsTV);

		// Concatenate TextView with Strings from Intent
		detailsTV.setText("Name:\t" + contactInfo.getName() + "\nFunction:\t"
				+ contactInfo.getFunction() + "\nCompany:\t"
				+ contactInfo.getCompany() + "\nPhone Number:\t"
				+ contactInfo.getPhone());
		
	}
}
