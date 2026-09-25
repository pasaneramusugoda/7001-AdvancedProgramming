package com.iwfc;

import com.iwfc.pattern.structural.IWFCSystemFacade;
import com.iwfc.ui.ConsoleMenu;
import com.iwfc.util.DataInitializer;

/**
 * Main application bootstrap entry point for the Intelligent Wellness and Fitness Center (IWFC) prototype.
 */
public class Main {

    public static void main(String[] args) {
        // Instantiate the system facade orchestrating all subsystems
        IWFCSystemFacade facade = new IWFCSystemFacade();

        // Seed realistic data for immediate testing and presentation demonstrations
        DataInitializer.initializeSeedData(facade);

        // Start interactive command-line interface
        ConsoleMenu menu = new ConsoleMenu(facade);
        menu.start();
    }
}
