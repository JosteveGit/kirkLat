package glirt.motun.glirt2;

import glirt.motun.glirt2.Model.User;

public class Message extends OneClass{
    public User user;

    public Message(User user, String message) {
        this.user = user;
        this.message = message;
    }

    public String message;
}
