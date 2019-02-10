/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.score.BilliardScore;
import billiard.model.Match;
import billiard.model.PlayerMatchResult;
import billiard.model.Player;
import billiard.model.ScoreChange;
import billiard.common.AppProperties;
import billiard.common.CommonDialogs;
import billiard.common.ControllerInterface;
import billiard.common.PermittedValues;
import billiard.common.SceneUtil;
import billiard.model.MatchManager;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author jean
 */
public class ScoreBoardController implements Initializable, ControllerInterface {
    private static final Logger LOGGER = Logger.getLogger(ScoreBoardController.class.getName());
    private Stage primaryStage;

    private final ArrayList<String> bannerURLs = new ArrayList<>();
    private int bannerIdx;
    private Timer bannerTimer;

    private int turn;
    int currentRun = 0;
    private boolean equalizingInning;
    private int winner;
    private Match match;
    
    private ArrayList<Integer> runPlayer1 = new ArrayList<>();
    private ArrayList<Integer> totalPlayer1 = new ArrayList<>();
    private ArrayList<Integer> runPlayer2 = new ArrayList<>();
    private ArrayList<Integer> totalPlayer2 = new ArrayList<>();
    private boolean correctionOngoing = false;
    private boolean scoreboardClosing = false;
    private boolean helpShowing = false;
    private boolean warmingup = false;
    private boolean scoresheetShowing = false;
    private boolean undoOngoing = false;
    private int warmingUpTime = 0;
    private MatchManager matchManager;

    private ResourceBundle bundle;

    @FXML
    private Label player_1_name;
    @FXML
    private Label player_1_club;
    @FXML
    private Label player_2_club;
    @FXML
    private Label player_2_name;
    @FXML
    public Label player_1_score;
    @FXML
    private Label player_1_pts;
    @FXML
    public Label player_1_hr;
    @FXML
    public Label player_1_avg;
    @FXML
    private Circle player_1_turn_indicator;
    @FXML
    private Circle player_2_turn_indicator;
    @FXML
    public Label player_2_avg;
    @FXML
    public Label player_2_hr;
    @FXML
    private Label player_2_pts;
    @FXML
    public Label player_2_score;
    @FXML
    private Label innings;
    @FXML
    private Label player_1_discipline;
    @FXML
    private Label player_2_discipline;
    @FXML
    private TextField run;
    @FXML
    public Text inning11;
    @FXML
    public Text inning12;
    @FXML
    public Text inning13;
    @FXML
    public Text score11;
    @FXML
    public Text score12;
    @FXML
    public Text score13;
    @FXML
    public Text total11;
    @FXML
    public Text total12;
    @FXML
    public Text total13;
    @FXML
    public Text inning21;
    @FXML
    public Text inning22;
    @FXML
    public Text inning23;
    @FXML
    public Text score21;
    @FXML
    public Text score22;
    @FXML
    public Text score23;
    @FXML
    public Text total21;
    @FXML
    public Text total22;
    @FXML
    public Text total23;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView banner_img;
    @FXML
    private HBox extraInfo1;
    @FXML
    private HBox extraInfo2;
    @FXML
    private ProgressBar player_1_progress;
    @FXML
    private ProgressBar player_2_progress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            String strLocale = AppProperties.getInstance().getLocale();
            Locale locale = new Locale(strLocale);
            bundle = ResourceBundle.getBundle("languages.lang", locale);

            matchManager = MatchManager.getInstance();

            loadBanners();

            equalizingInning = false;
            winner = 0;

            // Initialize player 1
            player_1_score.setText("0");
            player_1_avg.setText("0");
            player_1_hr.setText("0");
            player_1_progress.setProgress(0);

            // Initialize player 2
            player_2_score.setText("0");
            player_2_avg.setText("0");
            player_2_hr.setText("0");
            player_2_progress.setProgress(0);

            // Initialize innings and currentRun
            innings.setText("0");
            run.setText("0");

            turn = 1;
            switchTurnIndicator();

