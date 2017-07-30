/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.common.MailSender;
import billiard.common.PDFCreator;
import billiard.common.PermittedValues;
import billiard.model.Match;
import billiard.model.Player;
import billiard.model.PlayerMatchResult;
import billiard.model.Team;
import billiard.model.TeamCompetition;
import billiard.model.TeamCompetitionManager;
import billiard.model.TeamResult;
import billiard.score.BilliardScore;
import billiard.common.AppProperties;
import billiard.common.SceneUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author jean
 */
public class TeamCompetitionSummarySheetController {

    private static final Logger LOGGER = Logger.getLogger(TeamCompetitionSummarySheetController.class.getName(), null);
    private static final String SUMMARYSHEET_CSS = "/summarysheet.css";

    Stage stage;
    final WebView browser;
    final WebEngine webEngine;
    private final TeamCompetition competition;
    private final Team team1;
    private final Team team2;
    private final String datum;
    private final ResourceBundle bundle;
    private String summarySheetFilePath = "";

    public static void showSummarySheet(TeamCompetition competition) throws Exception {
        TeamCompetitionSummarySheetController teamCompetitionSummarySheetController = new TeamCompetitionSummarySheetController(competition);
    }

    public TeamCompetitionSummarySheetController(TeamCompetition competition) throws Exception {
        LOGGER.log(Level.FINEST, "TeamCompetitionSummarySheetController => Start");

        String strLocale = AppProperties.getInstance().getLocale();
        Locale locale = new Locale(strLocale);
        bundle = ResourceBundle.getBundle("languages.lang", locale);

        this.competition = competition;
        this.team1 = competition.getTeam1();
        this.team2 = competition.getTeam2();

        // Define stage
        stage = new Stage();
        stage.setTitle(bundle.getString("label.verzamelstaat") + "" + team1.getName() + " - " + team2.getName());

        // Define toolbar
        HBox toolbar = new HBox();
        toolbar.setPadding(new Insets(15, 12, 15, 12));
        toolbar.setSpacing(10);
        toolbar.setStyle("-fx-background-color: #336699;");

        Button buttonPrint = new Button(bundle.getString("btn.afdrukken"));
        buttonPrint.setPrefSize(100, 20);
        buttonPrint
                .setOnAction((ActionEvent e) -> {
                    print();
                });
        toolbar.getChildren().add(buttonPrint);

        if (AppProperties.getInstance().isEmailConfigured()) {
            Button buttonSend = new Button(bundle.getString("btn.verzenden"));
            buttonSend.setPrefSize(100, 20);
            buttonSend
                    .setOnAction((ActionEvent e) -> {
                        try {
                            send();
                        } catch (Exception ex) {
                            LOGGER.severe(Arrays.toString(ex.getStackTrace()));
                            throw new RuntimeException(ex);
                        }
                    });
            toolbar.getChildren().add(buttonSend);
        }

        Button buttonSave = new Button(bundle.getString("btn.bewaren"));
        buttonSave.setPrefSize(100, 20);
        buttonSave
                .setOnAction((ActionEvent e) -> {
                    try {
                        save();
                    } catch (Exception ex) {
                        LOGGER.severe(Arrays.toString(ex.getStackTrace()));
                        throw new RuntimeException(ex);
                    }
                });
        toolbar.getChildren().add(buttonSave);

        Button buttonCancel = new Button(bundle.getString("btn.sluiten"));
        buttonCancel.setPrefSize(100, 20);
        buttonCancel
                .setOnAction((ActionEvent e) -> {
                    cancel();
                });
        toolbar.getChildren().add(buttonCancel);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
        datum = ft.format(dNow);

        SimpleDateFormat ft2 = new SimpleDateFormat("yyyyMMdd");
        String archiveDate = ft2.format(dNow);

        // Define webview
        browser = new WebView();
        browser.setPrefSize(1024, 768);
        webEngine = browser.getEngine();
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(1024, 768);
        pane.getChildren().add(browser);
        String html = genScroreSheet();
        competition.setSummarySheetHTML(html);
        TeamCompetitionManager.getInstance().updateTeamCompetition(competition);
        webEngine.loadContent(html);

        String filename = competition.getName() + " - (" + competition.getNumber() + ") " + competition.getTeam1().getName() + "-" + competition.getTeam2().getName() + ".pdf";
        filename = filename.replace("/", "-");
        String location = AppProperties.getInstance().getArchiveLocation() + "/" + archiveDate + "/";

        File fileLocation = new File(location);
        if (!fileLocation.exists()) {
            fileLocation.mkdirs();
        }
        summarySheetFilePath = location + filename;
        PDFCreator.createPDF(html, summarySheetFilePath);

        BorderPane rootPane = new BorderPane(browser);
        rootPane.setTop(toolbar);

        Scene scene = new Scene(rootPane);

        stage.setScene(scene);
        stage.showAndWait();
        LOGGER.log(Level.FINEST, "TeamCompetitionSummarySheetController => End");
    }

