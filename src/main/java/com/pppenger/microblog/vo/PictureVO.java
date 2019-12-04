package com.pppenger.microblog.vo;

public class PictureVO {
    String path;
    String compressPath;

    public PictureVO(String path, String compressPath) {
        this.path = path;
        this.compressPath = compressPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }
}
