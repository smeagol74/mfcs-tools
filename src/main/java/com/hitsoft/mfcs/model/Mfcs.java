package com.hitsoft.mfcs.model;

import com.hitsoft.mfcs.www.Style;
import com.hitsoft.mfcs.www.TableModel;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: smeagol
 * Date: 03.09.11
 * Time: 22:27
 */
public class Mfcs {

    public Category addCategory(String key, String name, @org.jetbrains.annotations.Nullable Color color) {
        Category res = new Category(categories.size(), key, name, color);
        categories.add(res);
        return res;
    }

    public static class Value {
        public String key;
        public String value;
        public Color color = new Color(0xffffff);

        public Value(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static class Category implements Comparable<Category> {
        public int index;
        public String key;
        public String name;
        private List<Value> values = new ArrayList<Value>();
        public int weight = 1;
        public Color baseColor = null;

        public Category(int index, String key, String name, Color baseColor) {
            this.index = index;
            this.key = key;
            this.name = name;
            this.baseColor = baseColor;
        }

        public Value addValue(String value) {
            return addValue(value, value);
        }

        public Value addValue(String key, String value) {
            Value res = new Value(key, value);
            values.add(res);
            int i = 0;
            for (Value val : values) {
                val.color = getValueColor(i, values.size());
                i++;
            }
            return res;
        }

        private Color getValueColor(int i, int size) {
            Color result;
            if (baseColor != null) {
                float rgb[] = baseColor.getRGBComponents(null);
                float r = getValueColorComponent(rgb[0], i, size);
                float g = getValueColorComponent(rgb[1], i, size);
                float b = getValueColorComponent(rgb[2], i, size);
                result = new Color(r, g, b);
            } else
                result = null;
            return result;
        }

        private float getValueColorComponent(float c, int i, int size) {
            float res;
            if (size > 1)
                res = 1f - ((1f - c) * i / (size - 1f));
            else
                res = 1f;
            return res;
        }

        public int compareTo(Category o) {
            return index - o.index;
        }
    }

    private String title;
    private List<Category> categories = new ArrayList<Category>();

    public Mfcs(String title) {
        this.title = title;
    }

    private enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    private class Layer {
        Orientation orientation;
        Category category;
        ArrayList<TableModel.Cell> cells = new ArrayList<TableModel.Cell>();

        public Layer(Orientation orientation, Category category, int dups) {
            this.orientation = orientation;
            this.category = category;
            for (int i = 0; i < dups; i++) {
                for (Value value : category.values) {
                    TableModel.Cell cell = new TableModel.Cell();
                    cell.colSpan = 1;
                    cell.rowSpan = 1;
                    cell.data = value.value;
                    cell.isVisible = true;
                    cell.isHeader = true;
                    cell.headerCategory = category;
                    cell.setCoordinate(category, value);
                    cells.add(cell);
                }
            }
        }

        public void updateSpan(int size) {
            category.weight = category.weight + 1;
            for (TableModel.Cell cell : cells) {
                switch (orientation) {
                    case HORIZONTAL:
                        cell.colSpan = cell.colSpan * size;
                        break;
                    case VERTICAL:
                        cell.rowSpan = cell.rowSpan * size;
                        break;
                }
            }
        }

        public int getSpan() {
            int res = 1;
            if (!cells.isEmpty()) {
                switch (orientation) {
                    case HORIZONTAL:
                        res = cells.get(0).colSpan;
                        break;
                    case VERTICAL:
                        res = cells.get(0).rowSpan;
                        break;
                }
            }
            return res;
        }
    }

    private class Header {

        private class Index {
            private static final int BOTTOM = 0;
            private static final int LEFT = 1;
            private static final int TOP = 2;
            private static final int RIGHT = 3;
        }

        Orientation orientation;
        ArrayList<Layer> layers = new ArrayList<Layer>();

        Header(Orientation orientation) {
            this.orientation = orientation;
        }

        Layer addLayer(Category category, Header opposite) {
            int dups = getCellsCount();
            if (dups == 1)
                dups = opposite.getCellsCount();
            Layer res = new Layer(orientation, category, dups);
            updateSpan(category);
            opposite.updateSpan(category);
            layers.add(res);
            return res;
        }

        private int getCellsCount() {
            int res;
            if (layers.size() > 0) {
                Layer lastLayer = layers.get(layers.size() - 1);
                res = lastLayer.cells.size() * lastLayer.getSpan();
            } else
                res = 1;
            return res;
        }

        private void updateSpan(Category category) {
            for (Layer layer : layers) {
                layer.updateSpan(category.values.size());
            }
        }
    }

    public void renderToRequest(HttpServletRequest request) {
        TableModel model = new TableModel();

        int rows = 1;
        int cols = 1;

        boolean hor = true;
        for (Category cat : categories) {
            if (hor)
                cols = cols * cat.values.size();
            else
                rows = rows * cat.values.size();
            hor = !hor;
        }

        List<Header> headers = setupHeaders();
        updateHeadersSizes(headers, model);

        model.title = this.title;
        addTopHeaders(headers.get(Header.Index.TOP), model);
        addMiddleCells(headers.get(Header.Index.LEFT),
                rows,
                cols,
                headers.get(Header.Index.RIGHT),
                model);
        addBottomHeaders(headers.get(Header.Index.BOTTOM), model);

        updateFrames(headers, model);

        updateCoordinates(headers, model);

        updateCellsData(model);

        model.setToRequest(request);
    }

    private List<Header> setupHeaders() {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new Header(Orientation.HORIZONTAL));
        headers.add(new Header(Orientation.VERTICAL));
        headers.add(new Header(Orientation.HORIZONTAL));
        headers.add(new Header(Orientation.VERTICAL));

        int hdrIdx = 0;
        for (Category category : categories) {
            int oppositeIdx = 0;
            switch (hdrIdx) {
                case Header.Index.BOTTOM:
                    oppositeIdx = Header.Index.TOP;
                    break;
                case Header.Index.LEFT:
                    oppositeIdx = Header.Index.RIGHT;
                    break;
                case Header.Index.TOP:
                    oppositeIdx = Header.Index.BOTTOM;
                    break;
                case Header.Index.RIGHT:
                    oppositeIdx = Header.Index.LEFT;
                    break;
            }
            headers.get(hdrIdx).addLayer(category, headers.get(oppositeIdx));
            hdrIdx++;
            if (hdrIdx > 3)
                hdrIdx = 0;
        }
        return headers;
    }

