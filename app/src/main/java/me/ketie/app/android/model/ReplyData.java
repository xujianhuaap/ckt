package me.ketie.app.android.model;

import java.util.ArrayList;

import me.ketie.app.android.model.praise.PraiseItem;
import me.ketie.app.android.model.reply.ReplyItem;

/**
 * Created by android on 15-4-21.
 */
public class ReplyData {
    private ArrayList<ReplyItem> reply = new ArrayList<ReplyItem>();
    private PraiseItem praise = new PraiseItem();

    public ArrayList<ReplyItem> getReply() {
        return reply;
    }

    public void setReply(ArrayList<ReplyItem> reply) {
        this.reply = reply;
    }

    public PraiseItem getPraise() {
        return praise;
    }

    public void setPraise(PraiseItem praise) {
        this.praise = praise;
    }
}
