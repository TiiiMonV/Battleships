module de.schiffe_versenken.schiffe_versenken {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens de.schiffe_versenken to javafx.fxml;
    exports de.schiffe_versenken;
}