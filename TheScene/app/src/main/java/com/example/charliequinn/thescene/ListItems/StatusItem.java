package com.example.charliequinn.thescene.ListItems;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by charliequinn on 1/16/17.
 */

public class StatusItem {
    public String name;
    public String status;
    public int userIDX;
    public Bitmap profliePic;
    public StatusItem(String name, String status, int userIDX){
        this.name = name;
        this.status = status;
        this.userIDX = userIDX;
    }

    public void setPhoto(Bitmap bm){
        this.profliePic = bm;
    }
}
