package ui;


import model.Village;

//Main class, creates a new village and game manager.
public class Main {


    public static void main(String[] args) {
        new GameManager(new Village());
        //GameManagerGUI gameManager = new GameManagerGUI(new Village());

    }
}
