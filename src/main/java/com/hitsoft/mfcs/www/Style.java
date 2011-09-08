package com.hitsoft.mfcs.www;

import com.hitsoft.mfcs.model.Mfcs;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: smeagol
 * Date: 03.09.11
 * Time: 19:52
 */
public class Style {

    public static enum Side {
        LEFT("l", "left"),
        TOP("t", "top"),
        RIGHT("r", "right"),
        BOTTOM("b", "bottom");

        private String id;
        private String key;

        Side(String id, String key) {
            this.id = id;
            this.key = key;
        }

        public String formatId(String fmt) {
            return String.format(fmt, id);
        }

        public String formatKey(String fmt) {
            return String.format(fmt, key);
        }
    }

    public static class Key {
        public static final String BORDER_FMT = "border-%s";
    }

    public static class StyleRec {
        public String name;
        public Map<String, String> values = new HashMap<String, String>();

        StyleRec(String name) {
            this.name = name;
        }

        public static StyleRec applyFrame(Side side, int width) {
            StyleRec res = new StyleRec("." + Clazz.FRAME(side, width));
            res.values.put(side.formatKey(Key.BORDER_FMT), String.format("solid %dpx black", width));
            return res;
        }

        public static StyleRec applyBackroundColorForId(String id, SortedMap<Mfcs.Category, Mfcs.Value> coordinate) {
            StyleRec res = new StyleRec(String.format("#%s", id));
            String color = renderCoordinateColor(coordinate);
            if (color == null)
                res = null;
            else
                res.values.put("background", color);
            return res;
        }

        private static String renderCoordinateColor(SortedMap<Mfcs.Category, Mfcs.Value> coordinate) {
            String result;
            Color res = null;
            for (Mfcs.Category key : coordinate.keySet()) {
                if (res == null)
                    res = coordinate.get(key).color;
                else
                    res = mixColors(res, coordinate.get(key).color);
            }
            if (res == null)
                result = null;
            else
                result = String.format("rgb(%d,%d,%d)", res.getRed(), res.getGreen(), res.getBlue());
            return result;
        }

        private static Color mixColors(Color c1, Color c2) {
            Color result;
            if (c1 == null)
                result = c2;
            else if (c2 == null)
                result = c1;
            else {
                float x = 0.5f;
                float rgb1[] = c1.getRGBComponents(null);
                float rgb2[] = c2.getRGBComponents(null);

                float r = mixComponent(rgb1[0], rgb2[0], x);
                float g = mixComponent(rgb1[1], rgb2[1], x);
                float b = mixComponent(rgb1[2], rgb2[2], x);
                result = new Color(r, g, b);
            }
            return result;
        }

        private static float mixComponent(float c1, float c2, float x) {
            float res;
            if (is1(c1))
                res = c2;
            else if (is1(c2))
                res = c1;
            else
                res = c1 * x + c2 * (1f - x);
            return res;
        }

        private static boolean is1(float value) {
            return 255 == Math.round(value * 255);
        }
    }

    public static List<StyleRec> process(HttpServletRequest request) {
        List<StyleRec> res = new ArrayList<StyleRec>();
        TableModel model = TableModel.getFromRequest(request);

        for (int i = 1; i <= model.maxWidth; i++) {
            for (Side side : Side.values()) {
                res.add(StyleRec.applyFrame(side, i));
            }
        }

        for (TableModel.Row row : model.rows) {
            for (TableModel.Cell cell : row.cells) {
                String id = cell.getId();
                if (!id.isEmpty()) {
                    StyleRec rec = StyleRec.applyBackroundColorForId(cell.getId(), cell.coordinate);
                    if (rec != null)
                        res.add(rec);
                }
            }
        }

        return res;
    }

    public static class Clazz {
        public static String FRAME(Side side, int width) {
            return side.formatId(String.format("f%d%%s", width));
        }

        public static String FRAME(Map<Side, Integer> frame) {
            StringBuilder sb = new StringBuilder();
            for (Side side : frame.keySet()) {
                if (sb.length() > 0)
                    sb.append(" ");
                sb.append(FRAME(side, frame.get(side)));
            }
            return sb.toString();
        }
    }
}
