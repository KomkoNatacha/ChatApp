package ca.dmi.uqtr.applicationchat.bdlocal.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ca.dmi.uqtr.applicationchat.bdlocal.model.Message;

@Dao
public interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message... messages);
    @Query("SELECT * FROM messages WHERE destinater = :destinater  AND sender_id = :sender_id ORDER BY time DESC")
    LiveData<List<Message>>  getMessagesForContactAndUser(String destinater, String sender_id);

    @Query("SELECT * FROM messages WHERE messageId = :id")
    LiveData<Message> getLiveMessageById(String id);

    @Query("SELECT * FROM messages")
    List<Message> getAll();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessageForUserAndContact( Message message);

    @Query("SELECT * FROM messages ORDER BY time DESC")
    LiveData<List<Message>> getAllMessages();
    @Delete()
    void delete(Message... messages);


}
