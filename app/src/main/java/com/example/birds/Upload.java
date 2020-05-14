package com.example.birds;

public class Upload {
    private String mName;
    private String ImageUrl;

    public Upload(){
        //Empty Constructor
    }

    public Upload(String name, String url){
        ImageUrl = url;
        if(name.trim().equals("")){
            mName = "No Name";
        }
        else{
            mName = name;
        }
    }

    public String getmName() {
        return mName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
