package com.github.rabbit.Lvl3;

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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.rabbit.LvlFinished;
import com.github.rabbit.Rabbithole;

public class Lvl3Game implements Screen {
    final Rabbithole game;

    Texture backgroundTexture;
    Texture rabbitTexture;
    Texture rabbitholeTexture;
    Sound rabbitholeSound;
    Music music;

    SpriteBatch spriteBatch; // Zum Zeichnen von Sprites
    FitViewport viewport;   // Kamera mit skalierbarer Ansicht

    Sprite rabbitSprite;    // Das Kaninchen
    Sprite rabbitholeSprite;      // Das Loch (einzelner Sprite)

    Rectangle rabbitRectangle; // Rechteck für Kollisionserkennung des Kaninchens
    Rectangle rabbitholeRectangle;   // Rechteck für Kollisionserkennung des Lochs

    int spawnCount = 0; // Zähler für die Anzahl der bisher gespawnten Löcher
    int maxSpawns = 5;  // Maximale Anzahl an Löchern

    private float spielZeitGame; //Zeiterfassung deklarieren

    Texture wandTexture; //Wand
    private Rectangle wand;


    // Konfigurierbare Konstanten für Bewegungssteuerung
    private static final float SCALE_FACTOR = 0.04f; // Bewegungsskalierung // Bewegungsskalierung reduziert (je weiter runter, desto langsamer)
    private static final float ALPHA = 0.1f; // Gleitender Durchschnitt // Glättung verstärkt

    // Letzte Position des Kaninchens
    private float lastX, lastY;

