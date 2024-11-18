/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author reemalbridi
 */
import java.util.List;

// Interface for updating the user list in the UI
public interface UserListUpdateListener {
    void onUserListUpdated(List<String> usernames);
}