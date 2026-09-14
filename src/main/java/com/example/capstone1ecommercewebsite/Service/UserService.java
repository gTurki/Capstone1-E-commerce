package com.example.capstone1ecommercewebsite.Service;

import com.example.capstone1ecommercewebsite.Model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {

    private final ArrayList<User> users = new ArrayList<>();

    public ArrayList<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public boolean updateUser(String id, User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equalsIgnoreCase(id)) {
                users.set(i, user);
                return true;
            }
        }
        return false;
    }

    public boolean deleteUser(String id) {
        for (User u : users) {
            if (u.getId().equalsIgnoreCase(id)) {
                users.remove(u);
                return true;
            }
        }
        return false;
    }
}
