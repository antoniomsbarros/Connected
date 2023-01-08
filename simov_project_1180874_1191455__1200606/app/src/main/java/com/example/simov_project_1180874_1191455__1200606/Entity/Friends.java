package com.example.simov_project_1180874_1191455__1200606.Entity;

import java.util.Objects;

public class Friends {
    private String uuidUserA;
    private String uuiduserb;
    private FriendsStatus status;
    private  String uidUseremail;
    public Friends() {
    }

    public Friends(String uuidUserA, String uuiduserb,String uidUseremail, FriendsStatus status) {
        this.uuidUserA = uuidUserA;
        this.uuiduserb = uuiduserb;
        this.status = status;
        this.uidUseremail=uidUseremail;
    }

    public String getUuidUserA() {
        return uuidUserA;
    }

    public void setUuidUserA(String uuidUserA) {
        this.uuidUserA = uuidUserA;
    }

    public String getUuiduserb() {
        return uuiduserb;
    }

    public void setUuiduserb(String uuiduserb) {
        this.uuiduserb = uuiduserb;
    }

    public FriendsStatus getStatus() {
        return status;
    }

    public void setStatus(FriendsStatus status) {
        this.status = status;
    }

    private boolean isFriend(String uuidUser) {
        if (!uuidUserA.equals(uuidUser) && !uuiduserb.equals(uuidUser)) return false;
        return status.equals(FriendsStatus.accept);
    }

    public String getUidUseremail() {
        return uidUseremail;
    }

    public void setUidUseremail(String uidUseremail) {
        this.uidUseremail = uidUseremail;
    }

    public String getFriend(String uuidUser) {
        if (!isFriend(uuidUser)) return null;
        if (uuidUserA.equals(uuidUser)) return uuiduserb;
        return uuidUserA;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friends friends = (Friends) o;
        return uuidUserA.equals(friends.uuidUserA)
                && uuiduserb.equals(friends.uuiduserb)
                && status == friends.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuidUserA, uuiduserb, status);
    }
}
