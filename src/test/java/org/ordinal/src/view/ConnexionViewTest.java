package org.ordinal.src.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ConnexionViewTest {

    private ConnexionView connexionView;

    @BeforeEach
    public void setUp() {
        SwingUtilities.invokeLater(() -> {
            connexionView = new ConnexionView();
            connexionView.setVisible(false);
        });
    }

    @Test
    public void testDefaultValues() {
        SwingUtilities.invokeLater(() -> {
            assertEquals(502, connexionView.getWidth());
            assertEquals(331, connexionView.getHeight());
            assertEquals("E-chat Connexion", connexionView.getTitle());
            assertFalse(connexionView.isResizable());
        });
    }

    @Test
    public void testComponentsInitialization() {
        SwingUtilities.invokeLater(() -> {
            assertEquals(15, connexionView.tname.getFont().getSize());
            assertEquals(15, connexionView.tIp.getFont().getSize());
            assertEquals(15, connexionView.tport.getFont().getSize());
            assertEquals("cancel", connexionView.getContentPane().getComponent(6).getName());
            assertEquals("start", connexionView.getContentPane().getComponent(7).getName());
        });
    }
}
