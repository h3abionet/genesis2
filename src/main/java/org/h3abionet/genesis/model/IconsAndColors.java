package org.h3abionet.genesis.model;

import java.io.Serializable;
import java.util.HashMap;

public class IconsAndColors implements Serializable {

    private static final long serialVersionUID = 2L;

    public IconsAndColors() {
        setIconTypes();

    }

    private HashMap<String, String> iconTypes = new HashMap<>();

    private String tree ="M 13.00,24.0 C 13.00,24.00 11.00,24.00 11.00,24.00 11.00,24.00 11.00,19.00 11.00,19.00 "+
            "11.00,19.00 4.00,19.00 4.00,19.00 4.00,19.00 8.00,13.00 8.00,13.00 8.00,13.00 5.00,13.00 5.00,13.00 5.00,13.00 9.00,7.00 9.00,7.00 9.00,7.00 7.00,7.00 7.00,7.00 "+
            "7.00,7.00 12.00,0.00 12.00,0.00 12.00,0.00 17.00,7.00 17.00,7.00 17.00,7.00 15.00,7.00 15.00,7.00 15.00,7.00 19.00,13.00 19.00,13.00 "+
            "19.00,13.00 16.00,13.00 16.00,13.00 16.00,13.00 20.00,19.00 20.00,19.00 20.00,19.00 13.00,19.00 13.00,19.00 13.00,19.00 13.00,24.00 13.00,24.00 Z";

    private String circle = "M 24.00,12.00 C 24.00,18.63 18.63,24.00 12.00,24.00 5.37,24.00 0.00,18.63 0.00,12.00 0.00,5.37 5.37,0.00 12.00,0.00 18.63,0.00 24.00,5.37 24.00,12.00 Z";

    private String heart = "M 6.28,3.00  C 9.52,7.97 9.77,8.72 12.06,1.45  14.53,-3.57 17.79,-3.57 14.12,1.45  18.39,2.76 18.39,5.63 14.12,4.89  " +
            "9.38,12.75 4.12,17.89 8.86,-0.26  -1.14,-9.82 -1.14,-13.26 8.86,-2.93 10.83,-7.12 13.14,-7.12 13.14,-7.12 13.14,-7.12 6.28,3.00 6.28,3.00 Z  " +
            "M 13.14,-9.12 C 9.96,-9.12 6.86,-6.94 6.86,-2.93 6.86,1.73 12.43,6.50 18.86,12.88  25.29,6.50 30.86,1.73 30.86,-2.93 30.86,-6.94 " +
            "27.76,-9.11 24.59,-9.11 22.38,-9.11 20.14,-8.07 18.86,-5.88 17.57,-8.08 15.34,-9.12 13.14,-9.12 13.14,-9.12 13.14,-9.12 13.14,-9.12 Z";

    private String hexagon = "M 12.00,0.00 C 12.00,0.00 1.00,6.00 1.00,6.00 1.00,6.00 1.00,18.00 1.00,18.00 1.00,18.00 12.00,24.00 12.00,24.00 12.00,24.00 23.00,18.00 23.00,18.00 "+
            "23.00,18.00 23.00,6.00 23.00,6.00 23.00,6.00 12.00,0.00 12.00,0.00 Z  M 3.00,16.81 C 3.00,16.81 3.00,7.19 3.00,7.19 3.00,7.19 12.00,2.28 12.00,2.28 "+
            "12.00,2.28 21.00,7.19 21.00,7.19 21.00,7.19 21.00,16.81 21.00,16.81 21.00,16.81 12.00,21.72 12.00,21.72 12.00,21.72 3.00,16.81 3.00,16.81 Z";

    private String egg = "M 12.00,0.00 C 7.50,0.00 3.00,7.11 3.00,13.58 3.00,19.31 6.42,24.00 12.00,24.00 17.58,24.00 21.00,19.31 21.00,13.58 21.00,7.11 16.50,0.00 12.00,0.00 Z";

