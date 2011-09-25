package com.hitsoft.mfcs.vaadin.view;

import com.hitsoft.mfcs.vaadin.MfcsApplication;
import com.vaadin.Application;
import com.vaadin.ui.*;
import eu.livotov.tpt.gui.widgets.TPTMultiView;
import org.vaadin.openid.OpenIdHandler;

import java.util.Map;

/**
 * User: smeagol
 * Date: 25.09.11
 * Time: 14:54
 */
public class Index extends GridLayout implements TPTMultiView.TPTView {
    public static final String VIEW = "index";

    private Application app;

    public Index(Application application) {
        super(2, 2);
        this.app = application;
        setSizeFull();
        setRowExpandRatio(0, 0);
        setRowExpandRatio(1, 1);

        Label lTitle = new Label("<h1>МСКФ - Многомерная Система Координат Федосеева<h1>", Label.CONTENT_XHTML);
        lTitle.setWidth("");
        addComponent(lTitle, 0, 0, 1, 0);
        setComponentAlignment(lTitle, Alignment.TOP_CENTER);

        Panel leftPanel = buildSingleUsageMFCS();
        addComponent(leftPanel, 0, 1);
        setComponentAlignment(leftPanel, Alignment.MIDDLE_CENTER);

        Panel rightPanel = buildDatabaseMFCS();
        addComponent(rightPanel, 1, 1);
        setComponentAlignment(rightPanel, Alignment.MIDDLE_CENTER);

    }

    private Panel buildDatabaseMFCS() {
        final Panel result = new Panel("Работа с базой данных");
        result.setWidth("75%");

        Label label = new Label("Эта секция пока не работает.");
        result.addComponent(label);

        OpenIdHandler openIdHandler = new OpenIdHandler(app);

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

    private Link createLoginLink(OpenIdHandler openIdHandler, String id,
                                 String caption) {
        return new Link(caption, openIdHandler.getLoginResource(id),
                "openidLogin", 600, 400, Window.BORDER_NONE);
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
                SimpleMfcs.show(simpleName.getValue().toString());
            }
        });

        return result;
    }

    public void viewActivated(String s, String s1) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void viewDeactivated(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void viewAttached() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void viewRemoved() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void show() {
        MfcsApplication.app().mvController.switchView(VIEW);
    }
}
