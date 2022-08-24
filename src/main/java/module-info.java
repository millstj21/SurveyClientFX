module tim.survey.surveyclientfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens tim.survey.surveyclientfx to javafx.fxml;
    exports tim.survey.surveyclientfx;
}