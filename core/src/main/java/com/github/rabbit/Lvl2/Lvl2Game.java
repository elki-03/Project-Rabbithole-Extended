package com.github.rabbit.Lvl2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.rabbit.LvlFinished;
import com.github.rabbit.Rabbithole;

public class Lvl2Game implements Screen {
    final Rabbithole game;

    Texture backgroundTexture;
    Texture rabbitTexture;
    Texture rabbitholeTexture;
    Texture karottenTexture;
    Texture wandTexture;

    Sound rabbitholeSound;
    Music music;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Sprite rabbitSprite;
    Sprite rabbitholeSprite;

    Array<Sprite> karottenSprites; // Liste der Karotten-Sprites
    Array<Rectangle> karottenRectangles; // Rechtecke für Kollisionserkennung der Karotten

    Rectangle rabbitRectangle;
    Rectangle rabbitholeRectangle;
    Rectangle wand;

    private float spielZeitGame;

    private static final float SCALE_FACTOR = 0.04f;
    private static final float ALPHA = 0.1f;

    private float lastX, lastY;

    public Lvl2Game(final Rabbithole game) {
        this.game = game;

        // Texturen laden
        backgroundTexture = new Texture("boden1.png");
        rabbitTexture = new Texture("rabbit.png");
        rabbitholeTexture = new Texture("hole.png");
        karottenTexture = new Texture("karotte.png");
        wandTexture = new Texture(Gdx.files.internal("wandlvl2.png"));

        // Sounds laden
        rabbitholeSound = Gdx.audio.newSound(Gdx.files.internal("rabbithole.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("8 Bit Epic GameBoy! HeatleyBros.mp3"));

        // SpriteBatch und Viewport initialisieren
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(18, 10);

        // Wand initialisieren
        wand = new Rectangle(
            viewport.getWorldWidth() * 1 / 4,
            viewport.getWorldHeight() * 1 / 6,
            0.3f,
            viewport.getWorldHeight() * 4 / 6
        );

        // Kaninchen-Sprite initialisieren
        rabbitSprite = new Sprite(rabbitTexture);
        rabbitSprite.setSize(1, 1);
        rabbitSprite.setCenter(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f);

        // Loch-Sprite initialisieren
        rabbitholeSprite = new Sprite(rabbitholeTexture);
        rabbitholeSprite.setSize(2, 2);

        // Rechtecke initialisieren
        rabbitRectangle = new Rectangle();
        rabbitholeRectangle = new Rectangle();

        // Musik konfigurieren und starten
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

        lastX = rabbitSprite.getX();
        lastY = rabbitSprite.getY();

        spielZeitGame = 0;

        karottenSprites = new Array<>();
        karottenRectangles = new Array<>();

        // Karotten und Loch spawnen
        spawnKarotten();
        spawnRabbithole();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        spielZeitGame += delta;
        input();
        logic();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void input() {

        float ax = Gdx.input.getAccelerometerX();
        float ay = Gdx.input.getAccelerometerY();
        float az = Gdx.input.getAccelerometerZ();

        float pitch = (float) Math.atan2(-ax, az) * 180.0f / (float) Math.PI;
        float roll = (float) Math.atan2(ay, Math.sqrt(ax * ax + az * az)) * 180.0f / (float) Math.PI;

        float newX = rabbitSprite.getX() + roll * SCALE_FACTOR;
        float newY = rabbitSprite.getY() + pitch * SCALE_FACTOR;

        newX = MathUtils.clamp(newX, 0, viewport.getWorldWidth() - rabbitSprite.getWidth());
        newY = MathUtils.clamp(newY, 0, viewport.getWorldHeight() - rabbitSprite.getHeight());

        rabbitRectangle.set(newX, newY, rabbitSprite.getWidth(), rabbitSprite.getHeight());

        handleWallCollision(newX, newY, roll, pitch);

        lastX = rabbitSprite.getX();
        lastY = rabbitSprite.getY();
    }






    private void handleWallCollision(float newX, float newY, float roll, float pitch) {
        float tempX = newX;
        float tempY = newY;

        rabbitRectangle.set(tempX, rabbitSprite.getY(), rabbitSprite.getWidth(), rabbitSprite.getHeight());
        if (rabbitRectangle.overlaps(wand)) {
            tempX = rabbitSprite.getX();
        }

        rabbitRectangle.set(rabbitSprite.getX(), tempY, rabbitSprite.getWidth(), rabbitSprite.getHeight());
        if (rabbitRectangle.overlaps(wand)) {
            tempY = rabbitSprite.getY();
        }

        rabbitRectangle.set(tempX, tempY, rabbitSprite.getWidth(), rabbitSprite.getHeight());
        if (rabbitRectangle.overlaps(wand)) {
            tempX = rabbitSprite.getX() - roll * SCALE_FACTOR * 0.5f;
            tempY = rabbitSprite.getY() - pitch * SCALE_FACTOR * 0.5f;

            tempX = MathUtils.clamp(tempX, 0, viewport.getWorldWidth() - rabbitSprite.getWidth());
            tempY = MathUtils.clamp(tempY, 0, viewport.getWorldHeight() - rabbitSprite.getHeight());
        }

        rabbitSprite.setPosition(tempX, tempY);
    }


    //collision-handling --> wegen Karottenproblem Work in Progress
/*
    private void handleRabbitholeCollision(float newX, float newY, float roll, float pitch) {
        float tempX = newX;
        float tempY = newY;

        rabbitRectangle.set(tempX, rabbitSprite.getY(), rabbitSprite.getWidth(), rabbitSprite.getHeight());
        if (rabbitRectangle.overlaps(rabbitholeRectangle)) {
            tempX = rabbitSprite.getX();
        }

        rabbitRectangle.set(rabbitSprite.getX(), tempY, rabbitSprite.getWidth(), rabbitSprite.getHeight());
        if (rabbitRectangle.overlaps(rabbitholeRectangle)) {
            tempY = rabbitSprite.getY();
        }

        rabbitRectangle.set(tempX, tempY, rabbitSprite.getWidth(), rabbitSprite.getHeight());
        if (rabbitRectangle.overlaps(rabbitholeRectangle)) {
            tempX = rabbitSprite.getX() - roll * SCALE_FACTOR * 0.5f;
            tempY = rabbitSprite.getY() - pitch * SCALE_FACTOR * 0.5f;

            tempX = MathUtils.clamp(tempX, 0, viewport.getWorldWidth() - rabbitSprite.getWidth());
            tempY = MathUtils.clamp(tempY, 0, viewport.getWorldHeight() - rabbitSprite.getHeight());
        }

        rabbitSprite.setPosition(tempX, tempY);
    }
    */

    public void logic() {
        rabbitRectangle.set(rabbitSprite.getX(), rabbitSprite.getY(), rabbitSprite.getWidth(), rabbitSprite.getHeight());

        // Prüfen, ob alle Karotten eingesammelt wurden
        for (int i = karottenSprites.size - 1; i >= 0; i--) {
            if (rabbitRectangle.overlaps(karottenRectangles.get(i))) {
                rabbitholeSound.play();
                karottenSprites.removeIndex(i);
                karottenRectangles.removeIndex(i);
            }
        }

        // Kollisionshandling für das Loch aktivieren, wenn alle Karotten eingesammelt sind
        if (karottenSprites.isEmpty()) {
            if (rabbitRectangle.overlaps(rabbitholeRectangle)) {
                rabbitholeSound.play();
                Rabbithole.spielZeit = spielZeitGame;
                game.setScreen(new LvlFinished(game));
                music.dispose();
            }
        }
    }


    public void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.draw(wandTexture, wand.x, wand.y, wand.width, wand.height);

        rabbitSprite.draw(spriteBatch);
        rabbitholeSprite.draw(spriteBatch);

        for (Sprite karotte : karottenSprites) {
            karotte.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    /*
    private void spawnKarotten() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        for (int i = 0; i < 10; i++) {
            Sprite karotte = new Sprite(karottenTexture);
            karotte.setSize(0.5f, 0.5f);

            Rectangle karotteRectangle;
            do {
                karotte.setX(MathUtils.random(0, worldWidth - karotte.getWidth()));
                karotte.setY(MathUtils.random(0, worldHeight - karotte.getHeight()));

                karotteRectangle = new Rectangle(
                    karotte.getX(), karotte.getY(), karotte.getWidth(), karotte.getHeight()
                );
            } while (karotteRectangle.overlaps(wand));

            karottenSprites.add(karotte);
            karottenRectangles.add(karotteRectangle);
        }
    }
    */

    private void spawnKarotten() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        for (int i = 0; i < 10; i++) {
            Sprite karotte = new Sprite(karottenTexture);
            karotte.setSize(1f, 1f); // Größe auf eine Welt-Einheit setzen

            Rectangle karotteRectangle;
            do {
                karotte.setX(MathUtils.random(0, worldWidth - karotte.getWidth()));
                karotte.setY(MathUtils.random(0, worldHeight - karotte.getHeight()));

                karotteRectangle = new Rectangle(
                    karotte.getX(), karotte.getY(), karotte.getWidth(), karotte.getHeight()
                );
            } while (karotteRectangle.overlaps(wand)); // Karotte nicht in der Wand spawnen lassen

            karottenSprites.add(karotte);
            karottenRectangles.add(karotteRectangle);
        }
    }


    private void spawnRabbithole() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        do {
            rabbitholeSprite.setX(MathUtils.random(0, worldWidth - rabbitholeSprite.getWidth()));
            rabbitholeSprite.setY(MathUtils.random(0, worldHeight - rabbitholeSprite.getHeight()));

            rabbitholeRectangle.set(
                rabbitholeSprite.getX(), rabbitholeSprite.getY(),
                rabbitholeSprite.getWidth(), rabbitholeSprite.getHeight()
            );
        } while (rabbitholeRectangle.overlaps(wand));
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        rabbitTexture.dispose();
        rabbitholeTexture.dispose();
        karottenTexture.dispose();
        rabbitholeSound.dispose();
        music.dispose();
        spriteBatch.dispose();
        wandTexture.dispose();
    }
}
