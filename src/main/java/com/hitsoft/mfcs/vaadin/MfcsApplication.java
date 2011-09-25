package com.hitsoft.mfcs.vaadin;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.hitsoft.mfcs.vaadin.view.Index;
import com.hitsoft.mfcs.vaadin.view.SimpleMfcs;
import com.vaadin.Application;
import com.vaadin.ui.Window;
import eu.livotov.tpt.TPTApplication;
import eu.livotov.tpt.gui.widgets.TPTMultiView;

import javax.servlet.http.HttpServletRequest;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MfcsApplication extends TPTApplication {

    public HttpServletRequest request = null;
    public TPTMultiView mvController = null;

    @Override
    public void applicationInit() {
        Window window = new Window("My Vaadin Application");
        setMainWindow(window);

        mvController = new TPTMultiView(true);
        window.setContent(mvController);
        mvController.setSizeFull();

        mvController.addView(Index.VIEW, new Index(this));
        mvController.addView(SimpleMfcs.VIEW, new SimpleMfcs());

        mvController.switchView(Index.VIEW);
    }

    @Override
    public void firstApplicationStartup() {
        // Do nothing
    }

    @Override
    public void transactionStart(Application application, Object o) {
        super.transactionStart(application, o);
        this.request = (HttpServletRequest) o;
    }

    @Override
    public void transactionEnd(Application application, Object o) {
        super.transactionEnd(application, o);
        this.request = null;
    }

    static {
        MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
    }

    public static MfcsApplication app() {
        return (MfcsApplication) TPTApplication.getCurrentApplication();
    }
}
