package Util;

import javax.swing.*;
import java.awt.*;

public class UIImageLoader {

    public static JLabel createLogoLabel(int x, int y, int width, int height) {
        java.net.URL imgUrl = UIImageLoader.class
                .getClassLoader()
                .getResource("hochschule.png");

        if (imgUrl == null) {
            System.err.println("❌ Logo nicht gefunden!");
            return new JLabel();
        }

        ImageIcon icon = new ImageIcon(imgUrl);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(img));
        label.setBounds(x, y, width, height);

        return label;
    }

    private UIImageLoader() {}
}
