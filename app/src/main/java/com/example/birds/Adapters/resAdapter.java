package com.example.birds.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birds.Model.Result;
import com.example.birds.R;

import java.util.List;

public class resAdapter extends RecyclerView.Adapter<resAdapter.ViewHolder> {

    private Context mcontext;
    private List<Result> mUsers;

    public resAdapter(Context mContext, List<Result> mUsers){
        this.mUsers = mUsers;
        this.mcontext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.res_item, parent, false);
        return new resAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Result user = mUsers.get(position);
        holder.username.setText(user.getName().replace('_', ' '));
        holder.conf.setText("Confidence: "+ user.getConf()+"%");

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mcontext, MessageActivity.class);
//                intent.putExtra("userid", user.getid());
//                mcontext.startActivity(intent);
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, conf;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.birdname);
            conf = itemView.findViewById(R.id.conf_level);
        }
    }

}
