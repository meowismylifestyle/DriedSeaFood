package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowResultFragment extends Fragment {
    Bitmap selectedImage;
    Fish_Item predictedFish;
    String confidence;

    ImageView selectedImageView;
    CircleImageView predictedFishImageView;
    TextView predictedFishName;
    TextView predictedFishPrice;
    TextView confidenceTextView;

    public ShowResultFragment(Bitmap selectedImage, Fish_Item predictedResult, String confidence) {
        super(R.layout.activity_show_result);
        this.selectedImage = selectedImage;
        this.predictedFish = predictedResult;
        this.confidence = confidence;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            selectedImageView = view.findViewById(R.id.selected_image_view);
            predictedFishImageView = view.findViewById(R.id.predicted_image);
            predictedFishName = view.findViewById(R.id.name_fish);
            predictedFishPrice = view.findViewById(R.id.price_fish);
//        confidenceTextView = view.findViewById(R.id.confidence_TextView);

            selectedImageView.setImageBitmap(this.selectedImage);

            if (predictedFish != null) {
                predictedFishImageView.setImageBitmap (
                        BitmapFactory.decodeResource (
                                view.getContext().getResources(),
                                predictedFish.getImage()
                        )
                );
                predictedFishName.setText(predictedFish.getName());
                predictedFishPrice.setText(predictedFish.getPrice());
                // confidenceTextView.setText(confidence);
            }
        } else
            Log.d("ShowResultFragment", "onCreateView: view is null");

        return view;
    }
}
