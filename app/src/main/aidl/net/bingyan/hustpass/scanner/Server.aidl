// IQueryManager.aidl
package net.bingyan.hustpass.scanner;
import net.bingyan.hustpass.scanner.model.UserInfo;
import net.bingyan.hustpass.scanner.model.User;

// Declare any non-default types here with import statements

interface Server {

    List<UserInfo> queryUserInfo(String key);

    long addUserInfo(in UserInfo info);

    long attachQRCodeKey(int userId, String key);

    void updateUserInfo(in UserInfo newInfo);

    String getQRCodeKey(int userId);

    boolean login(String email, String pwdMd5);

    long addUser(in User user);
}
