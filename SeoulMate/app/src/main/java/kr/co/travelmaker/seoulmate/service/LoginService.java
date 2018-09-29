package kr.co.travelmaker.seoulmate.service;

import kr.co.travelmaker.seoulmate.model.Member;

public class LoginService {
    private static LoginService curr = null;
    private Member loginMember;

    public static LoginService getInstance() {
        if(curr==null) {
            curr = new LoginService();
        }
        return curr;
    }

    private LoginService() {
    }

    public Member getLoginMember() {
        return loginMember;
    }

    public void setLoginMember(Member loginMember) {
        this.loginMember = loginMember;
    }
}
