package akostenko.aicars;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.lwjgl.input.Keyboard.KEY_ESCAPE;
import static org.lwjgl.input.Keyboard.KEY_Q;
import static org.lwjgl.input.Keyboard.KEY_R;

import akostenko.aicars.keyboard.ComboKeyAction;
import akostenko.aicars.keyboard.GameAction;
import akostenko.aicars.menu.Mode;
import akostenko.aicars.track.Track;
import akostenko.aicars.keyboard.KeyboardHelper;
import akostenko.aicars.keyboard.SingleKeyAction;
import akostenko.aicars.menu.WithPlayer;
import akostenko.aicars.track.StraightTrack;

import org.newdawn.slick.KeyListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameSettings {

    private static final Path settingsPath = Paths.get("settings.ini");
    private static final String trackToken = "track=";
    private static final String modeToken = "mode=";

    private static GameSettings instance;

    private Iterable<KeyListener> globalListeners;
    private Track track = new StraightTrack();
    private Mode mode = new WithPlayer();

    public static GameSettings get() {
        if (instance == null) {
            instance = restore();
        }
        return instance;
    }

    private static GameSettings restore() {
        GameSettings gameSettings;

        if (!Files.exists(settingsPath)) {
            gameSettings = getDefaultSettings();
            gameSettings.save();
        } else {
            gameSettings = fromFile(settingsPath);
        }

        return gameSettings;
    }

    private static GameSettings fromFile(Path settingsPath) {
        GameSettings settings = getDefaultSettings();
        try {
            Files.readAllLines(settingsPath)
                    .forEach(line -> {
                        if (line.startsWith(trackToken)) {
                            settings.track = Track.forName(line.split(trackToken)[1]);
                        }
                        if (line.startsWith(modeToken)) {
                            settings.mode = Mode.forName(line.split(modeToken)[1]);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return settings;
    }

    private static GameSettings getDefaultSettings() {
        GameSettings defaults = new GameSettings();
        Collection<KeyListener> defaultBindings = new ArrayList<>();
        defaultBindings.add(new SingleKeyAction(v -> Game.get().noticeAction(GameAction.QUIT), KEY_ESCAPE));
        defaultBindings.addAll(new ComboKeyAction(v -> Game.get().noticeAction(GameAction.QUIT), KEY_Q, v -> KeyboardHelper.isCtrlDown())
                .listeners());
        defaultBindings.addAll(new ComboKeyAction(v -> Game.get().noticeAction(GameAction.RESTART), KEY_R, v -> KeyboardHelper.isCtrlDown())
                .listeners());

        defaults.globalListeners = defaultBindings;

        return defaults;
    }

    public void save() {
        List<String> content = new ArrayList<>();
        content.add(trackToken + track.getTitle());
        content.add(modeToken + mode.getTitle());
        try {
            if (Files.exists(settingsPath)) {
                Files.write(settingsPath, content, TRUNCATE_EXISTING, WRITE);
            } else {
                Files.write(settingsPath, content, CREATE_NEW, WRITE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<KeyListener> getGlobalListeners() {
        return globalListeners;
    }

    public Track getTrack() {
        return track;
    }

    public GameSettings setTrack(Track track) {
        this.track = track;
        return this;
    }

    public GameSettings setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public Mode getMode() {
        return mode;
    }
}
