package com.pppenger.microblog.vo;

public class UserVoteSizeRankVO {


    private Long id;
    private Long voteSize;
    private String username;
    private Long rank;
    private String percentage;
    //是否有参加活动的资格
    private String haveQua;
    //如果有资格参加，那秒杀活动是否开始了
    private String canGrab;
    //是否秒杀到了，有资格发布置顶微博
//    private String hadGrab;
    //假设秒杀到了，是否已经发布置顶微博
    private String hadSend;

    public UserVoteSizeRankVO(Long id, Long voteSize, String username, Long rank) {
        this.id = id;
        this.voteSize = voteSize;
        this.username = username;
        this.rank = rank;
        this.percentage = null;
        this.haveQua = null;
        this.canGrab = null;
//        this.hadGrab = null;
        this.hadSend = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVoteSize() {
        return voteSize;
    }

    public void setVoteSize(Long voteSize) {
        this.voteSize = voteSize;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getHaveQua() {
        return haveQua;
    }

    public void setHaveQua(String haveQua) {
        this.haveQua = haveQua;
    }

    public String getCanGrab() {
        return canGrab;
    }

    public void setCanGrab(String canGrab) {
        this.canGrab = canGrab;
    }

    public String getHadSend() {
        return hadSend;
    }

    public void setHadSend(String hadSend) {
        this.hadSend = hadSend;
    }


}
