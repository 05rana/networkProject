/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rana
 */
import java.util.List;

// Interface for updating the user list in the UI
public interface UserListUpdateListener {
    void onUserListUpdated(List<String> usernames);
}