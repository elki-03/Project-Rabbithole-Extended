package com.github.rabbit;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Rabbithole extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public BitmapFont font2;
    public FitViewport viewport;
    public OrthographicCamera cam;
    //public static float startZeit;         // Startzeit des Spiels

    //public static long endZeit;           //EndZeit
    public static float spielZeit;      // Verstrichene Zeit seit Spielstart

    public Texture pixel; // Die 1x1 Pixel-Textur für den Button (in anderen Klassen verwendet)

    public static int naechstesLvl=0;// Levelzaehler für (Zwischenscreen)

    @Override
    public void create() {

        /*
        // Kamera und Viewport initialisieren
        cam = new OrthographicCamera();
        viewport = new FitViewport(16, 10, cam); // 16x10 Spielwelt-Einheiten (Meter)
        cam.position.set(8, 5, 0); // Kamera auf die Mitte der Welt setzen
        cam.update();
        */

        cam = new OrthographicCamera();
        viewport = new FitViewport(800, 480, cam); // Beispielwerte für die Weltgröße
        cam.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        cam.update();


        //

        batch = new SpriteBatch();

        // Bildschirmabmessungen ermitteln
        int screenHeight = Gdx.graphics.getHeight();

        // Basis-Schriftgröße (dieser Wert kann angepasst werden)
        int baseFontSize = 60;
        int baseFontSize2 = 12;

        // Schriftgröße basierend auf Bildschirmhöhe berechnen
        int calculatedFontSize = (int)(screenHeight * 0.05); // 5% der Bildschirmhöhe

        // Sicherstellen, dass die Schriftgröße nicht zu klein oder zu groß ist
        int fontSize = Math.max(baseFontSize, calculatedFontSize);
       //int fontSize2 = Math.max(baseFontSize2, calculatedFontSize);




        // Schriftgenerator konfigurieren
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Quicksand-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.WHITE;
        parameter.color = Color.PINK;

        font = generator.generateFont(parameter);
        generator.dispose();// Nicht vergessen, den Generator zu entsorgen

        // 2.Schriftgenerator (test)
        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("Quicksand-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        //parameter2.size = fontSize2;
        parameter2.size = 30; //test --> bleibt so, weil "never change a running system"
        parameter2.borderWidth = 1;
        parameter2.borderColor = Color.BLACK;
        parameter2.color = Color.WHITE;

        font2 = generator2.generateFont(parameter2);

        generator2.dispose();// Nicht vergessen, den Generator zu entsorgen !!!
        // Die 1x1 Pixel-Textur laden
        pixel = new Texture("pixel1x1.png"); // Lade die Datei "pixel.png" aus dem assets-Verzeichnis



        //--> Screen wechseln auf MainMenuScreen
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // Wichtig, um den aktuellen Screen zu rendern
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        pixel.dispose();
    }
}
