package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import com.example.myapplication.ml.Model;

public class SearchFragment extends Fragment {
    private RecyclerView rcvFish;
    private FishAdapter fishAdapter;
    private SearchView searchView;
    private ImageButton buttonAddPhoto;
    private ImageButton buttonSelectPhoto;
    private View view;
    private FragmentActivity activity;
    private Context context;
    final int imageSize = 224;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        if (activity == null)
            Log.d("SearchFragment", "onCreate: activity is null");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            context = view.getContext();
            rcvFish = view.findViewById(R.id.rcv_fish);
            searchView = view.findViewById(R.id.searchView);
            buttonAddPhoto = view.findViewById(R.id.btn_add_photo);
            buttonSelectPhoto = view.findViewById(R.id.btn_select_photo);
        } else
            Log.d("SearchFragment", "onCreateView: view is null");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        rcvFish.setLayoutManager(linearLayoutManager);

        fishAdapter = new FishAdapter(getListFish());
        rcvFish.setAdapter(fishAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        rcvFish.addItemDecoration(itemDecoration);

        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fishAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fishAdapter.getFilter().filter(newText);
                return false;
            }
        });

        buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                Log.d("SearchFragment", "buttonAddPhoto is clicked");
                currentPhotoPath = "";

                String[] requiredPermissions = {
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                boolean grantedAllPermissions = true;
                for (String permission : requiredPermissions) {
                    if (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_DENIED)
                        grantedAllPermissions = false;
                }

                if (!grantedAllPermissions) {
                    ActivityCompat.requestPermissions(
                            activity,
                            requiredPermissions,
                            100
                    );
                }

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(
                                view.getContext(),
                                "com.example.android.fileprovider",
                                photoFile
                        );
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        takePictureActivityResultLauncher.launch(takePictureIntent);
                    }
                }
            }

            final ActivityResultLauncher<Intent> takePictureActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                // There are no request codes
                                Intent data = result.getData();

                                File capturedImage = new File(currentPhotoPath);

                                if (capturedImage.exists()) {
                                    Bitmap imgBitmap = BitmapFactory.decodeFile(capturedImage.getAbsolutePath());
                                    Pair<Fish_Item, String> predictedResult = classifyImage(imgBitmap);
                                    showResult(imgBitmap, predictedResult);
                                    galleryAddPic();
                                }
                            }
                        }
                    });
        });

        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                selectPictureActivityResultLauncher.launch(chooserIntent);
            }

            final ActivityResultLauncher<Intent> selectPictureActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                // There are no request codes
                                Intent data = result.getData();
                                Uri contentUri = Objects.requireNonNull(data).getData();
                                if (contentUri != null) {
                                    try {
                                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), contentUri);
                                        Pair<Fish_Item, String> predictedResult = classifyImage(imageBitmap);
                                        showResult(imageBitmap, predictedResult);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
        });
        return view;
    }

    private void showResult(Bitmap imageBitmap, Pair<Fish_Item, String> predictedResult) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment showResultFragment = new ShowResultFragment(imageBitmap, predictedResult.first, predictedResult.second);
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, showResultFragment)
                .addToBackStack(null)
                .commit();
    }

    private String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + File.separator + "FindYourDriedFish"
        );
        boolean storageDirExist = storageDir.exists();
        if (!storageDirExist) {
            Log.d("SearchFragment", "createImageFile: storageDir not exists");
            storageDirExist = storageDir.mkdirs();
        }

        if (storageDirExist) {
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.getAbsolutePath();
            return image;
        }

        return null;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        view.getContext().sendBroadcast(mediaScanIntent);
    }

    public static ArrayList<Fish_Item> getListFish() {
//        ArrayList<Fish_Item> list = new ArrayList<>();
//        list.add(new Fish_Item(R.drawable.image1,"Kh?? c?? D???a","100.000/kg", "dua"));
//        list.add(new Fish_Item(R.drawable.image2,"Kh?? c?? L??c","200.000/kg", "loc"));
//        list.add(new Fish_Item(R.drawable.image3,"Kh?? c?? S???c","120.000/kg", "sac"));
//        list.add(new Fish_Item(R.drawable.image4,"Kh?? c?? ????","130.000/kg", "du"));
//        list.add(new Fish_Item(R.drawable.image5,"Kh?? c?? Khoai","140.000/kg", "khoai"));
//        list.add(new Fish_Item(R.drawable.image6,"Kh?? c?? Ch??? V??ng","150.000/kg", "chivang"));
//        list.add(new Fish_Item(R.drawable.image7,"Kh?? c?? C??m","160.000/kg", "com"));
//        list.add(new Fish_Item(R.drawable.image8,"Kh?? c?? ??u???i","210.000/kg", "duoi"));
//        list.add(new Fish_Item(R.drawable.image9,"Kh?? c?? M???i","190.000/kg", "moi"));
//        list.add(new Fish_Item(R.drawable.image10,"Kh?? c?? Tr???ch","90.000/kg", "chach"));
//        list.add(new Fish_Item(R.drawable.image11,"Kh?? c?? S???u","190.000/kg", "suu"));
//        list.add(new Fish_Item(R.drawable.image12,"Kh?? c?? Tra","80.000/kg", "tra"));
//        list.add(new Fish_Item(R.drawable.image13,"Kh?? c?? L?????i Tr??u","170.000/kg", "luoitrau"));
//        list.add(new Fish_Item(R.drawable.image14,"Kh?? c?? K??o","110.000/kg", "keo"));
//        list.add(new Fish_Item(R.drawable.image15,"Kh?? c?? Th??i L??i","175.000/kg", "thoiloi"));
//        list.add(new Fish_Item(R.drawable.image16,"Kh?? c?? ????t","195.000/kg", "det"));
//        list.add(new Fish_Item(R.drawable.image17,"Kh?? c?? L??ng Tong","230.000/kg", "longtong"));
//        return list;

        ArrayList<Fish_Item> list = new ArrayList<>();
        list.add(new Fish_Item(R.drawable.bong, "Kh?? c?? B???ng","270.000/kg","bong"));
        list.add(new Fish_Item(R.drawable.chach, "Kh?? c?? Ch???ch","460.000/kg","chach"));
        list.add(new Fish_Item(R.drawable.chivang, "Kh?? c?? Ch??? V??ng","210.000/kg","chivang"));
        list.add(new Fish_Item(R.drawable.chot, "Kh?? c?? Ch???t","270.000/kg","chot"));
        list.add(new Fish_Item(R.drawable.com, "Kh?? c?? C??m","180.000/kg","com"));
        list.add(new Fish_Item(R.drawable.det, "Kh?? c?? ????t","230.000/kg","det"));
        list.add(new Fish_Item(R.drawable.dong, "Kh?? c?? ?????ng","180.000/kg","dong"));
        list.add(new Fish_Item(R.drawable.du, "Kh?? c?? ????","160.000/kg","du"));
        list.add(new Fish_Item(R.drawable.dua, "Kh?? c?? D???a","180.000/kg","dua"));
        list.add(new Fish_Item(R.drawable.duoi, "Kh?? c?? ??u???i","270.000/kg","duoi"));
        list.add(new Fish_Item(R.drawable.ho, "Kh?? c?? H???","200.000/kg","ho"));
        list.add(new Fish_Item(R.drawable.keo, "Kh?? c?? K??o","300.000/kg","keo"));
        list.add(new Fish_Item(R.drawable.khoai, "Kh?? c?? Khoai","380.000/kg","khoai"));
        list.add(new Fish_Item(R.drawable.loc, "Kh?? c?? L??c","240.000/kg","loc"));
        list.add(new Fish_Item(R.drawable.longtong, "Kh?? c?? L??ng Tong","440.000/kg","longtong"));
        list.add(new Fish_Item(R.drawable.luoitrau, "Kh?? c?? L?????i Tr??u","350.000/kg","luoitrau"));
        list.add(new Fish_Item(R.drawable.mai, "Kh?? c?? Mai","370.000/kg","mai"));
        list.add(new Fish_Item(R.drawable.moi, "Kh?? c?? M???i","240.000/kg","moi"));
        list.add(new Fish_Item(R.drawable.muc, "Kh?? M???c","900.000/kg","muc"));
        list.add(new Fish_Item(R.drawable.nhai, "Kh?? c?? Nh??i","500.000/kg","nhai"));
        list.add(new Fish_Item(R.drawable.nhong, "Kh?? c?? Nh???ng","200.000/kg","nhong"));
        list.add(new Fish_Item(R.drawable.nuc, "Kh?? c?? N???c","85.000/kg","nuc"));
        list.add(new Fish_Item(R.drawable.ruoc,"Kh?? c?? Ru???c","79.000/kg","ruoc"));
        list.add(new Fish_Item(R.drawable.sac, "Kh?? c?? S???c","270.000/kg","sac"));
        list.add(new Fish_Item(R.drawable.thieu, "Kh?? c?? Thi???u","85.000 ~ 650.000/kg","thieu"));
        list.add(new Fish_Item(R.drawable.thoiloi, "Kh?? c?? Th??i L??i","530.000/kg","thoiloi"));
        list.add(new Fish_Item(R.drawable.thu, "Kh?? c?? Thu","270/kg","thu"));
        list.add(new Fish_Item(R.drawable.tom_80, "Kh?? T??m","1.350.000/kg","tom"));
        list.add(new Fish_Item(R.drawable.tren_60, "Kh?? c?? Tr??n","500.000/kg","tren"));
        list.add(new Fish_Item(R.drawable.trich, "Kh?? c?? Tr??ch","300.000/kg","trich"));

        return list;
    }

    @SuppressLint("DefaultLocale")
    Pair<Fish_Item, String> classifyImage(Bitmap image) {
        try {
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            Model model = Model.newInstance(activity.getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int [] intValues = new int[imageSize * imageSize];

            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = Fish_Item.getClassLabelList(getListFish());
            String predictedClass = classes[maxPos];

            Fish_Item returnFish = new Fish_Item(R.drawable.ic_block,
                    getString(R.string.unknown),
                    getString(R.string.unknown),
                    "unknown");
            String confidence = "0.0%";

            ArrayList<Fish_Item> fishItems = getListFish();
            for (Fish_Item fish : fishItems) {
                if (fish.isLabel(predictedClass)) {
                    returnFish = fish;
                    confidence = String.format("%.1f%%", maxConfidence * 100);
                }
            }

            return new Pair<>(returnFish, confidence);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public SearchFragment() {
        super(R.layout.search_activity);
    }
}
