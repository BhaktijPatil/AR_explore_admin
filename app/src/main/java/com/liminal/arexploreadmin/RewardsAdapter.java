package com.liminal.arexploreadmin;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.MyViewHolder> {

    private List<RewardDetails> rewardDetailsList;
    private DatabaseReference databaseReference;
    private Context context;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reward_list_row, parent, false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("rewardsTable");
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RewardDetails rewardDetails = rewardDetailsList.get(position);
        holder.title.setText(rewardDetails.title);
        holder.description.setText(rewardDetails.description);
        holder.cost.setText(String.valueOf(rewardDetails.cost));
        holder.quantity.setText(String.valueOf(rewardDetails.quantity));
        holder.category.setText(rewardDetails.category);
        holder.delete.setOnClickListener(view -> databaseReference.child(rewardDetails.category).child(rewardDetails.rid).removeValue());
        holder.update.setOnClickListener(view -> {
            final Dialog inputTextDialog = new Dialog(context);
            inputTextDialog.setContentView(R.layout.dialog_reward_update);

            Button cancelButton = inputTextDialog.findViewById(R.id.cancelButton);
            Button uploadButton = inputTextDialog.findViewById(R.id.uploadButton);

            EditText title = inputTextDialog.findViewById(R.id.title);
            EditText description = inputTextDialog.findViewById(R.id.description);
            EditText cost = inputTextDialog.findViewById(R.id.cost);
            EditText quantity = inputTextDialog.findViewById(R.id.quantity);

            title.setText(rewardDetails.title);
            description.setText(rewardDetails.description);
            cost.setText(String.valueOf(rewardDetails.cost));
            quantity.setText(String.valueOf(rewardDetails.quantity));

            cancelButton.setOnClickListener(v -> inputTextDialog.dismiss());
            uploadButton.setOnClickListener(v -> {
                uploadText(rewardDetails.rid, rewardDetails.category, String.valueOf(title.getText()),String.valueOf(description.getText()),String.valueOf(cost.getText()),String.valueOf(quantity.getText()));
                inputTextDialog.dismiss();
            });
            inputTextDialog.show();
        });
    }

    private void uploadText(String rid, String category, String title, String description, String cost, String quantity) {
        databaseReference.child(category).child(rid).child("title").setValue(title);
        databaseReference.child(category).child(rid).child("description").setValue(description);
        databaseReference.child(category).child(rid).child("cost").setValue(Long.parseLong(cost));
        databaseReference.child(category).child(rid).child("quantity").setValue(Long.parseLong(quantity));
    }

    @Override
    public int getItemCount() {
        return rewardDetailsList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, cost, quantity, category;
        Button update, delete;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            cost = itemView.findViewById(R.id.cost);
            quantity = itemView.findViewById(R.id.quantity);
            category = itemView.findViewById(R.id.category);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    RewardsAdapter(List<RewardDetails> rewardDetailsList, Context context){
        this.rewardDetailsList = rewardDetailsList;
        this.context = context;
    }
}
