package com.example.chatapp.Login;

public class ColorGetter {
    private String color_code;

    public ColorGetter() {

    }

    public String getColor(String tag) {
        String tag_value = tag.toLowerCase();

        if (tag_value.equals("helper"))
            this.color_code = "#7AA6D7";
        else if (tag_value.equals("reformer"))
            this.color_code = "#6F9D80";
        else if (tag_value.equals("loyalist"))
            this.color_code = "#F7D967";
        else if (tag_value.equals("peacemaker"))
            this.color_code = "#4C8DAF";
        else if (tag_value.equals("peace maker"))
            this.color_code = "#4C8DAF";
        else if (tag_value.equals("challenger"))
            this.color_code = "#E54141";
        else if (tag_value.equals("leader"))
            this.color_code = "#F8AB81";
        else if (tag_value.equals("enthusiast"))
            this.color_code = "#DC6CA7";
        else if (tag_value.equals("investigator"))
            this.color_code = "#943C3C";
        else if (tag_value.equals("individualist"))
            this.color_code = "#A64DA6";
        else
            this.color_code = "#FFBB86FC";

        return color_code;
    }
}
