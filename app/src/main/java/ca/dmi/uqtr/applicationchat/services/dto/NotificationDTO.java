package ca.dmi.uqtr.applicationchat.services.dto;



public class NotificationDTO {


    public String icon;
    public long time;
    public String title;
    public String body;
    public String issuer;

    public NotificationDTO(String title, String body, String issuer, String icon, long time) {
        this.title = title;
        this.body = body;
        this.issuer = issuer;
        this.icon = icon;
        this.time = time;
    }
}
