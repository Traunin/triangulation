module com.github.traunin.triangulation.demo {
    requires javafx.fxml;
    requires javafx.graphics;

    requires com.github.traunin.triangulation;
    requires com.github.shimeoki.jfx.rasterization;

    opens com.github.traunin.triangulation.demo to javafx.fxml;

    exports com.github.traunin.triangulation.demo;
}