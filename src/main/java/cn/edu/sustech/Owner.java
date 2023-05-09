package cn.edu.sustech;

public class Owner {
    private String profile_image;
    private int account_id;
    private String user_type;
    private int user_id;
    private String link;
    private String display_name;
    private int reputation;
    Owner(String profile_image, int account_id, String user_type, int user_id, String link, String display_name, int reputation) {
        this.profile_image = profile_image;
        this.account_id = account_id;
        this.user_type = user_type;
        this.user_id = user_id;
        this.link = link;
        this.display_name = display_name;
        this.reputation = reputation;
    }
    public String getProfile_image() {
        return profile_image;
    }
    public int getAccount_id() {
        return account_id;
    }
    public String getUser_type() {
        return user_type;
    }
    public int getUser_id() {
        return user_id;
    }
    public String getLink() {
        return link;
    }
    public String getDisplay_name() {
        return display_name;
    }
    public int getReputation() {
        return reputation;
    }
    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }
    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
    public void setReputation(int reputation) {
        this.reputation = reputation;
    }
}
