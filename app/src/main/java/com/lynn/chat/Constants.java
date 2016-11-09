
package com.lynn.chat;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Constants {
    public static final String SERVER_IP = "192.168.1.104";//100.67.166.217
   // public static final String SERVER_IP = "100.67.166.217";
    public static final int SERVER_PORT = 10010;
    private static final String HTTP_SERVER_IP = "http://"+SERVER_IP+":8080";



    public static class ID{

        public static final int REGISTER = 301;
        public static final int LOGIN = 401;
        public static final int MODIFY_USERINFO = 501;
        public static final int MODIFY_PSD = 601;
        public static final int GET_FRIEND_LIST = 701;
        public static final int SEARCH_USER = 801;
        public static final int REMOVE_FRIEND = 901;
    }
    public static class ExtraData{
        public static final String SENT_PHOTO_URI = "picture_uri";
    }
    public static class UserType{
        public static final String USER_SEND_ADD_FRIEND = "send_add";//set user type when send add friend msg
        public static final String USER_RECEIVE_ADD_FRIEND = "receive_add";//set user type when receive add friend msg
        public static final String USER_SEND_ADD_AGREE = "send_agree";//set user type when send add friend agree msg
        public static final String USER_RECEIVE_ADD_AGREE="receive_agree";//set user type when receive add friend agree msg
    }

    public static class Register{
        public static final String GET_VERIFY_CODE_URL = HTTP_SERVER_IP + "/MyWeb2/GetVerifyCodeServlet";
        public static final String CHECK_VERIFY_CODE_URL = HTTP_SERVER_IP + "/MyWeb2/CheckVerifyCodeServlet";
        public static final String REGISTER_URL = HTTP_SERVER_IP + "/MyWeb2/RegisterServlet";

        public static class RequestParams{

            public static final String USER_ID = "user_id";

            public static final String NAME = "name";
            public static final String GENDER = "gender";
            public static final String PASSWORD = "password";
            public static final String PHOTO = "photo";
            public static final String BIG_PHOTO = "big_photo";
         
        }
        public static class ErrorInfo{
            public static final String CODE_ID_EXISTED = "201";
            public static final String MSG_ID_EXISTED = "phone has been registered";

            public static final String CODE_DISPATCH_FAILED = "202";
            public static final String MSG_DISPATCH_FAILED = "dispatch verifycode failed";

            public static final String CODE_TIMESTAMP_ERROR = "203";
            public static final String MSG_TIMESTAMP_ERROR = "timestamp is wrong";

            public static final String CODE_VERIFY_CODE_ERROR = "204";
            public static final String MSG_VERIFY_CODE_ERROR = "verify_code is wrong";

            public static final String CODE_VERIFY_CODE_EXPIRED = "205";
            public static final String MSG_VERIFY_CODE_EXPIRED = "verify_code is expired";

            public static final String CODE_REGISTER_FAILED = "206";
            public static final String MSG_REGISTER_FAILED = "register user failed";
        }
        private static Map<String, String> errorMap = new HashMap<String, String>();
        static{
            errorMap.put("201", "?????????!");
            errorMap.put("204", "?????!");
            errorMap.put("205", "??????!");
            errorMap.put("206", "????????!");
        }
        /**??????????????<BR/>??????*/
        public static String GetErrorInfo(String code){
            return errorMap.get(code);
        }
    }
    public static class AutoLogin{
        public static final String  ID = "userId";
        public static final String  PWD = "password";
        public static final String GENDER = "gender";
        public static final String PHOTO = "photo";
        public static final String NAME = "name";
    }
    /**????*/
    public static class Login{
        public static final String LOGIN_URL = HTTP_SERVER_IP + "/MyWeb2/LoginServlet";

        public static class RequestParams{
            public static final String USER_ID = "user_id";
            public static final String PASSWORD = "password";
        }

        public static class ResponseParams{
            public static final String USER_ID = "user_id";
            public static final String NAME = "name";
            public static final String GENDER = "gender";
            public static final String PHOTO = "photo";
            public static final String FRIENDSLIST = "frined_list";
        }
        public static class ErrorInfo {
            public static final String CODE_ID_UNRESGISTER = "301";
            public static final String MSG_ID_UNRESGISTER = "userid is unregistered";

            public static final String CODE_PSD_ERROR = "302";
            public static final String MSG_PSD_ERROR = "password id wrong";

        }
        private static Map<String, String> errorMap = new HashMap<String, String>();
        static{
            errorMap.put("301", "?????????!");
            errorMap.put("302", "??????!");
        }
        /**??????????????<BR/>??????*/
        public static String GetErrorInfo(String code){
            return errorMap.get(code);
        }

    }

    /**??????*/
    public static class UserInfo{
        public static final String MODIFY_USERINFO_URL = HTTP_SERVER_IP + "/MyWeb2/ModifyUserInfoServlet";
        public static final String MODIFY_PSD_URL = HTTP_SERVER_IP + "/MyWeb2/ModifyPsdServlet";

        public static class RequestParams{
            public static final String USER_ID = "user_id";
            public static final String NAME = "name";
            public static final String GENDER = "gender";
            public static final String PHOTO = "photo";

            public static final String OLD_PSD = "old_psd";
            public static final String PASSWORD = "password";
        }

        private static Map<String, String> errorMap = new HashMap<String, String>();
        static{
            errorMap.put("402", "????????");
            errorMap.put("403", "??????");
        }
        /**??????????????<BR/>??????*/
        public static String GetErrorInfo(String code){
            return errorMap.get(code);
        }
    }

    /**??????*/
    public static class GetFriendList{
        public static final String GET_FRIEND_LIST_URL = HTTP_SERVER_IP + "/MyWeb2/GetFriendListServlet";
        public static class RequestParams{
            public static final String USER_ID = "user_id";
        }

        public static class JsonKey{
            public static final String NUM = "num";
            public static final String FRIENDS = "friends";
        }
    }

    /**????*/
    public static class AddFriend{
        public static final String SEARCH_USER_URL = HTTP_SERVER_IP + "/MyWeb2/SearchUserServlet";

        public static class RequestParams{
            public static final String USER_ID = "user_id";
            public static final String VERIFY_MSG = "verify_msg";

        }

        public static class ResponseParams{
            public static final String USER_ID = "user_id";
            public static final String NAME = "name";
            public static final String GENDER = "gender";
            public static final String PHOTO = "photo";
        }

        private static Map<String, String> errorMap = new HashMap<String, String>();
        static{
            errorMap.put("501", "????");
        }
        /**??????????????<BR/>??????*/
        public static String GetErrorInfo(String code){
            return errorMap.get(code);
        }
    }

    /**????*/
    public static class RemoveFriend{
        public static final String REMOVE_FRIEND_URL = HTTP_SERVER_IP + "/MyWeb2/RemoveFriendServlet";

        public static class RequestParams{
            public static final String USER_ID = "user_id";
            public static final String FRIEND_ID = "friend_id";
        }
    }

    /**????????*/
    public static class RequestParams{
        public static final String DATA = "data";
        public static final String PHOTO = "photo";
    }

    /**????????*/
    public static class ResponseParams{
        public static final String RES_CODE = "res_code";
        public static final String RES_MSG = "res_msg";
    }
    public static class ResponseInfo {
        public static final String CODE_SUCCESS = "0";
        public static final String MSG_SUCCESS = "success";

        public static final String CODE_DATA_NULL = "101";
        public static final String MSG_DATA_NULL = "encrypt data is null";

        public static final String CODE_PARAM_MISSED = "102";
        public static final String MSG_PARAM_MISSED = "param is missed";

        public static final String CODE_RSA_DECRYPT_ERROR = "103";
        public static final String MSG_RSA_DECRYPT_ERROR = "rsa decrypt error";

        public static final String CODE_SYSTEM_BUSY = "150";
        public static final String MSG_SYSTEM_BUSY = "system busy";
    }


    /**
     * JSON??????key
     */
    public static class JsonFile{
        public static final String ID = "id";
        public static final String PHOTO = "photo";
        public static final String TITLE = "title";
        public static final String NAME = "name";
        public static final String GENDER = "gender";
        public static final String UNREAD = "unread";
        public static final String CONTENT = "content";
        public static final String TIME = "time";

        public static final String SEND = "send";
        public static final String SHOW_TIME = "show_time";
        public static final String RECODE = "recode";
        public static final String MSG = "msg";

        public static final String NEW_FRIENDS = "new_friends";
        public static final String VERIFY_MSG = "verify_msg";
        public static final String IS_AGREED = "is_agree";

        public static final String NUM = "num";
        public static final String FRIENDS = "friends";

    }

    /**Activity?????????*/
    public static class Flags{
        public static final String AUTO_LOGIN = "AUTO_LOGIN";
        public static final String SHOW_PHOTO = "SHOW_PHOTO";
        public static final String FRIEND_INFO = "FRIEND_INFO";
        public static final String CHATTING_FRIEND = "CHATTING_FRIEND";
        public static final String MSG = "MSG";
        public static final String NEW_FRIEND_LIST = "NEW_FRIEND_LIST";
        public static final String ADD_FRIEND_AGREE = "ADD_FRIEND_AGREE";
        public static final String ADD_FRIEND_REQUEST = "ADD_FRIEND_REQUEST";
        public static final String CHATTING_MSG = "CHATTING_MSG";
    }

    /**Broadcast?Action?*/
    public static class Actions{
        public static final String CHATTING_PREFIX = "com.lynn";
        public static final String CHAT_LIST = "com.lynn.chat_list";
        public static final String CONTACTS_LIST = "com.lynn.contacts_list";
        public static final String NEW_FRIEND_TIPS = "com.lynn.new_friend_tips";
        public static final String NEW_FRIEND_LIST = "com.lynn.new_friend_list";
        public static final String GET_BIG_IMAGE = "com.lynn.get_big_image";

    }

    /**????*/
    public static class Gender{
        public static final String MALE = "male";
        public static final String FEMALE = "female";
    }
}
