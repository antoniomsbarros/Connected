package com.example.simov_project_1180874_1191455__1200606.Entity;

public class Friends {
    private String uuidUserA;
    private String uuiduserb;
    private FriendsStatus status;

    public Friends(String uuidUserA, String uuiduserb, FriendsStatus status) {
        this.uuidUserA = uuidUserA;
        this.uuiduserb = uuiduserb;
        this.status = status;
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
}
