package com.unn.inputter;

import com.unn.inputter.service.DataController;

import java.sql.*;

public class Main implements DriverAction {

    public static void main(String[] args) {
        DataController.serve();
    }

    @Override
    public void deregister() {

    }
}
