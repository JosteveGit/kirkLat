package glirt.motun.glirt2.Model;

import glirt.motun.glirt2.OneClass;

public class User extends OneClass {
    private String id;
    private String username;
    private String fullname;
    private String imageurl;
    private String bio;
    private String status;
    private String search;
    private String profession;
    private String location;
    private String biztype;
    private String bizname;
    private String bizad;
    private String region;
    private String phone;
    private String lga;
    private String bizimageurl;

    public User(String id, String username, String fullname, String imageurl, String bio, String status, String search, String profession, String location, String biztype, String bizname, String bizad, String region, String phone, String lga, String bizimageurl) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.bio = bio;
        this.status = status;
        this.search = search;
        this.profession = profession;
        this.location = location;
        this.biztype = biztype;
        this.bizname = bizname;
        this.bizad = bizad;
        this.region = region;
        this.phone = phone;
        this.lga = lga;
        this.bizimageurl = bizimageurl;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBiztype() {
        return biztype;
    }

    public void setBiztype(String biztype) {
        this.biztype = biztype;
    }

    public String getBizname() {
        return bizname;
    }

    public void setBizname(String bizname) {
        this.bizname = bizname;
    }

    public String getBizad() {
        return bizad;
    }

    public void setBizad(String bizad) {
        this.bizad = bizad;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public String getBizimageurl() {
        return bizimageurl;
    }

    public void setBizimageurl(String bizimageurl) {
        this.bizimageurl = bizimageurl;
    }

}