    private String diamond ="M 18.00,1.00 C 18.00,1.00 6.08,1.00 6.08,1.00 6.08,1.00 0.00,8.00 0.00,8.00 0.00,8.00 12.00,23.00 12.00,23.00 12.00,23.00 24.00,8.08 24.00,8.08 "+
            "24.00,8.08 18.00,1.00 18.00,1.00 Z M 6.33,9.00 C 6.33,9.00 9.56,16.75 9.56,16.75 9.56,16.75 3.36,9.00 3.36,9.00 3.36,9.00 6.33,9.00 6.33,9.00 "+
            "6.33,9.00 6.33,9.00 6.33,9.00 Z  M 15.50,9.00 C 15.50,9.00 12.00,17.40 12.00,17.40 12.00,17.40 8.50,9.00 8.50,9.00 8.50,9.00 15.50,9.00 15.50,9.00 "+
            "15.50,9.00 15.50,9.00 15.50,9.00 Z  M 9.14,7.00 C 9.14,7.00 12.00,3.56 12.00,3.56 12.00,3.56 14.87,7.00 14.87,7.00 14.87,7.00 9.14,7.00 9.14,7.00 Z  M 17.67,9.00 "+
    "C 17.67,9.00 20.70,9.00 20.70,9.00 20.70,9.00 14.41,16.81 14.41,16.81 14.41,16.81 17.67,9.00 17.67,9.00 17.67,9.00 17.67,9.00 17.67,9.00 Z  M 20.46,7.00 "+
    "C 20.46,7.00 17.47,7.00 17.47,7.00 17.47,7.00 14.13,3.00 14.13,3.00 14.13,3.00 17.07,3.00 17.07,3.00 17.07,3.00 20.46,7.00 20.46,7.00 Z  M 7.00,3.00 "+
    "C 7.00,3.00 9.86,3.00 9.86,3.00 9.86,3.00 6.53,7.00 6.53,7.00 6.53,7.00 3.52,7.00 3.52,7.00 3.52,7.00 7.00,3.00 7.00,3.00 Z";

    private String drop = "M 12.00,0.00 C 7.13,7.20 4.00,11.70 4.00,16.07 4.00,20.45 7.58,24.00 12.00,24.00 16.42,24.00 20.00,20.45 20.00,16.07 20.00,11.70 16.87,7.20 12.00,0.00 Z";

    private String cursor = "M 3.00,0.00 C 3.00,0.00 21.00,15.00 21.00,15.00 21.00,15.00 9.92,15.00 9.92,15.00 9.92,15.00 3.00,24.00 3.00,24.00 3.00,24.00 3.00,0.00 3.00,0.00 Z";

    private String location = "M 12.00,0.00 C 7.80,0.00 4.00,3.40 4.00,7.60 4.00,11.80 7.47,16.81 12.00,24.00 16.53,16.81 20.00,11.80 20.00,7.60 20.00,3.40 16.20,0.00 12.00,0.00 " +
            "12.00,0.00 12.00,0.00 12.00,0.00 Z M 12.00,11.00 C 10.34,11.00 9.00,9.66 9.00,8.00 9.00,6.34 10.34,5.00 12.00,5.00 13.66,5.00 15.00,6.34 15.00,8.00 15.00,9.66 13.66,11.00 12.00,11.00 Z";

    private String shield = "M 12.00,0.00 C 8.56,-0.01 5.07,1.22 3.00,3.00 3.00,3.00 3.00,14.54 3.00,14.54 3.00,19.14 6.20,20.34 12.00,24.00 17.80,20.34 21.00,19.14 21.00,14.54 21.00,14.54 21.00,3.00 21.00,3.00 18.93,1.22 15.44,-0.01 12.00,0.00 Z";

    private String shield2 ="M 1.00,4.00 C 1.00,12.58 6.07,20.10 12.00,24.00 17.93,20.10 23.00,12.58 23.00,4.00 20.17,1.67 15.85,0.00 12.00,0.00 8.16,0.00 3.83,1.67 1.00,4.00 Z";

    private String user = "M 19.00,7.00 C 19.00,10.87 15.87,14.00 12.00,14.00 8.13,14.00 5.00,10.87 5.00,7.00 5.00,3.13 8.13,0.00 " +
            "12.00,0.00 15.87,0.00 19.00,3.13 19.00,7.00 19.00,7.00 " +
            "19.00,7.00 19.00,7.00 Z  M 17.40,14.18 C 15.90,15.32 14.03,16.00 12.00,16.00 9.97,16.00 8.10,15.32 6.59,14.18 " +
            "2.52,15.97 0.00,21.56 0.00,24.00 0.00,24.00 24.00,24.00 " +
            "24.00,24.00 24.00,21.58 21.40,15.99 17.40,14.18 17.40,14.18 17.40,14.18 17.40,14.18 Z";

