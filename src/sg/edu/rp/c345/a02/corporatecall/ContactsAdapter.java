package sg.edu.rp.c345.a02.corporatecall;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ContactsAdapter extends BaseAdapter {
	
	public ArrayList<Contacts> contactList;
	public Context context;

	public ContactsAdapter(Context context, ArrayList<Contacts> contactList) {
		super();
		this.contactList = contactList;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contactList.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return contactList.get(index);
	}

	@Override
	public long getItemId(int index) {
		// TODO Auto-generated method stub
		return contactList.get(index).getId();
	}

	@Override
	public View getView(int index, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Contacts entry = contactList.get(index);
		return new ContactsAdapterView(context, entry);
	}

}
