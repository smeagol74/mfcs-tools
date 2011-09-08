package com.hitsoft.mfcs.vaadin;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.hitsoft.mfcs.model.Mfcs;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import eu.livotov.tpt.TPTApplication;
import org.vaadin.openid.OpenIdHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.Map;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MfcsApplication extends TPTApplication implements HttpServletRequestListener {
    private Window window = null;

    private HttpServletRequest request = null;

    @Override
    public void applicationInit() {
        window = new Window("My Vaadin Application");
        setMainWindow(window);

        GridLayout grid = new GridLayout(2, 2);
        grid.setSizeFull();
        window.setContent(grid);
        grid.setRowExpandRatio(0, 0);
        grid.setRowExpandRatio(1, 1);
        window.getContent().setSizeFull();

        Label lTitle = new Label("<h1>МСКФ - Многомерная Система Координат Федосеева<h1>", Label.CONTENT_XHTML);
        lTitle.setWidth("");
        grid.addComponent(lTitle, 0, 0, 1, 0);
        grid.setComponentAlignment(lTitle, Alignment.TOP_CENTER);

        Panel leftPanel = buildSingleUsageMFCS();
        grid.addComponent(leftPanel, 0, 1);
        grid.setComponentAlignment(leftPanel, Alignment.MIDDLE_CENTER);

        Panel rightPanel = buildDatabaseMFCS();
        grid.addComponent(rightPanel, 1, 1);
        grid.setComponentAlignment(rightPanel, Alignment.MIDDLE_CENTER);


//        Button button = new Button("Click Me");
//        button.addListener(new Button.ClickListener() {
//            public void buttonClick(Button.ClickEvent event) {
//
//                prepareDemoData();
//
//                window.open(new ExternalResource("/table.jsp"), "_blank");
//                window.addComponent(new com.vaadin.ui.Label("Thank you for clicking"));
//            }
//        });
//        window.addComponent(button);
    }

    private Link createLoginLink(OpenIdHandler openIdHandler, String id,
                                 String caption) {
        return new Link(caption, openIdHandler.getLoginResource(id),
                "openidLogin", 600, 400, Window.BORDER_NONE);
    }

    private Panel buildDatabaseMFCS() {
        final Panel result = new Panel("Работа с базой данных");
        result.setWidth("75%");

        OpenIdHandler openIdHandler = new OpenIdHandler(this);

        final HorizontalLayout linkHolder = new HorizontalLayout();
        linkHolder.setSpacing(true);
        linkHolder
                .addComponent(createLoginLink(openIdHandler,
                        "https://www.google.com/accounts/o8/id",
                        "Войти через Google"));
        linkHolder.addComponent(createLoginLink(openIdHandler,
                "https://me.yahoo.com", "Войти через Yahoo"));

        result.addComponent(linkHolder);

        openIdHandler.addListener(new OpenIdHandler.OpenIdLoginListener() {
            public void onLogin(String id, Map<String, String> userInfo) {
                result.removeComponent(linkHolder);
                result.addComponent(new Label("Logged in as " + id));
            }
        });
        return result;
    }

    private Panel buildSingleUsageMFCS() {
        Panel result = new Panel("Создание одноразовой МСКФ");
        result.setWidth("75%");
        Label lDescription = new Label("<p>Если Вам нужно подготовить заготовку для МСКФ " +
                "ровно на один раз, Вы не собираетесь более в будущем работать с этой системой, " +
                "то Вам сюда. Вводите название своей МСКФ и жмите кнопку чтобы начать работу над " +
                "строительством своего пространства.</p>", Label.CONTENT_XHTML);
        result.addComponent(lDescription);
        FormLayout form = new FormLayout();
        result.addComponent(form);

        final TextField simpleName = new TextField("Название");
        simpleName.setWidth("100%");
        form.addComponent(simpleName);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidth("100%");
        result.addComponent(buttons);

        Button button = new Button("Создать одноразовую МСКФ");
        buttons.addComponent(button);
        buttons.setComponentAlignment(button, Alignment.TOP_RIGHT);

        button.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                String name = simpleName.getValue().toString();
                window.showNotification(name);
            }
        });

        return result;
    }

    private void prepareDemoData() {
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
    }

    @Override
    public void firstApplicationStartup() {
        // Do nothing
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
