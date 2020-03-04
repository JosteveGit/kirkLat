package glirt.motun.glirt2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import glirt.motun.glirt2.Model.User;

import java.util.List;

public class OneViewModel extends ViewModel {
    private MutableLiveData<List<Message>> _messages = new MutableLiveData<>();

    private MutableLiveData<List<User>> _users = new MutableLiveData<>();


    public void set_messages(List<Message> messages){
        _messages.setValue(messages);
    }


    public void set_users(List<User> users){_users.setValue(users);}


    public LiveData<List<Message>> get_messages(){
        return _messages;
    }

    public LiveData<List<User>> get_users(){return _users;}
}
