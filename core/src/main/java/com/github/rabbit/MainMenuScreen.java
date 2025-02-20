package com.github.rabbit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.github.rabbit.Lvl1.Lvl1Game;
import com.github.rabbit.Lvl2.Lvl2Game;


public class MainMenuScreen implements Screen {

    final Rabbithole game;
    Music music;

    public MainMenuScreen(final Rabbithole game) {
        this.game = game;
        music = Gdx.audio.newMusic(Gdx.files.internal("Pix -A Lonely Cherry Tree.mp3"));
        music.play();
    }

    @Override
    public void show() {
        // Keine speziellen Initialisierungen nötig
    }

    @Override
    public void render(float delta) {
        // Hintergrundfarbe setzen
        ScreenUtils.clear(Color.DARK_GRAY);

        // Kamera und Viewport anwenden
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);


        // Text zeichnen
        //game.batch.begin();
       //game.font.draw(game.batch, "Project Rabbithole!!!", 1, 7); // 1m von links, 9m von unten

        /*
// Textnachricht definieren
        String message = "Project Rabbithole!!!";

         */

// GlyphLayout erstellen und Text setzen
        GlyphLayout layout = new GlyphLayout();
        //layout.setText(game.font, message);



// Berechnung der X-Position, um den Text zu zentrieren
        float x = (game.viewport.getWorldWidth() - layout.width) / 2;
        float y = 400; // Y-Position des Textes


// Text zeichnen
        game.batch.begin();
        game.font.draw(game.batch, layout, x, y);
        game.font.draw(game.batch, "Project Rabbithole", 30, 350);


        // Einfache Button-Darstellung (z. B. Rechteck als Button)
        //Start
        float buttonX = 100; // X-Position des Buttons
        float buttonY = 100; // Y-Position des Buttons
        float buttonWidth = 200; // Breite des Buttons
        float buttonHeight = 50; // Höhe des Buttons
        game.batch.setColor(Color.LIGHT_GRAY); // Farbe Button
        game.batch.draw(game.pixel, buttonX, buttonY, buttonWidth, buttonHeight); // "game.pixel" ist eine 1x1-Pixel-Textur
        game.batch.setColor(Color.WHITE); // Farbe zurücksetzen
        game.font2.draw(game.batch, "Start", buttonX + 70, buttonY + 30); // Button-Beschriftung

        //Spiel verlassen
        // Einfache Button-Darstellung (z. B. Rechteck als Button)
        float buttonXSpielAus = 350; // X-Position des Buttons
        float buttonYSpielAus = 100; // Y-Position des Buttons
        float buttonWidthSpielAus = 400; // Breite des Buttons
        float buttonHeightSpielAus = 50; // Höhe des Buttons
        game.batch.setColor(Color.LIGHT_GRAY); // Farbe Button
        game.batch.draw(game.pixel, buttonXSpielAus, buttonYSpielAus, buttonWidthSpielAus, buttonHeightSpielAus); // "game.pixel" ist eine 1x1-Pixel-Textur
        game.batch.setColor(Color.WHITE); // Farbe zurücksetzen
        game.font2.draw(game.batch, "Leave Game", buttonXSpielAus + 70, buttonYSpielAus + 30); // Button-Beschriftung



        game.batch.end();




        //game.font.draw(game.batch, "Tap sceen to begin!", 1, 2); // 1m von links, 8m von unten
        //game.batch.end();

        // Auf Eingabe reagieren



        /*
        //Tap
        if (Gdx.input.isTouched()) {
            // Touch-Position in Weltkoordinaten umwandeln
            Vector3 touchPos = game.viewport.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            System.out.println("Touch position in world coordinates: " + touchPos.x + ", " + touchPos.y);

            // Wechsel zum Spielbildschirm
            game.setScreen(new Lvl2Game(game));
            dispose();
        }

        */

        // Auf Button-Klick reagieren
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = game.viewport.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (touchPos.x >= buttonX && touchPos.x <= buttonX + buttonWidth &&
                touchPos.y >= buttonY && touchPos.y <= buttonY + buttonHeight) {
                //System.out.println("Button clicked!");
                game.setScreen(new Lvl1Game(game));
                dispose();

            }
            if (touchPos.x >= buttonXSpielAus && touchPos.x <= buttonXSpielAus + buttonWidthSpielAus &&
                touchPos.y >= buttonYSpielAus && touchPos.y <= buttonYSpielAus + buttonHeightSpielAus) {

                dispose();

                //LibGdx-eigene Lösung um das Spiel zu verlassen:
                Gdx.app.exit();
            }
        }

    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        music.dispose();
        // Ressourcen werden zentral in der Game-Klasse verwaltet, daher hier kein dispose nötig
    }
}
