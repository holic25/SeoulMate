package kr.co.travelmaker.seoulmate.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseChatRoom {
    // 채팅방 유저들
    private Map<String,Boolean> members = new HashMap<>();
    // 채팅방 대화내용
    private Map<String,Comment> comments = new HashMap<>();
    // 채팅방 삭제시간
    private Map<String,Object> removeTime = new HashMap<>();

    public static class Comment {
        public String uid;
        public String message;
        public Object timeStamp;
        public Map<String, Object> readMembers = new HashMap<>();
    }
}
