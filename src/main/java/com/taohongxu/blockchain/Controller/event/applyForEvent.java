package com.taohongxu.blockchain.Controller.event;

import com.taohongxu.blockchain.Entity.mailDetail;
import org.springframework.context.ApplicationEvent;


public class applyForEvent extends ApplicationEvent {
    private static final long serialVersionUID = -3604594396507468357L;

    public applyForEvent(mailDetail mailDetail) {
        super(mailDetail);
    }
}
