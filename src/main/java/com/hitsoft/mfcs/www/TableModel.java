package com.hitsoft.mfcs.www;

import com.hitsoft.mfcs.model.Mfcs;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * User: smeagol
 * Date: 03.09.11
 * Time: 21:05
 */
public class TableModel {

    public int getRowCount() {
        return rows.size();
    }

    public int getColCount() {
        int result = 0;
        if (getRowCount() > 0)
            result = rows.get(0).cells.size();
        return result;
    }

    public static class Cell {
        public boolean isVisible = false;
        public int colSpan = 1;
        public int rowSpan = 1;
        public String style = "";
        public Map<Style.Side, Integer> frame = new HashMap<Style.Side, Integer>();
        public String data = "";
        public SortedMap<Mfcs.Category, Mfcs.Value> coordinate = new TreeMap<Mfcs.Category, Mfcs.Value>();
        public boolean isHeader = false;
        public Mfcs.Category headerCategory = null;
        public boolean isCorner = false;
        public String rawStyle = "";

        public String styleClass() {
            StringBuilder sb = new StringBuilder();
            sb.append(style);
            String frameClass = Style.Clazz.FRAME(frame);
            if (sb.length() > 0 && frameClass.length() > 0)
                sb.append(" ");
            sb.append(frameClass);
            if (isHeader)
                sb.append(" th");
            return sb.toString();
        }

        public void setFrame(int left, int top, int right, int bottom) {
            frame.clear();
            updateFrame(Style.Side.LEFT, left);
            updateFrame(Style.Side.TOP, top);
            updateFrame(Style.Side.RIGHT, right);
            updateFrame(Style.Side.BOTTOM, bottom);
        }

        public void setCoordinate(Mfcs.Category category, Mfcs.Value value) {
            if (!isCorner)
                this.coordinate.put(category, value);
        }

        public void updateFrame(Style.Side side, int value) {
            if (frame.containsKey(side)) {
                frame.put(side, Math.max(value, frame.get(side)));
            } else {
                frame.put(side, value);
            }
        }

        public String getId() {
            String result;
            if (isCorner)
                result = "";
            else if (isHeader && headerCategory == null)
                result = "";
            else if (isHeader)
                result = String.format("h_%s_%s", headerCategory.key, printCoordinateForId());
            else
                result = String.format("c_%s", printCoordinateForId());
            return result;
        }

        private String printCoordinateForId() {
            StringBuilder sb = new StringBuilder();
            boolean isFirst = true;
            for (Mfcs.Category key : coordinate.keySet()) {
                if (!isFirst) {
                    sb.append("-");
                }
                Mfcs.Value val = coordinate.get(key);
                sb.append(val.key);
                isFirst = false;
            }
            return sb.toString();
        }

        public String printCoordinateShort() {
            StringBuilder sb = new StringBuilder().append("(");
            boolean isFirst = true;
            for (Mfcs.Category category : coordinate.keySet()) {
                if (!isFirst) {
                    sb.append(":");
                }
                sb.append(coordinate.get(category).key);
                isFirst = false;
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static class Row {
        public List<Cell> cells = new ArrayList<Cell>();

        public Cell addCell() {
            return addCell(new Cell());
        }

        public Cell addCell(Cell cell) {
            cells.add(cell);
            return cell;
        }

    }

    public String title = "";
    public String styleClass = "";
    public int maxWidth = 1;
    public int leftHeadersCount = 0;
    public int topHeadersCount = 0;
    public int rightHeadersCount = 0;
    public int bottomHeadersCount = 0;

    public List<Row> rows = new ArrayList<Row>();
    public List<Row> legend = new ArrayList<Row>();

    public void setToRequest(HttpServletRequest request) {
        request.getSession().setAttribute(TableModel.class.getName(), this);
    }

    public static TableModel getFromRequest(HttpServletRequest request) {
        return (TableModel) request.getSession().getAttribute(TableModel.class.getName());
    }

    public Row addRow() {
        Row res = new Row();
        rows.add(res);
        return res;
    }

    public Row addLegend() {
        Row res = new Row();
        legend.add(res);
        return res;
    }
}
