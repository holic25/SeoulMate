package kr.co.travelmaker.seoulmate.model;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class GoogleAccount {
    private static GoogleAccount curr = null;
    private GoogleSignInAccount signInAccount;

    public static GoogleAccount getInstance() {
        if(curr==null) {
            curr = new GoogleAccount();
        }
        return curr;
    }

    private GoogleAccount() {
    }

    public GoogleSignInAccount getSignInAccount() {
        return signInAccount;
    }

    public void setSignInAccount(GoogleSignInAccount signInAccount) {
        this.signInAccount = signInAccount;
    }
}
