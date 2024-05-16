package service;

import org.controlsfx.control.Notifications;

public class SystemNotification {

    public static void showNotification(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .showInformation();
    }
}
