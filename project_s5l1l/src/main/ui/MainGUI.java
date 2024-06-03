package ui;

import model.Village;

//Main class, creates a new village and game manager.
public class MainGUI {


    public static void main(String[] args) {
        //GameManager gameManager = new GameManager(new Village());
        new GameManagerGUI(new Village());

    }
}
