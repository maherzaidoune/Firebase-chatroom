package com.rrdl.chatroom.model;

import androidx.annotation.Nullable;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

public class Message implements IMessage{

    private String id;
    private String Text;
    public  IUser user;
    private Date CreatedAt;

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.Text = text;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(Date createdAt) {
        this.CreatedAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return Text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        if(CreatedAt == null)
            return new Date();
        return CreatedAt;
    }
}