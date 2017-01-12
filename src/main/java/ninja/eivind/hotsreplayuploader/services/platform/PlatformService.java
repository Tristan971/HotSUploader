// Copyright 2015 Eivind Vegsundvåg
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package ninja.eivind.hotsreplayuploader.services.platform;

import javafx.application.Platform;
import javafx.stage.Stage;
import ninja.eivind.hotsreplayuploader.files.tempwatcher.BattleLobbyTempDirectories;
import ninja.eivind.hotsreplayuploader.utils.Constants;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

/**
 * Interface for implementations. There should be one active at all times, depending on the user's platform/operating
 * system. When no valid implementation can be detected, the {@link PlatformServiceFactoryBean} will throw a
 * {@link PlatformNotSupportedException}.
 */
public interface PlatformService {

    String USER_HOME = System.getProperty("user.home");
    String APPLICATION_DIRECTORY_NAME = Constants.APPLICATION_NAME.replaceAll(" ", "");

    File getApplicationHome();

    File getHotSHome();

    /**
     * Defines how the application reacts on window notifications like closing or hiding.
     * @param primaryStage the main stage
     */
    default void setupWindowBehaviour(Stage primaryStage) {
        Platform.setImplicitExit(false);
        primaryStage.setOnHiding(value -> {
            primaryStage.setIconified(true);
            value.consume();
        });
    }

    default TrayIcon getTrayIcon(Stage primaryStage) throws PlatformNotSupportedException {
        throw new PlatformNotSupportedException("Not implemented in " + getClass());
    }

    default TrayIcon buildTrayIcon(URL imageURL, Stage primaryStage) {
        final Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
        final PopupMenu popup = new PopupMenu();
        final MenuItem showItem = new MenuItem("Show");
        final MenuItem exitItem = new MenuItem("Exit");

        // Declare shared action for showItem and trayicon click
        final Runnable openAction = () -> Platform.runLater(() -> {
            primaryStage.setIconified(false);
            primaryStage.show();
            primaryStage.toFront();
        });
        popup.add(showItem);
        popup.add(exitItem);

        final TrayIcon trayIcon = new TrayIcon(image, "Initializing tooltip", popup);
        trayIcon.setImageAutoSize(true);

        // Add listeners
        trayIcon.addMouseListener(new TrayMouseListenerBase() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() % 2 == 0) {
                    openAction.run();
                }
            }
        });
        showItem.addActionListener(e -> openAction.run());
        exitItem.addActionListener(event -> shutdown());
        return trayIcon;

    }

    /**
     * Shuts down the application by delegating service cleanups
     * to the Application Thread.
     */
    default void shutdown() {
        // let JavaFX shut close its services gracefully
        Platform.exit();
    }

    void browse(String uri);

    URL getLogoUrl();

    boolean isPreloaderSupported();

    BattleLobbyTempDirectories getBattleLobbyTempDirectories();
}
