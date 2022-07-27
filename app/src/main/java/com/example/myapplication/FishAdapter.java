package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class FishAdapter extends RecyclerView.Adapter<FishAdapter.FishViewHolder> implements Filterable {
    private List<Fish_Item> mListFish;
    private List<Fish_Item> mListFishOld;

    public FishAdapter(List<Fish_Item> mListFish){
        this.mListFish = mListFish;
        this.mListFishOld = mListFish;
    }
    @NonNull
    @Override
    public FishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fish,parent,false);
        return new FishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FishViewHolder holder, int position) {
        Fish_Item fish = mListFish.get(position);
        if(fish == null){
            return;
        }
        holder.imgFish.setImageResource(fish.getImage());
        holder.Name.setText(fish.getName());
        holder.Price.setText(fish.getPrice());
    }

    @Override
    public int getItemCount() {
        if(mListFish != null){
            return mListFish.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if(strSearch.isEmpty()){
                    mListFish = mListFishOld;
                }else {
                    List<Fish_Item> list = new ArrayList<>();
                    for(Fish_Item x : mListFishOld){
                        if(x.getName().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(x);
                        }
                    }
                    mListFish = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mListFish;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListFish = (List<Fish_Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class FishViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imgFish;
        private TextView Name;
        private TextView Price;

        public FishViewHolder (@NonNull View itemView){
            super(itemView);
            imgFish = itemView.findViewById(R.id.img_fish);
            Name = itemView.findViewById(R.id.name_fish);
            Price = itemView.findViewById(R.id.price_fish);
        }

    }
}
