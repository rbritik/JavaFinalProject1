class User {
    private String user;
    private String pass;
    public User(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }
    // Returns username
    public String getUser() {
        return user;
    }    

    // Returns password
    public String getPass() {
        return pass;
    }
}
