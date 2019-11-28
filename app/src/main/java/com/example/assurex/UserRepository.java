package com.example.assurex;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import com.example.assurex.database.AppDatabase;
import com.example.assurex.model.User;

import java.util.List;

public class UserRepository {
    private String DB_NAME = "db-user";

    private AppDatabase appDatabase;

    public UserRepository(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }

    public void insertUser(String un, String pw, String em) {
        User user = new User();
        user.setUsername(un);
        user.setPassword(pw);
        user.setEmail(em);
        insertUser(user);
    }

    public void insertUser(final User user) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.userDao().insertUser(user);
                return null;
            }
        }.execute();
    }

    /*public void deleteUser(final String un) {
        final LiveData<User> user = getUser(un);
        if(user != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    appDatabase.userDao().deleteUser(user.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteUser(final User user) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.userDao().deleteUser(user);
                return null;
            }
        }.execute();
    }*/

    public User getUser(String un) {
        return appDatabase.userDao().getUser(un);
    }

    public List<User> getUser() {
        return appDatabase.userDao().fetchAllUsers();
    }
}