    private void updateCellsData(TableModel model) {
        for (TableModel.Row row : model.rows) {
            for (TableModel.Cell cell : row.cells) {
                if (cell.isCorner)
                    cell.data = "";
                else if (!cell.isHeader)
                    cell.data = "";
//                if (cell.isCorner)
//                    cell.data = "";
//                else if (cell.isHeader)
//                    cell.data = String.format("%s %s", cell.data, cell.printCoordinateShort());
//                else
//                    cell.data = cell.printCoordinateShort();
            }
        }

    }

    private void updateCoordinates(List<Header> headers, TableModel model) {
        for (int i = categories.size() - 1; i >= 0; i--) {
            updateCategoryCoordinates(categories.get(i), headers, model);
        }
    }

    private void updateCategoryCoordinates(Category category, List<Header> headers, TableModel model) {
        int hdrIdx = 0;
        for (Header header : headers) {
            int idx = 0;
            for (Layer layer : header.layers) {
                if (layer.category == category) {
                    switch (hdrIdx) {
                        case Header.Index.LEFT:
                            updateLeftCategoryCoordinates(category, layer, model);
                            break;
                        case Header.Index.TOP:
                            updateTopCategoryCoordinates(category, layer, model);
                            break;
                        case Header.Index.RIGHT:
                            updateRightCategoryCoordinates(category, layer, model);
                            break;
                        case Header.Index.BOTTOM:
                            updateBottomCategoryCoordinates(category, layer, idx, model);
                            break;
                    }
                }
                idx++;
            }
            hdrIdx++;
        }
    }

    private void updateBottomCategoryCoordinates(Category category, Layer layer, int layerIdx, TableModel model) {
        updateHorizontalCategoryCoordinates(category, layer, model);
    }

    private void updateRightCategoryCoordinates(Category category, Layer layer, TableModel model) {
        updateVerticalCategoryCoordinates(category, layer, model, false);
    }

    private void updateTopCategoryCoordinates(Category category, Layer layer, TableModel model) {
        updateHorizontalCategoryCoordinates(category, layer, model);
    }

