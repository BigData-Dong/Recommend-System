package com.recom.realtime;

/*
 * @ClassName: NewClickEvent
 * @projectName RecommendSys
 * @Auther: djr
 * @Date: 2019/8/28 21:21
 * @Description: 点击事件 Bean
 */
public class NewClickEvent {

    private long userId;
    private long itemId;

    public NewClickEvent(){
        this.userId = -1L;
        this.itemId = -1L;
    }
    public NewClickEvent(long userId, long itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

}
