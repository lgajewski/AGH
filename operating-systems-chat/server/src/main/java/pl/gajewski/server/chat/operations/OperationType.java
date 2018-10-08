package pl.gajewski.server.chat.operations;

public enum OperationType {

        LOGIN("LoginOP"),
        LOGOUT("LogoutOP"),
        GET_LOGGED_USERS("LoggedUsersOP"),
        SEND_TO_USER("SendMsgOP"),
        GET_ALL_MSG("GetAllMsgOP"),
        GET_STAT("GetStatOP"),

        C_SUCCESS("q_success"),
        C_FAILURE("q_failure");

        private String aClassName;

        OperationType(String aClassName) {
            this.aClassName = aClassName;
        }

        public String getClassName() {
            return aClassName;
        }
    }