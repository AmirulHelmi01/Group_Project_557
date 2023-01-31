package com.example.groupproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;
import com.example.groupproject.model.Consultation;
import com.example.groupproject.model.User;
import java.util.List;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        public TextView lecturerName;
        public TextView date;
        public TextView time;
        public TextView purpose;
        public TextView status;

        public ViewHolder(View itemView) {
            super(itemView);

            lecturerName = (TextView) itemView.findViewById(R.id.lecturerName);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            purpose = (TextView) itemView.findViewById(R.id.purpose);
            status = (TextView) itemView.findViewById(R.id.status);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }
    }

    private List<Consultation> mListData;   // list of book objects
    private Context mContext;       // activity context
    private int currentPos;         //current selected position.
    private User user;

    public RequestListAdapter(Context context, List<Consultation> listData, User user){
        mListData = listData;
        mContext = context;
        this.user = user;
    }

    private Context getmContext(){return mContext;}


    @Override
    public RequestListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        Consultation c = null;
        boolean status = false;

        if(mListData != null)
        {
            for (int i = 0; i < mListData.size(); i++) {
                c = mListData.get(i);
                if (c.getStudent_id() == user.getId()) {
                    status = true;
                }
            }
        }

        // bind data to the view holder
        View view;

        if(status){
            // Inflate the single item layout
            view = inflater.inflate(R.layout.student_listview, parent, false);
        }
        else {
            // Inflate the single item layout
            view = inflater.inflate(R.layout.empty_listview, parent, false);
        }

        // Return a new holder instance
        RequestListAdapter.ViewHolder viewHolder = new RequestListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mListData != null)
        {
            // bind data to the view holder
            Consultation c = mListData.get(position);

            if (c.getStudent_id() == user.getId()) {
                holder.lecturerName.setText(c.getLecturer().getName());
                holder.date.setText(c.getConsult_date());
                holder.time.setText(c.getConsult_time());
                holder.purpose.setText(c.getPurpose());
                holder.status.setText(c.getStatus());
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;

        if(mListData != null)
        {
            for (int i = 0; i < mListData.size(); i++) {
                Consultation c = mListData.get(i);
                if (c.getStudent_id() == user.getId())
                    size++;
            }
        }

        if(size == 0)
            return 1;
        else
            return size;
    }

    public Consultation getSelectedItem() {
        if(currentPos>=0 && mListData!=null && currentPos<mListData.size()) {
            return mListData.get(currentPos);
        }
        return null;
    }
}