package kr.co.travelmaker.seoulmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseNotification {
    private String to;
    private Notification notification = new Notification();
    private Data data = new Data();

    public static class Notification {
        public String fromUid;
        public String title;
        public String text;
    }

    public static class Data {
        public String fromUid;
        public String title;
        public String text;
    }
}
