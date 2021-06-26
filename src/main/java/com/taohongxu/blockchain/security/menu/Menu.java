package com.taohongxu.blockchain.security.menu;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;


@Data
public class Menu {

    private String path;
    private String name;
    private String component;
    private String redirect;
    private List<children> children = new ArrayList<>();
}