    private void updateHorizontalCategoryCoordinates(Category category, Layer layer, TableModel model) {
        int col = model.leftHeadersCount;
        for (TableModel.Cell cell : layer.cells) {
            for (int c = 0; c < cell.colSpan; c++) {
                updateColCoordinates(model, col, category, cell.coordinate.get(category));
                col++;
            }
        }
    }

    private void updateColCoordinates(TableModel model, int col, Category category, Value value) {
        for (TableModel.Row row : model.rows) {
            TableModel.Cell cell = row.cells.get(col);
            if (cell.isHeader) {
                if (category.index < cell.headerCategory.index)
                    cell.setCoordinate(category, value);
            } else
                cell.setCoordinate(category, value);
        }
    }

    private void updateLeftCategoryCoordinates(Category category, Layer layer, TableModel model) {
        updateVerticalCategoryCoordinates(category, layer, model, true);
    }

    private void updateVerticalCategoryCoordinates(Category category, Layer layer, TableModel model, boolean reverse) {
        int row;
        if (reverse)
            row = model.getRowCount() - model.bottomHeadersCount - 1;
        else
            row = model.topHeadersCount;
        for (TableModel.Cell cell : layer.cells) {
            for (int r = 0; r < cell.rowSpan; r++) {
                updateRowCoordinates(model, row, category, cell.coordinate.get(category));
                if (reverse)
                    row--;
                else
                    row++;
            }
        }
    }

    private void updateRowCoordinates(TableModel model, int row, Category category, Value value) {
        for (TableModel.Cell cell : model.rows.get(row).cells) {
            if (cell.isHeader) {
                if (category.index < cell.headerCategory.index)
                    cell.setCoordinate(category, value);
            } else
                cell.setCoordinate(category, value);
        }
    }

    private void updateHeadersSizes(List<Header> headers, TableModel model) {
        int hdrIdx = 0;
        model.leftHeadersCount = 0;
        model.topHeadersCount = 0;
        model.rightHeadersCount = 0;
        model.bottomHeadersCount = 0;
        for (Header header : headers) {
            switch (hdrIdx) {
                case Header.Index.LEFT:
                    model.leftHeadersCount = header.layers.size();
                    break;
                case Header.Index.TOP:
                    model.topHeadersCount = header.layers.size();
                    break;
                case Header.Index.RIGHT:
                    model.rightHeadersCount = header.layers.size();
                    break;
                case Header.Index.BOTTOM:
                    model.bottomHeadersCount = header.layers.size();
                    break;
            }
            hdrIdx++;
        }
    }

    private void updateFrames(List<Header> headers, TableModel model) {
        resetTableFrames(model);
        updateWholeTableFrame(model);
        for (Category category : categories) {
            updateCategoryFrames(category, headers, model);
        }
    }

    private void updateCategoryFrames(Category category, List<Header> headers, TableModel model) {
        int hdrIdx = 0;
        for (Header header : headers) {
            int idx = 0;
            for (Layer layer : header.layers) {
                if (layer.category == category) {
                    switch (hdrIdx) {
                        case Header.Index.LEFT:
                            updateLeftCategoryFrames(category, layer, idx, model);
                            break;
                        case Header.Index.TOP:
                            updateTopCategoryFrames(category, layer, idx, model);
                            break;
                        case Header.Index.RIGHT:
                            updateRightCategoryFrames(category, layer, idx, model);
                            break;
                        case Header.Index.BOTTOM:
                            updateBottomCategoryFrames(category, layer, idx, model);
                            break;
                    }
                }
                idx++;
            }
            hdrIdx++;
        }
    }

    private void updateBottomCategoryFrames(Category category, Layer layer, int layerIdx, TableModel model) {
        updateHorizontalCategoryFrames(category, layer, model, model.getRowCount() - 1 - layerIdx);
    }

    private void updateHorizontalCategoryFrames(Category category, Layer layer, TableModel model, int layerRow) {
        updateRowAllFrames(model, layerRow, category.weight);
        int col = model.leftHeadersCount;
        for (TableModel.Cell cell : layer.cells) {
            updateColFrame(model, col, Style.Side.LEFT, category.weight);
            col = col + cell.colSpan - 1;
            updateColFrame(model, col, Style.Side.RIGHT, category.weight);
            col++;
            if (col < model.getColCount()) {
                updateColFrame(model, col, Style.Side.LEFT, category.weight);
            }
        }
    }

