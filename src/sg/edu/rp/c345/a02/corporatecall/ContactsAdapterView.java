package sg.edu.rp.c345.a02.corporatecall;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactsAdapterView extends RelativeLayout {

	public ContactsAdapterView(Context context, Contacts contact) {
		super(context);
		// TODO Auto-generated constructor stub
		View v = inflate(context, R.layout.individual_contact, null);
		
		TextView nameTV = (TextView)v.findViewById(R.id.nameTV);
		
		nameTV.setText(contact.getName());
		
		addView(v);
	}

}