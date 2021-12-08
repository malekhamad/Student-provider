package com.astudent.partner.Adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.astudent.partner.Bean.Contact;
import com.astudent.partner.Fragments.ContactShareFragment;
import com.astudent.partner.R;

import java.util.List;

/**
 * Created by Tranxit Technologies.
 */
public class ContactShareListAdapter extends RecyclerView.Adapter<ContactShareListAdapter.Viewholder> {

    List<Contact> contacts;
    ContactShareFragment contactShareFragment;

    public ContactShareListAdapter(List<Contact> contacts, ContactShareFragment contactShareFragment) {
        this.contacts = contacts;
        this.contactShareFragment = contactShareFragment;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_share_list_item,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        final Contact contact = contacts.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvPhoneNumber.setText(contact.getPhone() + "");
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    contactShareFragment.setValueForContactSelection(position,contact.getPhone());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView tvName,tvPhoneNumber;
        CheckBox checkBox;
        public Viewholder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            tvName = itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = itemView.findViewById(R.id.tvContactNumber);
        }
    }
}