    private String genScroreSheet() throws Exception {
        LOGGER.log(Level.FINEST, "TeamCompetitionSummarySheetController.genScroreSheet => Start");

        String title = AppProperties.getInstance().getTitle();
        String subtitle = AppProperties.getInstance().getSubTitle();
        String club = AppProperties.getInstance().getClub();
        String userHome = System.getProperty("user.home");
        String logoLocation = AppProperties.getInstance().getLogoLocation();

        StringBuilder html = new StringBuilder();

        // html head
        html.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
        html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">").append("\n");
        html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">").append("\n");
        html.append("<head>").append("\n");
        html.append("<meta charset='UTF-8'/>").append("\n");
        html.append("<title>").append(bundle.getString("label.verzamelstaat.titel")).append("</title>").append("\n");
        html.append("<style type='text/css'>").append("\n");
        html.append(getStyles());
        html.append("</style>").append("\n");
        html.append("</head>" + "\n").append("\n");

        // html body
        // heading
        html.append("<body>").append("\n");
        html.append("<div id='page'>").append("\n");
        html.append("<div id='heading'>").append("\n");
        html.append("<table width='100%'>").append("\n");
        html.append("<tr>").append("\n");
        File logoFile = new File(logoLocation);
        if (logoFile.exists()) {
            html.append("<td>").append("\n");
            html.append("<p><img src='").append(logoFile.toURI()).append("'/></p>").append("\n");
            html.append("</td>").append("\n");
        }
        html.append("<td>").append("\n");
        html.append("<h2>").append(title).append("</h2>").append("\n");
        html.append("<p>").append(subtitle).append("</p>").append("\n");
        html.append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("</table>").append("\n");
        html.append("</div>").append("\n");

        // intro
        html.append("<div id='intro'>").append("\n");
        html.append("<table class='tabel'>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.club")).append("</td>").append("\n");
        html.append("<td class='value'>").append(club).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.datum")).append("</td>").append("\n");
        html.append("<td class='value'>").append(datum).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.competitie")).append("</td>").append("\n");
        html.append("<td class='value'>").append(competition.getName()).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.cat.afd.grp")).append("</td>").append("\n");
        html.append("<td class='value'>").append(competition.getGroup()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.discipline")).append("</td>").append("\n");
        html.append("<td class='value'>").append(competition.getDiscipline()).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.biljart.formaat")).append("</td>").append("\n");
        html.append("<td class='value'>").append(competition.getTableFormat()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.wedstrijdnr")).append("</td>").append("\n");
        html.append("<td class='value'>").append(competition.getNumber()).append("</td>").append("\n");
        html.append("<td class='label'>&nbsp;</td>").append("\n");
        html.append("<td class='value'>&nbsp;</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("</table>").append("\n");
        html.append("</div>").append("\n");

        // body
        html.append("<div id='body'>").append("\n");
        html.append("<table class='tabel'>").append("\n");
        html.append("<tr>").append("\n");

        // team 1
        html.append("<td width='50%'><table style='border-collapse: collapse;'>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.ploegnaam")).append("</td>").append("\n");
        html.append("<td colspan='8'>").append(team1.getName()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.lic")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.naam")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.discipline")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.tsp")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.punten")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.beurten")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.gem")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.hr")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.mp")).append("</td>").append("\n");
        html.append("</tr>").append("\n");

        // players  team 1
        for (Player player : team1.getPlayers()) {
            String dfFormat = "#0.00";
            if(player.getDiscipline().contains("Drieband")) {
                dfFormat = "#0.000";
            }
            DecimalFormat df = new DecimalFormat(dfFormat);
            df.setRoundingMode(RoundingMode.DOWN);

            PlayerMatchResult result = competition.getPlayerResult(player);
            html.append("<tr>").append("\n");
            html.append("<td>").append(player.getLicentie()).append("</td>").append("\n");
            html.append("<td class='naam'>").append(player.getName()).append("</td>").append("\n");
            html.append("<td>").append(player.getDiscipline()).append("</td>").append("\n");
            html.append("<td>").append(player.getTsp()).append("</td>").append("\n");
            html.append("<td>").append(result.getPoints()).append("</td>").append("\n");
            html.append("<td>").append(result.getInnings()).append("</td>").append("\n");
            html.append("<td>").append(df.format(result.getAverage())).append("</td>").append("\n");
            html.append("<td>").append(result.getHighestRun()).append("</td>").append("\n");
            html.append("<td>").append(result.getMatchPoints()).append("</td>").append("\n");
            html.append("</tr>");
        }
        TeamResult team1Result = competition.getTeam1Result();
        html.append("<tr>").append("\n");
        html.append("<td colspan='4' class='label'>").append(bundle.getString("label.totaal")).append("</td>").append("\n");
        html.append("<td>").append(team1Result.getPoints()).append("</td>").append("\n");
        html.append("<td>").append(team1Result.getInnings()).append("</td>").append("\n");
        html.append("<td>");
        if(null!=competition.getDiscipline()) {
            String dfFormat = "#0.00";
            if(competition.getDiscipline().contains("Drieband")) {
                dfFormat = "#0.000";
            }
            DecimalFormat df = new DecimalFormat(dfFormat);
            df.setRoundingMode(RoundingMode.DOWN);
            html.append(df.format(team1Result.getAverage()));
        }
        html.append("</td>").append("\n");
        html.append("<td>").append(team1Result.getHighestRun()).append("</td>").append("\n");
        html.append("<td>").append(team1Result.getMatchPoints()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("</table></td>").append("\n");

        // team 2
        html.append("<td width='50%'><table style='border-collapse: collapse;'>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.ploegnaam")).append("</td>").append("\n");
        html.append("<td colspan='8'>").append(team2.getName()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.lic")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.naam")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.discipline")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.tsp")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.punten")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.beurten")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.gem")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.hr")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("hdng.mp")).append("</td>").append("\n");
        html.append("</tr>").append("\n");

        // players  team 2
        for (Player player : team2.getPlayers()) {
            String dfFormat = "#0.00";
            if(player.getDiscipline().contains("Drieband")) {
                dfFormat = "#0.000";
            }
            DecimalFormat df = new DecimalFormat(dfFormat);
            df.setRoundingMode(RoundingMode.DOWN);

            PlayerMatchResult result = competition.getPlayerResult(player);
            html.append("<tr>").append("\n");
            html.append("<td>").append(player.getLicentie()).append("</td>").append("\n");
            html.append("<td class='naam'>").append(player.getName()).append("</td>").append("\n");
            html.append("<td>").append(player.getDiscipline()).append("</td>").append("\n");
            html.append("<td>").append(player.getTsp()).append("</td>").append("\n");
            html.append("<td>").append(result.getPoints()).append("</td>").append("\n");
            html.append("<td>").append(result.getInnings()).append("</td>").append("\n");
            html.append("<td>").append(df.format(result.getAverage())).append("</td>").append("\n");
            html.append("<td>").append(result.getHighestRun()).append("</td>").append("\n");
            html.append("<td>").append(result.getMatchPoints()).append("</td>").append("\n");
            html.append("</tr>");
        }
        TeamResult team2Result = competition.getTeam2Result();
        html.append("<tr>").append("\n");
        html.append("<td colspan='4' class='label'>").append(bundle.getString("label.totaal")).append("</td>").append("\n");
        html.append("<td>").append(team2Result.getPoints()).append("</td>").append("\n");
        html.append("<td>").append(team2Result.getInnings()).append("</td>").append("\n");
        html.append("<td>");
        if(null!=competition.getDiscipline()) {
            String dfFormat = "#0.00";
            if(competition.getDiscipline().contains("Drieband")) {
                dfFormat = "#0.000";
            }
            DecimalFormat df = new DecimalFormat(dfFormat);
            df.setRoundingMode(RoundingMode.DOWN);
            html.append(df.format(team2Result.getAverage()));
        }
        html.append("</td>").append("\n");
        html.append("<td>").append(team2Result.getHighestRun()).append("</td>").append("\n");
        html.append("<td>").append(team2Result.getMatchPoints()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("</table></td>").append("\n");

        html.append("</tr>").append("\n");
        html.append("</table>").append("\n");
        html.append("</div>").append("\n");

        // summary & footer
        html.append("<div id='summary'>").append("\n");
        html.append("<table class='tabel'>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.handtekening")).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.handtekening")).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='handtekening'>&nbsp;</td>").append("\n");
        html.append("<td class='handtekening'>&nbsp;</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label' colspan='2'>").append(bundle.getString("label.handtekening.scheidsrechters")).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='handtekening' colspan='2'>&nbsp;</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("</table>").append("\n");
        html.append("</div>").append("\n");
        html.append("<div id='footing'>http://billiardsoftware.be</div>").append("\n");
        html.append("</div>").append("\n");
        html.append("</body>").append("\n");
        html.append("</html>").append("\n");

        LOGGER.log(Level.FINEST, "TeamCompetitionSummarySheetController.genScroreSheet => End");
        return html.toString();
    }

    private void print() {
        LOGGER.log(Level.FINEST, "TeamCompetitionSummarySheetController.print => Start");
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(stage)) {
            PageLayout layout = job.getPrinter().createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);
            job.getJobSettings().setPageLayout(layout);
            webEngine.print(job);
            job.endJob();
        }
        LOGGER.log(Level.FINEST, "TeamCompetitionSummarySheetController.print => End");
    }

    private void cancel() {
        LOGGER.log(Level.FINEST, "TeamCompetitionSummarySheetController.cancel => Start");
        stage.close();
        LOGGER.log(Level.FINEST, "TeamCompetitionSummarySheetController.cancel => End");
    }

    private void send() throws Exception {
        String to = (null!=competition.getContactDetails()?competition.getContactDetails().getEmail():"");
        String from = AppProperties.getInstance().getEmailSender();
        String host = AppProperties.getInstance().getEmailServer();
        String subject = bundle.getString("label.verzamelstaat")
                + " " + competition.getName() + ": "
                + team1.getName() + " - " + team2.getName();
    
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(stage);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(bundle.getString("titel.verzenden.uitslag"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource(BilliardScore.FXML.SEND_MAIL),bundle);
        Parent root;
        root = loader.load();
    
        SendMailController controller = loader.<SendMailController>getController();
        controller.initController(dialog);
        controller.setData(to);
        
        Scene scene = new Scene(root);
        SceneUtil.setStylesheet(scene);
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.showAndWait();

        if(controller.getAction().equals(PermittedValues.Action.OK)) {
            String msg = controller.getMessage();
            String cc = controller.getCC();
            to = controller.getTo();

            ArrayList<String> attachements = new ArrayList();
            attachements.addAll(getFiles());

            String csvFileName = summarySheetFilePath.replace(".pdf", ".csv");
            File csvFile = new File(csvFileName);
            try (FileOutputStream fos = new FileOutputStream(csvFile)) {
                fos.write(competition.toCSV().getBytes());
            }
            attachements.add(csvFileName);

            boolean mailsent = MailSender.sendMailWithAttachement(from, to ,cc , host, subject, msg, attachements.toArray(new String[0]));

            Stage dialog2 = new Stage();
            dialog2.initModality(Modality.APPLICATION_MODAL);
            dialog2.initOwner(stage);
            dialog2.initStyle(StageStyle.UTILITY);
            dialog2.setTitle(bundle.getString("titel.verzenden.uitslag"));

            FXMLLoader loader2 = new FXMLLoader(getClass().getResource(BilliardScore.FXML.OK_CANCEL),bundle);
            Parent root2;
            root2 = loader2.load();

            OKCancelDialogController controller2 = loader2.<OKCancelDialogController>getController();
            if(mailsent){
                controller2.setMessage(bundle.getString("msg.mail.verzonden"));
            } else {
                controller2.setMessage(bundle.getString("msg.mail.niet.verzonden"));            
            }
            controller2.initController(dialog2);
            controller2.setMode(OKCancelDialogController.Mode.OK);

            Scene scene2 = new Scene(root2);
            SceneUtil.setStylesheet(scene2);
            dialog2.setScene(scene2);
            dialog2.centerOnScreen();
            dialog2.showAndWait();
        }
    }
    
    private void save() throws Exception {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dirChooser.setTitle(bundle.getString("titel.selecteer.folder"));
        File outputDir = dirChooser.showDialog(stage);
        if(outputDir!=null) {
            for(String src: getFiles()) {
                Path srcPath = new File(src).toPath();
                Files.copy(srcPath, outputDir.toPath().resolve(srcPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private ArrayList<String> getFiles() throws Exception {
        ArrayList<String> files = new ArrayList();
        Date dNow = new Date();
        SimpleDateFormat ft2 = new SimpleDateFormat("yyyyMMdd");
        String archiveDate = ft2.format(dNow);

        files.add(summarySheetFilePath);
        for (Match match : competition.getMatches()) {
            String filename = competition.getName() + " - (" + match.getNumber() + ") "
                    + match.getPlayer1().getName() + "-" + match.getPlayer2().getName() + ".pdf";
            String location = AppProperties.getInstance().getArchiveLocation() + "/" + archiveDate + "/";
            String filePath = location + filename;
            File file = new File(filePath);
            if (!file.exists()) {
                PDFCreator.createPDF(match.getScoreSheetHTML(), filePath);
            }
            files.add(filePath);
        }
        return files;
    }

    private String getStyles() throws Exception {
        LOGGER.log(Level.FINEST, "TeamCompetitionSummarySheetController.getStyles => Start");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(SUMMARYSHEET_CSS)));

        StringBuilder stringBuffer = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line).append("\n");
        }
        LOGGER.log(Level.FINEST, "TeamCompetitionSummarySheetController.getStyles => End");
        return stringBuffer.toString();
    }
}
