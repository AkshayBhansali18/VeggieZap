package com.example.zapper2;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter1 extends RecyclerView.Adapter<RecyclerViewAdapter1.ViewHolder> {

    private String TAG = "RecyclerViewAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ConstraintLayout layout;
    ArrayList<String> items;
    ArrayList<String> qty;
    ArrayList<String> price;
    ArrayList<String> ids;
    Context mContext;

    public RecyclerViewAdapter1(Context mContext, ArrayList<String> items, ArrayList<String> price,
                                ArrayList<String> ids,
                                ConstraintLayout layout){
        this.mContext = mContext;
        this.items = items;;
        this.price = price;
        this.layout = layout;
        this.ids = ids;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: The View Binder Has Been Called");
        holder.item.setText(items.get(position));
        holder.price.setText(price.get(position));
        holder.date.setText("");
        holder.qty.setText("");
        Glide.with(mContext)
                .asBitmap()
                .load("https://www.vegsoc.org/wp-content/uploads/2019/03/vegetable-box-750x580.jpg")
                .into(holder.image);
        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(position);
            }
        });
    }

    private void addToCart(final int position) {
        final String item = ids.get(position);
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_window,null);

        final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        Button popYes = customView.findViewById(R.id.popYes);
        popYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("zapper")
                        .document("test@test.com")
                        .collection("products")
                        .document(item)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                ProductsActivity productsActivity = ProductsActivity.getInstance();
                                productsActivity.onStart();
                                popupWindow.dismiss();
                                //mContext.startActivity(new Intent(mContext, MainPageActivity.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: This should'nt have happened");
                            }
                        });
            }
        });

        Button popCan = customView.findViewById(R.id.popCan);
        popCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView item;
        TextView qty;
        TextView price;
        TextView date;
        RelativeLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            item = itemView.findViewById(R.id.item);
            qty = itemView.findViewById(R.id.qty);
            price = itemView.findViewById(R.id.price);
            date = itemView.findViewById(R.id.date);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