    private void updateRightCategoryFrames(Category category, Layer layer, int layerIdx, TableModel model) {
        updateVerticalCategoryFrames(category, layer, model, model.getColCount() - 1 - layerIdx);
    }

    private void updateVerticalCategoryFrames(Category category, Layer layer, TableModel model, int layerCol) {
        updateColAllFrames(model, layerCol, category.weight);
        int row = model.topHeadersCount;
        for (TableModel.Cell cell : layer.cells) {
            updateRowFrame(model, row, Style.Side.TOP, category.weight);
            row = row + cell.rowSpan - 1;
            updateRowFrame(model, row, Style.Side.BOTTOM, category.weight);
            row++;
            if (row < model.getRowCount()) {
                updateRowFrame(model, row, Style.Side.TOP, category.weight);
            }
        }
    }

    private void updateTopCategoryFrames(Category category, Layer layer, int layerIdx, TableModel model) {
        updateHorizontalCategoryFrames(category, layer, model, layerIdx);
    }

    private void updateLeftCategoryFrames(Category category, Layer layer, int layerIdx, TableModel model) {
        updateVerticalCategoryFrames(category, layer, model, layerIdx);
    }

    private void updateColAllFrames(TableModel model, int col, int width) {
        updateColFrame(model, col, Style.Side.LEFT, width);
        updateColFrame(model, col, Style.Side.TOP, width);
        updateColFrame(model, col, Style.Side.RIGHT, width);
        updateColFrame(model, col, Style.Side.BOTTOM, width);
    }

    private void updateRowAllFrames(TableModel model, int row, int width) {
        updateRowFrame(model, row, Style.Side.LEFT, width);
        updateRowFrame(model, row, Style.Side.TOP, width);
        updateRowFrame(model, row, Style.Side.RIGHT, width);
        updateRowFrame(model, row, Style.Side.BOTTOM, width);
    }

    private void updateWholeTableFrame(TableModel model) {
        model.maxWidth = 1;
        for (Category category : categories) {
            model.maxWidth = Math.max(model.maxWidth, category.weight + 1);
        }
        int lastCol = model.getColCount() - 1;
        int lastRow = model.getRowCount() - 1;
        updateColFrame(model, 0, Style.Side.LEFT, model.maxWidth);
        updateRowFrame(model, 0, Style.Side.TOP, model.maxWidth);
        updateColFrame(model, lastCol, Style.Side.RIGHT, model.maxWidth);
        updateRowFrame(model, lastRow, Style.Side.BOTTOM, model.maxWidth);
        if (model.rightHeadersCount > 1)
            model.rows.get(0).cells.get(lastCol - model.rightHeadersCount + 1).updateFrame(Style.Side.RIGHT, model.maxWidth);
        if (model.bottomHeadersCount > 1)
            model.rows.get(lastRow - model.bottomHeadersCount + 1).cells.get(0).updateFrame(Style.Side.BOTTOM, model.maxWidth);
        if (model.rightHeadersCount > 1) {
            if (model.bottomHeadersCount > 1) {
                model.rows.get(lastRow - model.bottomHeadersCount + 1).cells.get(lastCol - model.rightHeadersCount + 1).updateFrame(Style.Side.RIGHT, model.maxWidth);
                model.rows.get(lastRow - model.bottomHeadersCount + 1).cells.get(lastCol - model.rightHeadersCount + 1).updateFrame(Style.Side.BOTTOM, model.maxWidth);
            } else {
                model.rows.get(lastRow).cells.get(lastCol - model.rightHeadersCount + 1).updateFrame(Style.Side.RIGHT, model.maxWidth);
            }
        } else {
            if (model.bottomHeadersCount > 1) {
                model.rows.get(lastRow - model.bottomHeadersCount + 1).cells.get(lastCol).updateFrame(Style.Side.RIGHT, model.maxWidth);
            } else {
                // Do nothing
            }
        }
    }

    private void resetTableFrames(TableModel model) {
        for (TableModel.Row row : model.rows) {
            for (TableModel.Cell cell : row.cells) {
                cell.setFrame(1, 1, 1, 1);
            }
        }
    }

    private void updateRowFrame(TableModel model, int row, Style.Side side, int width) {
        for (TableModel.Cell cell : model.rows.get(row).cells) {
            cell.updateFrame(side, width);
        }
    }

