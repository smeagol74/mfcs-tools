package com.hitsoft.mfcs.vaadin;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.hitsoft.mfcs.model.Mfcs;
import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MfcsApplication extends Application implements HttpServletRequestListener {
    private Window window = null;

    private HttpServletRequest request = null;

    @Override
    public void init() {
        window = new Window("My Vaadin Application");
        setMainWindow(window);
        Button button = new Button("Click Me");
        button.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {

                Mfcs.Category cat;
                Mfcs mfcs = new Mfcs("Проверка");

                cat = mfcs.addCategory("T", "Тип хвоста", new Color(0xa7cbd7));
                cat.addValue("0", "Нету");
                cat.addValue("1", "Длинный голый");
                cat.addValue("2", "Короткий волосатый");

                cat = mfcs.addCategory("W", "Наличие крыльев", new Color(0xc1acdb));
                cat.addValue("Y", "Есть");
                cat.addValue("N", "Нет");

                cat = mfcs.addCategory("H", "Количество голов", new Color(0xf4efbb));
                cat.addValue("1");
                cat.addValue("2");
                cat.addValue("3");
                cat.addValue("4");
                cat.addValue("15");
                cat.addValue("150");

                cat = mfcs.addCategory("S", "Покров", new Color(0xf4d8bb));
                cat.addValue("0", "Кожа");
                cat.addValue("1", "Чешуя");
                cat.addValue("2", "Шерсть");

                cat = mfcs.addCategory("F", "Зубы", null);
                cat.addValue("Y", "Есть");
                cat.addValue("N", "Нет");

                cat = mfcs.addCategory("E", "Глаза", null);
                cat.addValue("0", "Нет");
                cat.addValue("1", "1");
                cat.addValue("2", "2");

                cat = mfcs.addCategory("F", "Дыхание", null);
                cat.addValue("L", "Лёгкие");
                cat.addValue("Z", "Жабры");

                cat = mfcs.addCategory("A", "A", null);
                cat.addValue("a0", "A0");
                cat.addValue("a1", "A1");

                cat = mfcs.addCategory("B", "B", null);
                cat.addValue("b0", "B0");
                cat.addValue("b1", "B1");

                cat = mfcs.addCategory("C", "C", null);
                cat.addValue("c0", "C0");
                cat.addValue("c1", "C1");

                cat = mfcs.addCategory("D", "D", null);
                cat.addValue("d0", "D0");
                cat.addValue("d1", "D1");

                mfcs.renderToRequest(request);

                window.open(new ExternalResource("/table.jsp"), "_blank");
                window.addComponent(new Label("Thank you for clicking"));
            }
        });
        window.addComponent(button);

    }

    public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
    }

    public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
        this.request = null;
    }

    static {
        MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
    }
}