            run.requestFocus();
            run.selectAll();
        } catch (Exception ex) {
            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
            CommonDialogs.showException(ex);
        }
    }

    @FXML
    private void runOnKeyPressed(KeyEvent event) throws Exception {
        LOGGER.log(Level.FINEST, "runOnKeyPressed => event code: {0}", event.getCode());
        if (new KeyCodeCombination(KeyCode.ENTER).match(event)) {
            LOGGER.log(Level.FINEST, "runOnKeyPressed => ENTER");
            if (!match.isEnded()) {
                endOfTurn();
            }
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
            LOGGER.log(Level.FINEST, "runOnKeyPressed => C");
            if (!scoreboardClosing) {
                scoreboardClosing = true;
                closeBoard();
            } else {
                scoreboardClosing = false;
            }
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
            LOGGER.log(Level.FINEST, "runOnKeyPressed => S");
            if (!match.isStarted()) {
                switchPlayers();
            }
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN).match(event)) {
            LOGGER.log(Level.FINEST, "runOnKeyPressed => W");
            if (!match.isStarted()) {
                if (!warmingup) {
                    warmingup = true;
                    startWarmingUp();
                } else {
                    warmingup = false;
                }
            }
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN).match(event)) {
            LOGGER.log(Level.FINEST, "runOnKeyPressed => I");
            extraInfo1.setVisible(!extraInfo1.isVisible());
            extraInfo2.setVisible(!extraInfo2.isVisible());
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN).match(event)) {
            LOGGER.log(Level.FINEST, "runOnKeyPressed => H");
            if (!helpShowing) {
                helpShowing = true;
                showHelp();
            } else {
                helpShowing = false;
            }
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN).match(event)) {
            LOGGER.log(Level.FINEST, "runOnKeyPressed => E");
            if (!match.isEnded() && match.isStarted() && !correctionOngoing) {
                correctScore();
                correctionOngoing = true;
            } else {
                correctionOngoing = false;
            }
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN).match(event)) {
            LOGGER.log(Level.FINEST, "runOnKeyPressed => M");
            if (!match.isEnded() && !scoresheetShowing) {
                scoresheetShowing = true;
                showScoreSheet();
            } else {
                scoresheetShowing = false;
            }
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN).match(event)) {
            LOGGER.log(Level.FINEST, "runOnKeyPressed => U");
            if (!match.isEnded() && !undoOngoing) {
                undoOngoing = true;
                undoTries();
            } else {
                undoOngoing = false;
            }
            event.consume();
        }
    }

    @FXML
    private void runOnKeyTyped(KeyEvent event) throws Exception {
        LOGGER.log(Level.FINEST, "runOnKeyTyped => event char: {0}", event.getCharacter());
        switch (event.getCharacter()) {
            case "+":
                LOGGER.log(Level.FINEST, "runOnKeyTyped => event code: +");
                incrementRun();
                event.consume();
                break;
            case "-":
                LOGGER.log(Level.FINEST, "runOnKeyTyped => event code: -");
                decrementRun();
                event.consume();
                break;
            case "/":
                event.consume();
                break;
        }
    }

    private int getRun() {
        return Integer.parseInt(run.getText());
    }

    private int getScorePlayer1() {
        return Integer.parseInt(player_1_score.getText());
    }

    private int getHRPlayer1() {
        return Integer.parseInt(player_1_hr.getText());
    }

    private int getScorePlayer2() {
        return Integer.parseInt(player_2_score.getText());
    }

    private int getHRPlayer2() {
        return Integer.parseInt(player_2_hr.getText());
    }

    private int getInnings() {
        return Integer.parseInt(innings.getText());
    }

    private void closeBoard() throws Exception {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UTILITY);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(BilliardScore.FXML.OK_CANCEL), bundle);
        Parent root;
        root = loader.load();

        OKCancelDialogController controller = loader.<OKCancelDialogController>getController();
        controller.setMessage(bundle.getString("msg.wedstrijd.annuleren"));
        controller.initController(dialog);
        controller.setMode(OKCancelDialogController.Mode.OKCANCEL);

        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();

        if (controller.getAnswer().equalsIgnoreCase("OK")) {
            match.cancel();
            matchManager.updateMatch(match);
            if (bannerTimer != null) {
                bannerTimer.cancel();
            }
            primaryStage.hide();
        }
    }

    private void switchTurnIndicator() {
        if (turn == 1) {
            player_1_turn_indicator.setVisible(true);
            player_2_turn_indicator.setVisible(false);
        } else if (turn == 2) {
            player_1_turn_indicator.setVisible(false);
            player_2_turn_indicator.setVisible(true);
        }
    }

    private void endOfTurn() throws Exception {
        if (!match.isStarted()) {
            match.start(AppProperties.getInstance().getScoreboardId());
        }

        try {
            currentRun = getRun();
        } catch (NumberFormatException e) {
            displayRunInError();
            return;
        }
        if (turn == 1) {
            int score = getScorePlayer1() + currentRun;
            if (score > match.getPlayer1().getTsp()) {
                displayRunInError();
                return;
            }
            int inningsInt = getInnings() + 1;
            innings.setText(Integer.toString(inningsInt));

            runPlayer1.add(currentRun);
            totalPlayer1.add(score);
            player_1_score.setText(Integer.toString(score));
            int hr = getHRPlayer1();
            if (currentRun > hr) {
                player_1_hr.setText(Integer.toString(currentRun));
            }
            double avg = (double) score / getInnings();
            String dfFormat = "#0.00";
            if (player_1_discipline.getText().contains("Drieband")) {
                dfFormat = "#0.000";
            }
            DecimalFormat formatter = new DecimalFormat(dfFormat);
            player_1_avg.setText(formatter.format(avg));

            setScorePlayer(1, inningsInt, currentRun, score);

            turn = 2;
            
            double pct = (double) score/match.getPlayer1().getTsp().intValue();
            player_1_progress.setProgress(pct);
            if (score == match.getPlayer1().getTsp()) {
                equalizingInning = true;
                winner = 1;
                levelingInning();
            }
        } else if (turn == 2) {
            int score = getScorePlayer2() + currentRun;
            if (score > match.getPlayer2().getTsp()) {
                displayRunInError();
                return;
            }
            runPlayer2.add(currentRun);
            totalPlayer2.add(score);
            player_2_score.setText(Integer.toString(score));
            int hr = getHRPlayer2();
            if (currentRun > hr) {
                player_2_hr.setText(Integer.toString(currentRun));
            }
            double avg = (double) score / getInnings();
            String dfFormat = "#0.00";
            if (player_2_discipline.getText().equalsIgnoreCase("Drieband")) {
                dfFormat = "#0.000";
            }
            DecimalFormat formatter = new DecimalFormat(dfFormat);
            player_2_avg.setText(formatter.format(avg));

            setScorePlayer(2, getInnings(), currentRun, score);

            turn = 1;

            double pct = (double) score/match.getPlayer2().getTsp().intValue();
            player_2_progress.setProgress(pct);
            if (score == match.getPlayer2().getTsp()) {
                if (equalizingInning) {
                    winner = 0;
                } else {
                    winner = 2;
                }
                endOfMatch();
            } else if (equalizingInning) {
                endOfMatch();
            }
        }
        matchManager.updateMatch(match);
        switchTurnIndicator();
        currentRun = 0;
        run.setText(String.valueOf(currentRun));
        run.setStyle("-fxbackground-color:white");
        run.requestFocus();
        run.selectAll();
    }

    private void switchPlayers() {
        match.switchPlayers();
        displayPlayers();
    }

    public void setMatch(Match match) {
        this.match = match;
        displayPlayers();
    }

    public void setTurnIdnicatorsColor(String color) {
        if (color != null && color.equals("WHITE")) {
            // Flip Turnindicator Color
            RadialGradient rgFillp1 = (RadialGradient) player_1_turn_indicator.getFill();
            RadialGradient rgFillp2 = (RadialGradient) player_2_turn_indicator.getFill();
            player_1_turn_indicator.setFill(rgFillp2);
            player_2_turn_indicator.setFill(rgFillp1);
        }
    }

    public void setWarmingUpTime(int warmingUpTime) {
        this.warmingUpTime = warmingUpTime;
    }

    public Match getMatch() {
        return match;
    }

    private void displayPlayers() {
        Player player1 = match.getPlayer1();
        player_1_name.setText(player1.getName());
        player_1_club.setText(player1.getClub());
        player_1_pts.setText(player1.getTsp().toString());
        player_1_discipline.setText(player1.getDiscipline());

        Player player2 = match.getPlayer2();
        player_2_name.setText(player2.getName());
        player_2_club.setText(player2.getClub());
        player_2_pts.setText(player2.getTsp().toString());
        player_2_discipline.setText(player2.getDiscipline());
    }

    private void endOfMatch() throws Exception {
        PlayerMatchResult player1Result
                = new PlayerMatchResult(getScorePlayer1(), getInnings(), getHRPlayer1(),
                        runPlayer1, totalPlayer1);
        player1Result.setPercentage((double)player1Result.getPoints()/getTspPlayer(1)*100);

        PlayerMatchResult player2Result
                = new PlayerMatchResult(getScorePlayer2(), getInnings(), getHRPlayer2(),
                        runPlayer2, totalPlayer2);
        player2Result.setPercentage((double)player2Result.getPoints()/getTspPlayer(2)*100);

        match.setResult(winner, player1Result, player2Result);

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UTILITY);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(BilliardScore.FXML.EOM_DIALOG), bundle);
        Parent root;
        root = loader.load();

        EndOfMatchController controller = loader.<EndOfMatchController>getController();
        controller.setData(match);
        controller.initController(dialog);

        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.show();
        PauseTransition wait = new PauseTransition(Duration.seconds(5));
        wait.setOnFinished((e) -> {
            dialog.hide();
            wait.playFromStart();
        });
        wait.play();

        if (controller.getReply().equals(EndOfMatchController.Reply.CORRECT)) {
            if (!correctScore()) {
                primaryStage.hide();
            }
        } else {
            matchManager.updateMatch(match);
            if (bannerTimer != null) {
                bannerTimer.cancel();
            }
            primaryStage.hide();
        }

    }

    private void levelingInning() throws Exception {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UTILITY);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(BilliardScore.FXML.LEVELING_TURN_DIALOG), bundle);
        Parent root;
        root = loader.load();

        LevelingTurnController controller = loader.<LevelingTurnController>getController();
        int intPoints = match.getPlayer2().getTsp() - getScorePlayer2();

        controller.setData(match.getPlayer2(), intPoints);
        controller.initController(dialog);

        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.show();
        PauseTransition wait = new PauseTransition(Duration.seconds(5));
        wait.setOnFinished((e) -> {
            dialog.close();
            wait.playFromStart();
        });
        wait.play();
    }

    private void setScorePlayer(int player, int inning, int score, int total) throws Exception {
        int pos;
        if (inning < 4) {
            pos = inning;
        } else {
            pos = 3;
            shiftScoreUp(player, 2);
            shiftScoreUp(player, 3);
        }
        setScorePlayerPos(player, pos, inning, score, total);
    }

    private void shiftScoreUp(int player, int pos) throws Exception {
        Text textInning = (Text) ScoreBoardController.class.getField("inning" + player + pos).get(this);
        int intInning = Integer.parseInt(textInning.getText());
        Text textScore = (Text) ScoreBoardController.class.getField("score" + player + pos).get(this);
        int intScore = Integer.parseInt(textScore.getText());
        Text textTotal = (Text) ScoreBoardController.class.getField("total" + player + pos).get(this);
        int intTotal = Integer.parseInt(textTotal.getText());
        setScorePlayerPos(player, pos - 1, intInning, intScore, intTotal);
    }

    private void setScorePlayerPos(int player, int pos, int inning, int score, int total) throws Exception {
        Text textInning = (Text) ScoreBoardController.class.getField("inning" + player + pos).get(this);
        textInning.setText(Integer.toString(inning));
        Text textScore = (Text) ScoreBoardController.class.getField("score" + player + pos).get(this);
        textScore.setText(Integer.toString(score));
        Text textTotal = (Text) ScoreBoardController.class.getField("total" + player + pos).get(this);
        textTotal.setText(Integer.toString(total));
    }

    private boolean correctScore() throws Exception {
        boolean scoreCorrected = false;
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UTILITY);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(BilliardScore.FXML.CHANGE_SCORE), bundle);
        Parent root;
        root = loader.load();

        ChangeScoreController controller = loader.<ChangeScoreController>getController();
        controller.setData(match, getInnings());
        controller.initController(dialog);

        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();

        if (controller.getScoreChange() != null) {
            ScoreChange scoreChange = controller.getScoreChange();
            if (scoreChange.getPlayer().getLicentie().equals(match.getPlayer1().getLicentie())) {
                playerScoreCorrection(1, scoreChange, runPlayer1, totalPlayer1);
                if (getScorePlayer1() == match.getPlayer1().getTsp()) {
                    equalizingInning = true;
                    winner = 1;
                    levelingInning();
                } else if (equalizingInning) {
                    equalizingInning = false;
                    winner = 0;
                }
            }
            if (scoreChange.getPlayer().getLicentie().equals(match.getPlayer2().getLicentie())) {
                playerScoreCorrection(2, scoreChange, runPlayer2, totalPlayer2);
                if (getScorePlayer2() == match.getPlayer2().getTsp()) {
                    if (equalizingInning) {
                        winner = 0;
                    } else {
                        winner = 2;
                    }
                    endOfMatch();
                } else if (equalizingInning) {
                    endOfMatch();
                } else if (match.isEnded()) {
                    match.correct();
                    matchManager.updateMatch(match);
                }
            }
            scoreCorrected = true;
        }
        return scoreCorrected;
    }

    private void playerScoreCorrection(int player, ScoreChange scoreChange, ArrayList<Integer> runPlayer, ArrayList<Integer> totalPlayer) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, Exception {
        int idx = scoreChange.getInning() - 1;
        int intDiff = scoreChange.getPoints() - runPlayer.get(idx);
        runPlayer.set(idx, scoreChange.getPoints());
        int hr = 0;
        for (int i = 0; i < totalPlayer.size(); i++) {
            if (i >= idx) {
                totalPlayer.set(i, totalPlayer.get(i) + intDiff);
            }
            if (runPlayer.get(i) > hr) {
                hr = runPlayer.get(i);
                setLblHR(player, hr);
            }
        }
        setLblScore(player, getScorePlayer(player) + intDiff);
        double avg = (double) getScorePlayer(player) / getInnings();
        DecimalFormat formatter = new DecimalFormat("#0.00");
        setTFAVG(player, formatter.format(avg));

        if (scoreChange.getInning() + 3 > getInnings()) {
            int start = (runPlayer.size() < 4 ? 0 : runPlayer.size() - 3);
            int pos = 0;
            for (int i = start; i < runPlayer.size(); i++) {
                pos++;
                setScorePlayerPos(player, pos, i + 1, runPlayer.get(i), totalPlayer.get(i));
            }
        }
    }

    private void showHelp() throws Exception {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UTILITY);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(BilliardScore.FXML.SHOW_HELP), bundle);
        Parent root;
        root = loader.load();

        ScoreboardHelpController controller = loader.<ScoreboardHelpController>getController();
        controller.initController(dialog);

        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();
    }

    private void loadBanners() throws Exception {
        AppProperties prop = AppProperties.getInstance();

        java.nio.file.Path dir = Paths.get(prop.getBannerLocation());
        DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(dir, "*.jpg");
        for (java.nio.file.Path entry : stream) {
            //logger.log(Level.FINEST, "loadBanners => entry: {0}", entry.toString());
            bannerURLs.add("file:" + entry.toString());
        }

        if (bannerURLs.isEmpty()) {
            //logger.log(Level.FINEST, "loadBanners => no banners");
            banner_img.setVisible(false);
            return;
        }

        String imgURL = bannerURLs.get(0);
        if (imgURL != null) {
            //logger.log(Level.FINEST, "loadBanners => set first banner: {0}", imgURL);
            banner_img.setImage(new Image(imgURL));
        }

        if (bannerURLs.size() > 1) {
            //logger.log(Level.FINEST, "loadBanners => more then one banner");
            bannerIdx = 1;
            bannerTimer = new Timer();
            bannerTimer.schedule(
                    new TimerTask() {

                @Override
                public void run() {
                    String imgURL = bannerURLs.get(bannerIdx);
                    if (imgURL != null) {
                        //logger.log(Level.FINEST, "loadBanners => set next banner: {0}", imgURL);
                        banner_img.setImage(new Image(imgURL));
                    }
                    bannerIdx++;
                    if (bannerIdx == bannerURLs.size()) {
                        bannerIdx = 0;
                    }
                }
            }, 0, 10000);
        }
    }

    @Override
    public void initController(Stage stage) {
        primaryStage = stage;
    }

    private void setLblScore(int player, int score) throws Exception {
        Label label = (Label) ScoreBoardController.class.getField("player_" + player + "_score").get(this);
        label.setText(Integer.toString(score));
    }

    private void setLblHR(int player, int hr) throws Exception {
        Label label = (Label) ScoreBoardController.class.getField("player_" + player + "_hr").get(this);
        label.setText(Integer.toString(hr));
    }

    private void setTFAVG(int player, String avg) throws Exception {
        Label label = (Label) ScoreBoardController.class.getField("player_" + player + "_avg").get(this);
        label.setText(avg);
    }

    private int getHRPlayer(int player) throws Exception {
        Label label = (Label) ScoreBoardController.class.getField("player_" + player + "_hr").get(this);
        return Integer.parseInt(label.getText());
    }

    private int getScorePlayer(int player) throws Exception {
        Label label = (Label) ScoreBoardController.class.getField("player_" + player + "_score").get(this);
        return Integer.parseInt(label.getText());
    }

    private void displayRunInError() {
        run.setStyle("-fx-background-color:red");
        run.requestFocus();
        run.selectAll();
    }

    private void incrementRun() throws Exception {
        LOGGER.log(Level.FINEST, "incrementRun");
        currentRun++;
        LOGGER.log(Level.FINEST, "incrementRun => currentrun: {0}", currentRun);
        LOGGER.log(Level.FINEST, "incrementRun => String.valueOf(currentRun): {0}", String.valueOf(currentRun));
        run.setText(String.valueOf(currentRun));
        run.requestFocus();
        run.selectAll();
    }

    private void decrementRun() throws Exception {
        LOGGER.log(Level.FINEST, "decrementRun");
        currentRun--;
        LOGGER.log(Level.FINEST, "decrementRun => currentrun: {0}", currentRun);
        LOGGER.log(Level.FINEST, "decrementRun => String.valueOf(currentRun): {0}", String.valueOf(currentRun));
        run.setText(String.valueOf(currentRun));
        run.requestFocus();
        run.selectAll();
    }

    private void startWarmingUp() throws Exception {
        LOGGER.log(Level.FINEST, "startWarmingUp");
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(BilliardScore.FXML.WARMING_UP), bundle);
        Parent root;
        root = loader.load();

        WarmingUpCountDownController controller = loader.<WarmingUpCountDownController>getController();
        controller.initController(dialog);
        controller.setWarmingUpTime(warmingUpTime);

        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();
    }

    private void showScoreSheet() throws Exception {
        PlayerMatchResult player1Result
                = new PlayerMatchResult(getScorePlayer1(), getInnings(), getHRPlayer1(),
                        runPlayer1, totalPlayer1);

        PlayerMatchResult player2Result
                = new PlayerMatchResult(getScorePlayer2(), getInnings(), getHRPlayer2(),
                        runPlayer2, totalPlayer2);

        match.setIntermediatResult(player1Result, player2Result);
        ScoreSheetController.showScoreSheet(match, PermittedValues.Mode.READ_ONLY);
    }

    private int getTspPlayer(int player) {
        int result = 0;
        if (player == 1) {
            result = match.getPlayer1().getTsp();
        } else if (player == 2) {
            result = match.getPlayer2().getTsp();
        }
        return result;
    }

    private String getDisciplinePlayer(int player) {
        String result = "";
        if (player == 1) {
            result = match.getPlayer1().getDiscipline();
        } else if (player == 2) {
            result = match.getPlayer2().getDiscipline();
        }
        return result;
    }

    private void undoTries() throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        SceneUtil.setStylesheet(dialog.getDialogPane());
        dialog.setTitle(bundle.getString("titel.undo.innings"));
        dialog.setHeaderText(null);
        dialog.setContentText(bundle.getString("label.nbr.innings"));

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            int nbrOfInnings;
            try {
                nbrOfInnings = Integer.valueOf(result.get());
            } catch (NumberFormatException e) {
                return;
            }
            if(nbrOfInnings > getInnings()) {
                nbrOfInnings = getInnings();
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            SceneUtil.setStylesheet(alert.getDialogPane());
            alert.setTitle(bundle.getString("titel.undo.innings"));
            alert.setHeaderText(null);
            alert.setContentText(nbrOfInnings + " " + bundle.getString("msg.undo.innings"));

            Optional<ButtonType> result2 = alert.showAndWait();
            if (result2.get() == ButtonType.CANCEL){
                return;
            }
            if(nbrOfInnings > 0) {                
                innings.setText(String.valueOf(getInnings()-nbrOfInnings));
                
                //Player 1
                runPlayer1 = new ArrayList<>(runPlayer1.subList(0, runPlayer1.size()-nbrOfInnings));
                totalPlayer1 = new ArrayList<>(totalPlayer1.subList(0, totalPlayer1.size()-nbrOfInnings));
                
                int hr = 0;
                for (int i = 0; i < totalPlayer1.size(); i++) {
                    if (runPlayer1.get(i) > hr) {
                        hr = runPlayer1.get(i);
                        setLblHR(1, hr);
                    }
                }
                setLblScore(1, totalPlayer1.get(totalPlayer1.size()-1));
                double avg = (double) getScorePlayer(1) / getInnings();
                DecimalFormat formatter = new DecimalFormat("#0.00");
                setTFAVG(1, formatter.format(avg));

                int start = (runPlayer1.size() < 4 ? 0 : runPlayer1.size() - 3);
                int pos = 0;
                for (int i = start; i < runPlayer1.size(); i++) {
                    pos++;
                    setScorePlayerPos(1, pos, i + 1, runPlayer1.get(i), totalPlayer1.get(i));
                }
                
                //Player 2
                runPlayer2 = new ArrayList<>(runPlayer2.subList(0, runPlayer2.size()-nbrOfInnings));
                totalPlayer2 = new ArrayList<>(totalPlayer2.subList(0, totalPlayer2.size()-nbrOfInnings));
                
                hr = 0;
                for (int i = 0; i < totalPlayer2.size(); i++) {
                    if (runPlayer2.get(i) > hr) {
                        hr = runPlayer2.get(i);
                        setLblHR(2, hr);
                    }
                }
                setLblScore(2, totalPlayer2.get(totalPlayer2.size()-1));
                avg = (double) getScorePlayer(2) / getInnings();
                formatter = new DecimalFormat("#0.00");
                setTFAVG(2, formatter.format(avg));

                start = (runPlayer2.size() < 4 ? 0 : runPlayer2.size() - 3);
                pos = 0;
                for (int i = start; i < runPlayer2.size(); i++) {
                    pos++;
                    setScorePlayerPos(2, pos, i + 1, runPlayer2.get(i), totalPlayer2.get(i));
                }
            }
        }
    }
}