    private void updateColFrame(TableModel model, int col, Style.Side side, int width) {
        for (TableModel.Row row : model.rows) {
            TableModel.Cell cell = row.cells.get(col);
            cell.updateFrame(side, width);
        }
    }

    private void addHorizontalHeaders(Header header, TableModel model) {
        boolean isFirstLayer = true;
        for (Layer layer : header.layers) {
            TableModel.Row row = model.addRow();

            addCornerHorizontalHeaderCell(header, model.leftHeadersCount, isFirstLayer, row);

            for (TableModel.Cell cell : layer.cells) {
                row.addCell(cell);
                for (int i = 0; i < cell.colSpan - 1; i++) {
                    TableModel.Cell wcell = row.addCell();
                    wcell.isVisible = false;
                }
            }

            addCornerHorizontalHeaderCell(header, model.rightHeadersCount, isFirstLayer, row);
            isFirstLayer = false;
        }
    }

    private void addCornerHorizontalHeaderCell(Header header, int colSpan, boolean firstLayer, TableModel.Row row) {
        if (colSpan > 0) {
            TableModel.Cell cell = row.addCell();
            cell.rowSpan = header.layers.size();
            cell.colSpan = colSpan;
            cell.isVisible = firstLayer;
            cell.isHeader = true;
            cell.headerCategory = null;
            cell.isCorner = true;
            for (int i = 0; i < colSpan - 1; i++) {
                TableModel.Cell wcell = row.addCell();
                wcell.isVisible = false;
                wcell.isHeader = true;
                wcell.headerCategory = null;
                wcell.isCorner = true;
            }
        }
    }

    private void addTopHeaders(Header topHeader, TableModel model) {
        addHorizontalHeaders(topHeader, model);
    }

    private void addMiddleCells(Header leftHeader, int rows, int cols, Header rightHeader, TableModel model) {
        TableModel.Cell left[][] = null;
        if (leftHeader.layers.size() > 0) {
            left = new TableModel.Cell[rows][leftHeader.layers.size()];
            int c = 0;
            for (Layer layer : leftHeader.layers) {
                int r = rows - 1;
                for (TableModel.Cell cell : layer.cells) {
                    for (int i = 0; i < cell.rowSpan - 1; i++) {
                        TableModel.Cell cell1 = new TableModel.Cell();
                        cell1.isVisible = false;
                        left[r][c] = cell1;
                        r--;
                    }
                    left[r][c] = cell;
                    r--;
                }
                c++;
            }
        }
        TableModel.Cell right[][] = null;
        if (rightHeader.layers.size() > 0) {
            Header header = new Header(rightHeader.orientation);
            for (Layer layer : rightHeader.layers) {
                header.layers.add(0, layer);
            }
            right = new TableModel.Cell[rows][header.layers.size()];
            int c = 0;
            for (Layer layer : header.layers) {
                int r = 0;
                for (TableModel.Cell cell : layer.cells) {
                    right[r][c] = cell;
                    r++;
                    for (int i = 0; i < cell.rowSpan - 1; i++) {
                        TableModel.Cell cell1 = new TableModel.Cell();
                        cell1.isVisible = false;
                        right[r][c] = cell1;
                        r++;
                    }
                }
                c++;
            }
        }

        for (int r = 0; r < rows; r++) {
            TableModel.Row row = model.addRow();
            addMiddleHeaderCells(left, r, row);
            for (int c = 0; c < cols; c++) {
                TableModel.Cell cell = row.addCell();
                cell.isVisible = true;
                cell.colSpan = 1;
                cell.rowSpan = 1;
                cell.data = cell.printCoordinateShort();
            }
            addMiddleHeaderCells(right, r, row);
        }

    }

    private void addMiddleHeaderCells(TableModel.Cell[][] cells, int r, TableModel.Row row) {
        if (cells != null) {
            for (int c = 0; c < cells[r].length; c++) {
                row.addCell(cells[r][c]);
            }
        }
    }

    private void addBottomHeaders(Header bottomHeader, TableModel model) {
        Header header = new Header(bottomHeader.orientation);
        for (Layer layer : bottomHeader.layers) {
            header.layers.add(0, layer);
        }
        addHorizontalHeaders(header, model);
    }
}
