package com.rrdl.chatroom.model;

import android.os.Parcelable;

import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;

public class User implements Serializable, IUser {
    String Id;
    String Name;
    String Avatar;

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getAvatar() {
        return "https://api.adorable.io/avatars/285/" + Name + ".png";
    }
}
