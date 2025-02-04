package com.example.netflix_app4.db;

import androidx.lifecycle.LiveData;
import androidx.room.*;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE userName = :userName LIMIT 1")
    LiveData<User> getUser(String userName);

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    LiveData<User> getLoggedInUser();

    @Query("UPDATE users SET token = :token WHERE userName = :userName")
    void updateToken(String userName, String token);

    @Query("UPDATE users SET isLoggedIn = :isLoggedIn WHERE userName = :userName")
    void updateLoginStatus(String userName, boolean isLoggedIn);
}