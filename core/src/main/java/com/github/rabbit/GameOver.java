package com.github.rabbit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOver implements Screen {
    final Rabbithole game;
    Music music;


    public GameOver(Rabbithole game) {
        this.game = game;
        music = Gdx.audio.newMusic(Gdx.files.internal("Cody O Quinn - Its Not Game Over Yet.mp3"));
        music.play();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.PINK);

        // Kamera und Viewport anwenden
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        // GlyphLayout erstellen und Text setzen
        GlyphLayout layout = new GlyphLayout();

        // Berechnung der X-Position, um den Text zu zentrieren
        float x = (game.viewport.getWorldWidth() - layout.width) / 2;
        float y = 400; // Y-Position des Textes

        // Text zeichnen
        game.batch.begin();
        game.font.draw(game.batch, layout, x, y);
        game.font.draw(game.batch, "Game Over...!", 30, 350);
        game.font2.draw(game.batch, layout, x, y);
        game.font2.draw(game.batch, "Time: ", 20, 200);
        game.batch.end();




        //game.font.draw(game.batch, "Tap sceen to begin!", 1, 2); // 1m von links, 8m von unten
        //game.batch.end();

        // Auf Eingabe reagieren
        if (Gdx.input.isTouched()) {

            //evtl Methode -->endGame()
            // Touch-Position in Weltkoordinaten umwandeln
            Vector3 touchPos = game.viewport.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            System.out.println("Touch position in world coordinates: " + touchPos.x + ", " + touchPos.y);

            // Wechsel zum Spielbildschirm
            game.setScreen(new MainMenuScreen(game)); // oder abbrechen-knopf inhalt
            music.dispose();

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