    private String sharp_arrow = "M 16.00,9.00 C 16.00,9.00 16.00,5.00 16.00,5.00 16.00,5.00 24.00,13.00 24.00,13.00 24.00,13.00 " +
            "16.00,21.00 16.00,21.00 16.00,21.00 16.00,17.00 16.00,17.00 16.00,17.00 2.72,14.86 0.00,3.00 5.80,9.21 16.00,9.00 16.00,9.00 Z";

    private String folder = "M 11.00,5.00 C 9.37,5.00 8.70,3.94 7.00,2.00 7.00,2.00 0.00,2.00 0.00,2.00 0.00,2.00 0.00,22.00 0.00,22.00 0.00,22.00 24.00,22.00 24.00,22.00 24.00,22.00 24.00,5.00 24.00,5.00 24.00,5.00 11.00,5.00 11.00,5.00 Z";

    private String round_triangle = "M 23.68,18.52 C 24.59,20.04 23.49,21.99 21.71,21.99 21.71,21.99 2.30,21.99 2.30,21.99 0.51,21.99 " +
            "-0.58,20.04 0.33,18.52 0.33,18.52 10.04,2.34 10.04,2.34 9.15,0.86 13.08,0.86 13.97,2.34 13.97,2.34 23.68,18.52 23.68,18.52 23.68,18.52 23.68,18.52 23.68,18.52 Z";

    private String round_square = "M 24.00,5.00 C 24.00,2.24 21.76,0.00 19.00,0.00 19.00,0.00 5.00,0.00 5.00,0.00 2.24,0.00 0.00,2.24 0.00,5.00 0.00,5.00 0.00,19.00 " +
            "0.00,19.00 0.00,21.76 2.24,24.00 5.00,24.00 5.00,24.00 19.00,24.00 19.00,24.00 21.76,24.00 24.00,21.76 24.00,19.00 24.00,19.00 24.00,5.00 24.00,5.00 Z";

    private String arrow3 = "M 17.00,5.00 C 17.00,5.00 0.00,5.00 0.00,5.00 0.00,5.00 0.00,19.00 0.00,19.00 0.00,19.00 17.00,19.00 " +
            "17.00,19.00 17.00,19.00 24.00,12.00 24.00,12.00 24.00,12.00 17.00,5.00 17.00,5.00 Z";

    private String arrow1 = "M 12.00,4.00 C 12.00,4.00 12.00,0.00 12.00,0.00 12.00,0.00 24.00,12.00 24.00,12.00 24.00,12.00 12.00,24.00 12.00,24.00 12.00,24.00 " +
            "12.00,20.00 12.00,20.00 12.00,20.00 0.00,20.00 0.00,20.00 0.00,20.00 0.00,4.00 0.00,4.00 0.00,4.00 12.00,4.00 12.00,4.00 Z";

    private String speech = "M 0.00,23.00 C 0.97,21.09 2.05,18.46 1.99,16.63 0.68,15.07 -0.05,13.06 -0.05,11.01 -0.05,5.23 5.61,1.00 11.95,1.00 " +
            "18.25,1.00 23.95,5.20 23.95,11.01 23.95,17.06 17.21,22.71 7.98,20.47 6.30,21.49 2.60,22.53 -0.00,23.00 -0.00,23.00 0.00,23.00 0.00,23.00 Z";

    private String star = "M 0.0 10.0 L 3.0 3.0 L 10.0 0.0 L 3.0 -3.0 L 0.0 -10.0 L -3.0 -3.0 L -10.0 0.0 L -3.0 3.0 Z";

    private String arrow = "M0 -3.5 v7 l 4 -3.5z";
    private String kite = "M5,0 L10,9 L5,18 L0,9 Z";
    private String cross = "M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z";
    private String rectangle = "M 20.0 20.0  v24.0 h 10.0  v-24   Z";
    private String tick = "M0,4 L2,4 L4,8 L7,0 L9,0 L4,11 Z";
    private String triangle = "M 2 2 L 6 2 L 4 6 z";
    private String square = "M 10 10 H 90 V 90 H 10 L 10 10";


