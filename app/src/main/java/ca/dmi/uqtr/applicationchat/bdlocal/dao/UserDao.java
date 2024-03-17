package ca.dmi.uqtr.applicationchat.bdlocal.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ca.dmi.uqtr.applicationchat.bdlocal.model.UserInfo;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    List<UserInfo> getAllUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<UserInfo> users);

    @Update
    void updateUser(UserInfo user);

    @Delete
    void deleteUser(UserInfo user);

    @Query("DELETE FROM users")
    void deleteAllUsers();
}
