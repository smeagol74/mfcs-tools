package com.hitsoft.mfcs.vaadin.view;

import com.hitsoft.mfcs.model.Mfcs;
import com.hitsoft.mfcs.vaadin.MfcsApplication;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import eu.livotov.tpt.gui.widgets.TPTMultiView;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleMfcs extends GridLayout implements TPTMultiView.TPTView {
    public static final String VIEW = "simple";

    private Panel panel;
    private String name = "";

    public SimpleMfcs() {
        super(1, 2);
        setSizeFull();
        setRowExpandRatio(0, 0);
        setRowExpandRatio(1, 1);

        Label lTitle = new Label("<h1>МСКФ - Многомерная Система Координат Федосеева<h1>", Label.CONTENT_XHTML);
        lTitle.setWidth("");
        addComponent(lTitle, 0, 0);
        setComponentAlignment(lTitle, Alignment.TOP_CENTER);

        panel = buildPanel();
        addComponent(panel, 0, 1);
        setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
    }

    private Panel buildPanel() {
        Panel result = new Panel("МСКФ: ...");
        result.setWidth("75%");

        TabSheet tabs = new TabSheet();
        tabs.setWidth("100%");
        tabs.setHeight("520px");

        VerticalLayout tab1 = new VerticalLayout();
        tab1.setMargin(true);

        FormLayout form = new FormLayout();
        tab1.addComponent(form);

        final TextArea data = new TextArea();
        data.setWidth("100%");
        data.setRows(25);
        form.addComponent(data);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidth("100%");
        tab1.addComponent(buttons);

        Button button;

        button = new Button("Вернуться на главную");
        buttons.addComponent(button);
        buttons.setComponentAlignment(button, Alignment.TOP_LEFT);
        button.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Index.show();
            }
        });

        button = new Button("Создать МСКФ");
        buttons.addComponent(button);
        buttons.setComponentAlignment(button, Alignment.TOP_RIGHT);
        button.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                String errors = prepareData(data.getValue().toString());

                if (errors.length() > 0) {
                    getWindow().showNotification(
                            "Ошибка в описании системы координат<br>",
                            errors,
                            Window.Notification.TYPE_ERROR_MESSAGE);
                } else {
                    getWindow().open(new ExternalResource("/table.jsp"), "_blank");
                    getWindow().showNotification("Координатная сетка открылась в дополнительном окне.");
                }
            }
        });

        VerticalLayout tab2 = new VerticalLayout();
        tab2.setMargin(true);
        Label lDescription = new Label("<p>" +
                "Для того, чтобы отрисовать сетку МСКФ, необходимо описать собственно систему координат " +
                "(оси и принимаемые значения). Формат описания системы координат следующий:" +
                "<ul>" +
                "<li><strong>Строка описывающая ось:</strong><br/>" +
                "<pre>[X] Координата X &lt;#99FFFF&gt;</pre>, где X - короткое название оси координат, может " +
                "быть любой заглавной латинской буквой. \"Координата X\" - полное название оси координат, " +
                "#99FFFF - цвет оси координат. Цвет и короткое название можно не указывать, тогда короткое название " +
                "будет сгенерировано автоматически." +
                "</li>" +
                "<li><strong>Строка описывающая значение:</strong><br/>" +
                "<pre>* [1] значение 1</pre>, где 1 - это короткая координата, а \"значение 1\" - полное название координаты. " +
                "Короткую координату можно не указывать" +
                "</li>" +
                "</ul>" +
                "Пример описания системы координат:" +
                "<pre>\n" +
                "[X] Координата Х &lt;#99FFFF&gt;\n" +
                "* [1] значение 1\n" +
                "* [2] значение 2\n" +
                "Координата B\n" +
                "* значение 0\n" +
                "* значение 1\n" +
                "</pre>" +
                "</p>", Label.CONTENT_XHTML);
        tab2.addComponent(lDescription);

        tabs.addTab(tab1, "Создание МСКФ", null);
        tabs.addTab(tab2, "Описание и примеры", null);

        result.addComponent(tabs);

        return result;
    }

    private static final Pattern P_CATEGORY_FULL = Pattern.compile("^\\[([A-Z]*)\\] (.*) <#([0-9a-fA-F]{6})>$");
    private static final Pattern P_CATEGORY_NOCOLOR = Pattern.compile("^\\[([A-Z]*)\\] (.*)$");
    private static final Pattern P_CATEGORY_NOINDEX = Pattern.compile("^(.*) <#([0-9a-fA-F]{6})>$");
    private static final Pattern P_VALUE_FULL = Pattern.compile("^\\* \\[([a-z0-9]*)\\] (.*)$");
    private static final Pattern P_VALUE_NOINDEX = Pattern.compile("^\\* (.*)$");

    private String prepareData(String data) {

        Mfcs.Category cat = null;
        Mfcs mfcs = new Mfcs(name);

        int currentCategoryIndex = 0;

        StringBuilder errors = new StringBuilder();

        for (String line : data.split("\n")) {
            line = line.trim();
            if (!line.isEmpty()) {
                Matcher m;

                boolean isCategory = false;
                String categoryIndex = null;
                String categoryTitle = null;
                Color categoryColor = null;
                String valueIndex = null;
                String valueTitle = null;

                m = P_CATEGORY_FULL.matcher(line);
                if (m.matches()) {
                    isCategory = true;
                    categoryIndex = m.group(1);
                    categoryTitle = m.group(2);
                    categoryColor = parseColor(m.group(3));
                } else {
                    m = P_CATEGORY_NOCOLOR.matcher(line);
                    if (m.matches()) {
                        isCategory = true;
                        categoryIndex = m.group(1);
                        categoryTitle = m.group(2);
                    } else {
                        m = P_CATEGORY_NOINDEX.matcher(line);
                        if (m.matches()) {
                            isCategory = true;
                            categoryTitle = m.group(1);
                            categoryColor = parseColor(m.group(2));
                        } else {
                            m = P_VALUE_FULL.matcher(line);
                            if (m.matches()) {
                                valueIndex = m.group(1);
                                valueTitle = m.group(2);
                            } else {
                                m = P_VALUE_NOINDEX.matcher(line);
                                if (m.matches()) {
                                    valueTitle = m.group(1);
                                } else {
                                    isCategory = true;
                                    categoryTitle = line;
                                }
                            }
                        }
                    }
                }
                if (isCategory) {
                    if (cat == null || cat.values.size() > 1) {
                        if (categoryIndex == null) {
                            categoryIndex = String.valueOf((char) ('A' + currentCategoryIndex));
                        }
                        cat = mfcs.addCategory(categoryIndex, categoryTitle, categoryColor);
                        currentCategoryIndex++;
                    } else {
                        errors.append(String.format("!Мало значений! Координата '%s' содержит меньше двух значений.\n", cat.name));
                    }
                } else {
                    if (cat == null) {
                        errors.append("!Первая строка должна быть координатой!\n");
                    } else {
                        if (valueIndex == null)
                            cat.addValue(valueTitle);
                        else
                            cat.addValue(valueIndex, valueTitle);
                    }
                }
            }
        }
        if (cat == null) {
            errors.append("!Не задано ни одной оси координат!\n");
        } else if (cat.values.size() < 2) {
            errors.append(String.format("!Мало значений! Координата '%s' содержит меньше двух значений.\n", cat.name));
        }

        if (errors.length() == 0)
            mfcs.renderToRequest(MfcsApplication.app().request);

        return errors.toString();
    }

    private Color parseColor(String color) {
        Color result = null;
        try {
            Integer res = Integer.parseInt(color, 16);
            result = new Color(res);
        } catch (Exception ignored) {
        }
        return result;
    }

    public void viewActivated(String previousViewName, String parameters) {

    }

    public void viewDeactivated(String newViewName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void viewAttached() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void viewRemoved() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void show(String name) {
        TPTMultiView ctrl = MfcsApplication.app().mvController;
        SimpleMfcs view = (SimpleMfcs) ctrl.getView(VIEW);
        view.setMfcsName(name);
        ctrl.switchView(SimpleMfcs.VIEW);
    }

    private void setMfcsName(String name) {
        this.name = name;
        panel.setCaption(String.format("МСКФ: %s", name));
    }
}