    private void setIconTypes() {
        iconTypes.put(arrow, "Arrow");
        iconTypes.put(circle, "Circle");
        iconTypes.put(cross, "Cross");
        iconTypes.put(drop, "Drop");
        iconTypes.put(egg, "Egg");
        iconTypes.put(hexagon, "Hexagon");
        iconTypes.put(diamond, "Diamond");
        iconTypes.put(location, "Location");
        iconTypes.put(round_triangle, "Pyramid");
        iconTypes.put(rectangle, "Rectangle");
        iconTypes.put(arrow1, "R Arrow");
        iconTypes.put(round_square, "Round_square");
        iconTypes.put(shield, "Shield");
        iconTypes.put(shield2, "Shield2");
        iconTypes.put(square, "Square");
        iconTypes.put(star, "Star");
        iconTypes.put(sharp_arrow, "Sharp_arrow");
        iconTypes.put(tree, "Tree");
        iconTypes.put(tick, "Tick");
        iconTypes.put(triangle, "Triangle");

//        iconTypes.put(heart, "Heart");
//        iconTypes.put(kite, "kite");
//        iconTypes.put(cursor, "cursor");
//        iconTypes.put(user, "user");
//        iconTypes.put(folder, "folder");
//        iconTypes.put(speech, "speech");
    }

    private String[] listOfIcons = new String[]{tree, circle, hexagon, egg, rectangle, triangle, square, diamond, drop, cursor, heart, location, shield, shield2, user,
            sharp_arrow, folder, round_square, round_triangle, arrow3, arrow, arrow1, speech, star, kite, cross, tick, // repeat same icons
            tree, circle, hexagon, egg, rectangle, triangle, square, diamond, drop, cursor, location, shield, shield2, user,
            sharp_arrow, folder, round_square, round_triangle, arrow3, arrow, arrow1, speech, star, kite, cross, tick};

    private String[] listOfColors = new String[]{
            "#0000FF", // blue
            "#FFFF00", // yellow
            "#FF0000", // red
            "#800080", // purple
            "#00FF00", // green
            "#F79501", // brown
            "#FA13F3", // magenta
            "#58E7F2", // cyan
            "#800080", // Fresh Eggplant
            "#004C4C", // Sherpa Blue
            "#ff00ff", // Magenta
            "#593737", // Congo Brown
            "#396413", // Dell
            "#BB3385", // Medium Red Violet
            "#534491", //Victoria
            "#773F1A", // walnut
            "#5A503F", // Kabul
            "#7B6608", // Yukon Gold
            "#4D5328", // Woodland
            "#D5F014", // Las Palmas
            "#40826D", // Viridian
            "#240A40", // Violet
            "#49170C", // Van Cleef
            "#D84437", // Valencia
            "#044259", // Teal Blue
            "#302A0F", // Woodrush
            // repeat shuffled colors
            "#40826D", // Viridian
            "#004C4C", // Sherpa Blue
            "#000080", // Navy Blue
            "#ff00ff", // Magenta
            "#240A40", // Violet
            "#860061", // Fresh Eggplant
            "#D84437", // Valencia
            "#008080", // teal
            "#773F1A", // walnut
            "#593737", // Congo Brown
            "#07AB99", // Niagara
            "#800080", // Fresh Eggplant
            "#534491", //Victoria
            "#ff8000", // Flush Orange
            "#D5F014", // Las Palmas
            "#49170C", // Van Cleef
            "#808000", // Olive
            "#4D5328", // Woodland
            "#396413", // Dell
            "#BB3385", // Medium Red Violet
            "#7B6608", // Yukon Gold
            "#008000", // Japanese Laurel
            "#302A0F", // Woodrush
            "#5A503F", // Kabul
            "#800000", // maroon
            "#044259", // Teal Blue
            "#7B6608", // Yukon Gold
    };

    public String[] getListOfIcons() {
        return listOfIcons;
    }

    public String[] getListOfColors() {
        return listOfColors;
    }

    public HashMap<String, String> getIconTypes() {
        return iconTypes;
    }

}
