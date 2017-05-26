package com.example.charliequinn.thescene.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.charliequinn.thescene.Activities.MainActivity;
import com.example.charliequinn.thescene.Adapters.StatusAdapter;
import com.example.charliequinn.thescene.Helpers.Uploader;
import com.example.charliequinn.thescene.ListItems.StatusItem;
import com.example.charliequinn.thescene.R;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public final int REQUEST_CAMERA_PERMISSION = 3;
    public static final int REQUEST_IMAGE_CAPTURE = 3;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View myFragmentView;
    private StatusAdapter adapter;
    private int userIDX;
    private ListView listView;
    private Button addFriend;
    private ImageButton changeProfilePic;
    private TextView usernameTV;
    private Button friendCount;
    private Button checkinCount;
    private Button promoterCount;
    private ProgressBar progress;
    private Bitmap photo;
    private int[] friendIDs;
    private int checkins;

    String mCurrentPhotoPath;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        listView = (ListView) myFragmentView.findViewById(R.id.my_status);
        addFriend = (Button) myFragmentView.findViewById(R.id.add_friend_button);
        addFriend.setVisibility(View.GONE);
        progress = (ProgressBar) myFragmentView.findViewById(R.id.progressBarProfile);
        progress.setVisibility(View.GONE);
        usernameTV = (TextView) myFragmentView.findViewById(R.id.usernameDisplay);
        friendCount = (Button) myFragmentView.findViewById(R.id.friendsDisplay);
        promoterCount = (Button) myFragmentView.findViewById(R.id.promotersDisplay);
        checkinCount = (Button) myFragmentView.findViewById(R.id.checkinsDisplay);

        changeProfilePic = (ImageButton) myFragmentView.findViewById(R.id.profilepic);

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.i("profile pic", "permissions were granted");
            setOnClickForPic();
        }else{
            Log.i("profile pic", "requesting permissions");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }



        MainActivity ma = (MainActivity) getActivity();
        userIDX = ma.getUserIDX();



        new DownloadProfileInfo().execute(userIDX+"");



        friendCount.setText("FRIENDS\n"+friendIDs.length);
        promoterCount.setText("PROMOTERS\n"+0);


        // Inflate the layout for this fragment
        return myFragmentView;
    }

    private void setOnClickForPic(){
        changeProfilePic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takePicture();

            }
        });
    }

    public  void takePicture(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch (IOException e){
                Log.e("profile pic",e.toString());
            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this.getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.i("profile pic","starting camera intent");
                getActivity().startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                Log.i("profile pic", "inside on request permissions result");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("profile pic","setting up button");
                    setOnClickForPic();
                } else {
                    Log.i("profile pic","no camerin");
                }
                return;
            }
        }
    }



    public void changeProfilePicture(){
        int targetW = changeProfilePic.getWidth();
        int targetH = changeProfilePic.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;



        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);


        try{
            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    uploadProfilePic(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    uploadProfilePic(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    uploadProfilePic(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    uploadProfilePic(bitmap, 0);


                default:
                    break;
            }
        }catch (IOException e){
            Log.e("profile pic",e.toString());
        }
    }

    public void uploadProfilePic(Bitmap bitmap, int degreeRotation){
        Bitmap rotated = rotateImage(bitmap, degreeRotation);
        changeProfilePic.setImageBitmap(rotated);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        rotated.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        new uploadPhoto().execute(userIDX+"",encodedImage);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void createAdapter(){
        HashMap<Integer, Bitmap> hm = new HashMap<>();
        hm.put(userIDX, photo);
        adapter = new StatusAdapter(getActivity(),new ArrayList<StatusItem>(),hm);

        new downloadStatuses().execute(userIDX+"", "self");
    }

    public void createAdapterForReal(JSONArray jsonArray) throws JSONException {
        JSONObject job;
//        checkins = jsonArray.length();
        checkinCount.setText("CHECKINS\n"+jsonArray.length());
        for(int i = jsonArray.length()-1; i >= 0; i--){
            job = jsonArray.getJSONObject(i);
            adapter.add(new StatusItem(job.getString("username"), job.getString("placename"), job.getInt("useridx")));
        }
        createListView(adapter);
    }

    public void createListView(final StatusAdapter adapter) {
        if (listView != null) {
            listView.setAdapter(adapter);
        } else {
            Log.i("ay", "shit");
        }
    }

    public void setFriendIDs(int[] friendIDs){
        Log.i("setFriendIDs","setting friend IDs in profile to "+friendIDs.length);
        this.friendIDs = friendIDs;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
       // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "profile_pic",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private class DownloadProfileInfo extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("downloadPicture","beginning picture download");
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected  String doInBackground(String... strings){
            String[] param = {"profileInfoGetter","POST"};
            String[] keys = {"useridx"};
            return Uploader.getInstance().genericUpload(param,keys,strings);
        }
        @Override
        protected void onPostExecute(String serverReply){
            if(serverReply.equals("error response")){
                Log.i("getPicture","Something messed up");
            }else{
                try{
                    JSONArray jarray = new JSONArray(serverReply);
                    JSONObject job = jarray.getJSONObject(0);

                    String username = job.getString("username");
                    usernameTV.setText(username);

                    String picString = job.getString("profilepic");
                    if(picString.length() > 6){
                        Log.i("getPicture",picString.length()+ " a "+picString);
                        byte barray[] = Base64.decode(picString,4);
                        photo = BitmapFactory.decodeByteArray(barray,0,barray.length);
                        changeProfilePic.setImageBitmap(photo);
                    }else{
                        Log.i("getPicture","no profile picture");
                    }

                    createAdapter();

                }catch (Exception e){
                    Log.e("downloadStatus", "Server error: "+serverReply+", "+e.toString() );
                }
            }
        }

    }

    private class uploadPhoto extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute(){
            Log.i("uploadPhoto","beginning photo upload");
            //progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected  String doInBackground(String... stuff){
            String[] param = {"profilePicSetter","POST"};
            String[] keys = {"useridx","photo"};
            return Uploader.getInstance().genericUpload(param,keys,stuff);
        }
        @Override
        protected void onPostExecute(String serverReply){
            //progress.setVisibility(View.GONE);
            if(serverReply.equals("error response")){
                Log.i("profilePic","Something messed up");
            }else{
                try{
                    Log.i("profilePic","server says: "+serverReply);
                }catch (Exception e){
                    Log.e("ProfilePic", "Server error: "+serverReply+", "+e.toString() );
                }
            }
        }
    }

    private class downloadStatuses extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("downloadStatuses","beginning status download");
            //progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected  String doInBackground(String... strings){
            String[] param = {"getStatuses","POST"};
            String[] keys = {"useridx","filter"};
            return Uploader.getInstance().genericUpload(param,keys,strings);
        }
        @Override
        protected void onPostExecute(String serverReply){
            progress.setVisibility(View.GONE);
            if(serverReply.equals("error response")){
                Log.i("downloadStatus","Something messed up");
            }else{
                try{
                    JSONArray jarray = new JSONArray(serverReply);
                    createAdapterForReal(jarray);
                }catch (Exception e){
                    Log.e("downloadStatus", "Server error: "+serverReply+", "+e.toString() );
                }
            }
        }

    }
}
