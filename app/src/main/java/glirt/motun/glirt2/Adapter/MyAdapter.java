package glirt.motun.glirt2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import glirt.motun.glirt2.Model.Job;
import glirt.motun.glirt2.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<Job> list;
    public MyAdapter(ArrayList<Job> list){
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tix_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.profession.setText(list.get(position).getUserProfession());
        holder.description.setText(list.get(position).getUserDescription());
        holder.username.setText(list.get(position).getUserName());
        holder.address.setText(list.get(position).getUserDescription());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView profession,description, address, username;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profession = itemView.findViewById(R.id.user_profession);
            description= itemView.findViewById(R.id.user_description);
            address= itemView.findViewById(R.id.user_address);
            username= itemView.findViewById(R.id.user_name);

        }
    }

}