    public Lvl3Game(final Rabbithole game) {
        this.game = game;

        // Texturen laden
        backgroundTexture = new Texture("yellow.png");
        rabbitTexture = new Texture("rabbit.png");
        rabbitholeTexture = new Texture("hole.png");

        wandTexture = new Texture(Gdx.files.internal("wandlvl1.png"));

        // Sounds laden
        rabbitholeSound = Gdx.audio.newSound(Gdx.files.internal("rabbithole.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("8 Bit Epic GameBoy! HeatleyBros.mp3"));

        // SpriteBatch und Viewport initialisieren
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(18, 10); // Virtuelle Weltgröße (Breite x Höhe)


        // Wand kann erst nach viewport initialisiert werden, aus logischen Gründen
        wand = new Rectangle(
            viewport.getWorldWidth() * 1 / 4,  // x-Position
            //0,                             // y-Position
            viewport.getWorldHeight() * 1 / 6, //y-position
            0.3f,                             // Breite
            viewport.getWorldHeight() * 4 / 6    // Höhe
        );

        // Kaninchen-Sprite initialisieren und skalieren
        rabbitSprite = new Sprite(rabbitTexture);
        rabbitSprite.setSize(1, 1); // 1x1 Einheiten in der Welt
        rabbitSprite.setCenter(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f);

        // Loch-Sprite initialisieren und skalieren
        rabbitholeSprite = new Sprite(rabbitholeTexture);
        rabbitholeSprite.setSize(2, 2); // 2x2 Einheiten in der Welt

        // Rechtecke für Kollisionserkennung
        rabbitRectangle = new Rectangle();
        rabbitholeRectangle = new Rectangle();

        // Musik konfigurieren und starten
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

        // Startposition speichern
        lastX = rabbitSprite.getX();
        lastY = rabbitSprite.getY();

        //Startzeit initialisieren
        //Rabbithole.startZeit = System.currentTimeMillis(); --> führt zu problemen deswegen LibDX-eigene Lösung
        spielZeitGame = 0; //Zeiterfassung initiaisieren

        // Erstes Loch spawnen
        spawnRabbithole();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        spielZeitGame += delta; //libGDX-eigene Spielerfassung --> delta  macht, dass Zeit aufallen Geräten etwas mehr angepasst wird --> weniger Vorteile durch schnellere Geräte
        input();  // Eingabe verarbeiten
        logic();  // Logik und Kollisionserkennung
        draw();   // Zeichnen der Objekte
    }

    @Override
    public void resize(int width, int height) {
        // Kamera an neue Fenstergröße anpassen
        viewport.update(width, height, true);
    }

    public void input() {
        // Bewegungsmesser-Eingabe
        float ax = Gdx.input.getAccelerometerX();
        float ay = Gdx.input.getAccelerometerY();
        float az = Gdx.input.getAccelerometerZ();

        // Neigungswinkel berechnen
        float pitch = (float) Math.atan2(-ax, az) * 180.0f / (float) Math.PI;
        float roll = (float) Math.atan2(ay, Math.sqrt(ax * ax + az * az)) * 180.0f / (float) Math.PI;

        // Neue Position berechnen
        float newX = rabbitSprite.getX() + roll * SCALE_FACTOR;
        float newY = rabbitSprite.getY() + pitch * SCALE_FACTOR;

        // Begrenzung der Position innerhalb der sichtbaren Welt
        newX = MathUtils.clamp(newX, 0, viewport.getWorldWidth() - rabbitSprite.getWidth());
        newY = MathUtils.clamp(newY, 0, viewport.getWorldHeight() - rabbitSprite.getHeight());

        // Rechteck des Kaninchens aktualisieren
        rabbitRectangle.set(newX, newY, rabbitSprite.getWidth(), rabbitSprite.getHeight());

        // Wandkollision behandeln
        handleWallCollision(newX, newY, roll, pitch);

        // Letzte Position speichern
        lastX = rabbitSprite.getX();
        lastY = rabbitSprite.getY();
    }


    /*
    private float applyDeadzone(float value, float threshold) {
        if (Math.abs(value) < threshold) {
            return 0;
        }
        return value - Math.signum(value) * threshold;
    }
    */


    private void handleWallCollision(float newX, float newY, float roll, float pitch) {
        // Schrittweise Bewegung prüfen
        float tempX = newX;
        float tempY = newY;

        rabbitRectangle.set(tempX, rabbitSprite.getY(), rabbitSprite.getWidth(), rabbitSprite.getHeight());
        if (rabbitRectangle.overlaps(wand)) {
            tempX = rabbitSprite.getX(); // Blockiere X-Bewegung
        }

        rabbitRectangle.set(rabbitSprite.getX(), tempY, rabbitSprite.getWidth(), rabbitSprite.getHeight());
        if (rabbitRectangle.overlaps(wand)) {
            tempY = rabbitSprite.getY(); // Blockiere Y-Bewegung
        }

        // Prüfen, ob nach der Anpassung noch eine Kollision vorliegt
        rabbitRectangle.set(tempX, tempY, rabbitSprite.getWidth(), rabbitSprite.getHeight());
        if (rabbitRectangle.overlaps(wand)) {
            // Rückstoß bei verbleibender Kollision
            tempX = rabbitSprite.getX() - roll * SCALE_FACTOR * 0.5f;
            tempY = rabbitSprite.getY() - pitch * SCALE_FACTOR * 0.5f;

            // Begrenzungen anwenden
            tempX = MathUtils.clamp(tempX, 0, viewport.getWorldWidth() - rabbitSprite.getWidth());
            tempY = MathUtils.clamp(tempY, 0, viewport.getWorldHeight() - rabbitSprite.getHeight());
        }

        // Position aktualisieren
        rabbitSprite.setPosition(tempX, tempY);
    }


    public void logic() {
        // Rechteck des Kaninchens aktualisieren
        rabbitRectangle.set(rabbitSprite.getX(), rabbitSprite.getY(), rabbitSprite.getWidth(), rabbitSprite.getHeight());

        // Rechteck des Lochs aktualisieren
        rabbitholeRectangle.set(rabbitholeSprite.getX(), rabbitholeSprite.getY(), rabbitholeSprite.getWidth(), rabbitholeSprite.getHeight());

        //Kollisionserkenung: wenn das Kanninchen die Wandberührt

        if (rabbitRectangle.overlaps(wand)) {
            // Zurück zur letzten erlaubten Position
            rabbitSprite.setPosition(lastX, lastY);
        }

        // Kollisionserkennung: Wenn das Kaninchen das Loch berührt
        if (rabbitRectangle.overlaps(rabbitholeRectangle)) {
            rabbitholeSound.play();  // Soundeffekt abspielen
            spawnCount++;      // Zähler erhöhen
            if (spawnCount < maxSpawns) {
                spawnRabbithole();   // Nächstes Loch spawnen
            } else {

                //Rabbithole.spielZeit = (System.currentTimeMillis() - Rabbithole.startZeit) / 1000f; //Umrechnng in normale Zeit (von Milisekunden aus) //--> problematisch und ungeau!!
                Rabbithole.spielZeit = spielZeitGame;
                game.setScreen(new LvlFinished(game));
                music.dispose();
                //evtl dispose(); //--> testen

            }
        }
    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();


        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // Hintergrund
        spriteBatch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        // Wand z
        spriteBatch.draw(wandTexture, wand.x, wand.y, wand.width, wand.height);
        // Kaninchen
        rabbitSprite.draw(spriteBatch);

        // Loch zeichnen, wenn noch nicht alle gespawnt wurden
        if (spawnCount < maxSpawns) {
            rabbitholeSprite.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    private void spawnRabbithole() {

        /*
        // Sichtbare Weltgrenzen (Viewport)
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Loch zufällig positionieren, aber innerhalb der sichtbaren Welt
        rabbitholeSprite.setX(MathUtils.random(0, worldWidth - rabbitholeSprite.getWidth()));
        rabbitholeSprite.setY(MathUtils.random(0, worldHeight - rabbitholeSprite.getHeight()));
        */




        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        do {
            rabbitholeSprite.setX(MathUtils.random(0, worldWidth - rabbitholeSprite.getWidth()));
            rabbitholeSprite.setY(MathUtils.random(0, worldHeight - rabbitholeSprite.getHeight()));

            // Rechteck des Lochs aktualisieren
            rabbitholeRectangle.set(rabbitholeSprite.getX(), rabbitholeSprite.getY(), rabbitholeSprite.getWidth(), rabbitholeSprite.getHeight());
        } while (rabbitholeRectangle.overlaps(wand));  // Falls es die Wand überlappt, nochmal!
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
        // Ressourcen freigeben
        backgroundTexture.dispose();
        rabbitTexture.dispose();
        rabbitholeTexture.dispose();
        rabbitholeSound.dispose();
        music.dispose();
        spriteBatch.dispose();
        wandTexture.dispose();
    }
}

