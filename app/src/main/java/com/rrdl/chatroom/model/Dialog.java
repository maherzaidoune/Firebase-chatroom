package com.rrdl.chatroom.model;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.List;

public  class Dialog implements IDialog {
    String Id;
    String DialogPhoto;
    String DialogName;
    List<? extends IUser> Users;
    IMessage LastMessage;
    int UnreadCount;

    public void setId(String id) {
        Id = id;
    }

    public void setDialogPhoto(String dialogPhoto) {
        DialogPhoto = dialogPhoto;
    }

    public void setDialogName(String dialogName) {
        DialogName = dialogName;
    }

    public void setUsers(List<? extends IUser> users) {
        Users = users;
    }

    public void setUnreadCount(int unreadCount) {
        UnreadCount = unreadCount;
    }

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public String getDialogPhoto() {
        return "https://api.adorable.io/avatars/285/" + DialogName + ".png";
    }

    @Override
    public String getDialogName() {
        return DialogName;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return Users;
    }

    @Override
    public IMessage getLastMessage() {
        return LastMessage;
    }

    @Override
    public void setLastMessage(IMessage message) {
        this.LastMessage = message;
    }

    @Override
    public int getUnreadCount() {
        return UnreadCount;
    }
}
