package Server;

import Entity.Buffer;
import Entity.Message;
import Entity.MessageType;
import Entity.User;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserController implements PropertyChangeListener {
    private ServerNetworkBoundary serverNetworkBoundary;
    private HashMap<User, ServerNetworkBoundary.ClientHandler> clients = new HashMap<>();
    private List<User> allUsers;


    public UserController(ServerNetworkBoundary serverNetworkBoundary) {
        this.serverNetworkBoundary = serverNetworkBoundary;
        serverNetworkBoundary.addPropertyChangeListener(this);
        allUsers = new ArrayList<>();

        //testvärden för användare
        ImageIcon userImage1 = new ImageIcon("images/loubro.png");
        ImageIcon resizedImage1 = new ImageIcon(userImage1.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
        User user1 = new User("loubro", resizedImage1);
        ImageIcon userImage2 = new ImageIcon("images/alacol.png");
        ImageIcon resizedImage2 = new ImageIcon(userImage2.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
        User user2 = new User("alacol", resizedImage2);
        ImageIcon userImage3 = new ImageIcon("images/idanor.png");
        ImageIcon resizedImage3 = new ImageIcon(userImage3.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
        User user3 = new User("idanor", resizedImage3);
        ImageIcon userImage4 = new ImageIcon("images/kenalt.png");
        ImageIcon resizedImage4 = new ImageIcon(userImage4.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
        User user4 = new User("kenalt", resizedImage4);
        allUsers.add(user1);
        allUsers.add(user2);
        allUsers.add(user3);
        allUsers.add(user4);
    }

    public boolean checkIfUsersExists(User user) {
        boolean b = false;
        for (User u : allUsers) {
            if (u.getUserName().equals(user.getUserName())) {
                return true;
            } else {
                System.out.println("Can't find user");
                b = false;
            }
        }
        return b;
    }

    public boolean checkHashmapList(User user) {
        boolean b = false;
        for (User u : clients.keySet()) {
            if (u.getUserName().equals(user.getUserName())) {
                b = true;
            } else {
                b = false;
            }
        }
        return b;
    }

    public void logIn(User user, ServerNetworkBoundary.ClientHandler client) {
        if (checkIfUsersExists(user)) {
            User savedUser = null;
            String userName = user.getUserName();
            for (User u : allUsers) {
                if (u.getUserName().equals(userName)) {
                    savedUser = u;
                }
            }
            if (checkHashmapList(savedUser)) {
                clients.remove(savedUser);
                System.out.println("User " + savedUser.getUserName() + " is already logged in. Updating login information.");
            }
            clients.put(savedUser, client);
            System.out.println("User " + savedUser.getUserName() + " logged in successfully.");
            Message message = new Message(MessageType.loginSuccess, savedUser);
            serverNetworkBoundary.sendMessage(message, client);
        } else {
            System.out.println("User " + user.getUserName() + " does not exist.");
            Message message = new Message(MessageType.loginFail);
            serverNetworkBoundary.sendMessage(message, client);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("login".equals(evt.getPropertyName())) {
            //loginBuffer.put((Message) evt.getNewValue());
            User user = (User) evt.getNewValue();
            ServerNetworkBoundary.ClientHandler client = (ServerNetworkBoundary.ClientHandler) evt.getOldValue();
            logIn(user, client);
        } else if ("register".equals(evt.getPropertyName())) {
            Message message = (Message) evt.getOldValue();
            User user = message.getSender();
            ServerNetworkBoundary.ClientHandler client = (ServerNetworkBoundary.ClientHandler) evt.getNewValue();
            try {
                addUser(user, client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addUser(User user, ServerNetworkBoundary.ClientHandler client) throws IOException {
        boolean userExists = checkIfUsersExists(user);

        if (userExists == false) {
            allUsers.add(user);
            Message message = new Message(MessageType.registerSuccess);
            serverNetworkBoundary.sendMessage(message, client);
        } else {
            Message message = new Message(MessageType.registerFail);
            serverNetworkBoundary.sendMessage(message, client);
        }
    }
}