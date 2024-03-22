package Client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Entity.Message;
import Entity.MessageType;
import Entity.User;

import javax.swing.*;

public class LogController implements PropertyChangeListener{
    private ClientNetworkBoundary cnb;
    private ClientViewController cvc;


    public LogController(ClientViewController cvc) {
        this.cvc = cvc;
    }

    public void logIn(String userName) {
        User user = new User(userName,null);
        ClientNetworkBoundary cnb = new ClientNetworkBoundary("127.0.0.1", 1234);
        cnb.addPropertyChangeListener(this);
        Message message = new Message(MessageType.logIn, null, user, null, LocalDateTime.now(), null);
        cnb.sendMessage(message);
    }

    public void logOut(){

    }

    public void addUser(String userName, Icon icon){
        User user = new User(userName, icon);
        cnb = new ClientNetworkBoundary("127.0.0.1", 1234);
        cnb.addPropertyChangeListener(this);
        Message message = new Message(MessageType.registerUser, null, user, null, LocalDateTime.now(), null);
        cnb.sendMessage(message);
    }

    public void registerFail(Message message) {
        cvc.getRegisterUserFrame().setError("Registration failed. User already exists.");
    }

    public void registerSuccess() {
        cvc.getRegisterUserFrame().setSuccess();
    }

    public void loginSuccess(User user) {
        cvc.getLoginFrame().setSuccess();
        cvc.getMainFrame().setLoggedIn(user);

    }

    public void loginFail() {
        cvc.getLoginFrame().setError("Login failed. User does not exist.");
        System.out.println("login failed");
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("registerFail".equals(evt.getPropertyName())){
            Message message = (Message) evt.getNewValue();
            registerFail(message);
        }
        else if("registerSuccess".equals(evt.getPropertyName())) {
            registerSuccess();
        }
        else if("loginSuccess".equals(evt.getPropertyName())) {
            Message message = (Message) evt.getNewValue();
            User user = message.getSender();
            loginSuccess(user);
        }
        else if("logFail".equals(evt.getPropertyName())) {
            loginFail();
        }
    }
}
