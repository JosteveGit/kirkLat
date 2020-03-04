package glirt.motun.glirt2.Model;

public class BIZ {
    private String biztype;
    private String bizname;
    private String bizad;
    private String id;
    private String country;
    private String region;
    private String phone;
    private String lga;
    private String bizimageurl;

    public BIZ(String biztype, String bizname, String bizad, String id, String country, String region, String phone, String lga, String bizimageurl) {
        this.biztype = biztype;
        this.bizname = bizname;
        this.bizad = bizad;
        this.id = id;
        this.country = country;
        this.region = region;
        this.phone = phone;
        this.lga = lga;
        this.bizimageurl = bizimageurl;
    }

    public BIZ() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
