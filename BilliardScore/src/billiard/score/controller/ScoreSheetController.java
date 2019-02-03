/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.score.controller;

import billiard.common.MailSender;
import billiard.common.PDFCreator;
import billiard.common.PermittedValues;
import billiard.model.Competition;
import billiard.model.IndividualCompetition;
import billiard.model.Match;
import billiard.model.PlayerMatchResult;
import billiard.common.AppProperties;
import billiard.common.SceneUtil;
import billiard.model.CompetitionManager;
import billiard.model.MatchManager;
import billiard.score.BilliardScore;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author jean
 */
public class ScoreSheetController {
    private static final Logger LOGGER = Logger.getLogger(ScoreSheetController.class.getName());
    private static final String SCORESHEET_CSS = "/scoresheet.css";

    Stage stage;
    final WebView browser;
    final WebEngine webEngine;
    private final PlayerMatchResult player1Result;
    private final PlayerMatchResult player2Result;
    private final Match match;
    private final Competition competition;
    private final String datum;
    private final ResourceBundle bundle;
    private final PermittedValues.Mode mode;

    public static void showScoreSheet(Match match, PermittedValues.Mode mode) throws Exception {
        ScoreSheetController scoreSheetController = new ScoreSheetController(match, mode);
    }
    
    public ScoreSheetController(Match match, PermittedValues.Mode mode) throws Exception {
        this.mode = mode;
        String strLocale=AppProperties.getInstance().getLocale();
        Locale locale = new Locale(strLocale);
        bundle = ResourceBundle.getBundle("languages.lang", locale);
        
        player1Result = match.getPlayer1Result();
        player2Result = match.getPlayer2Result();
        this.match = match;
        this.competition = CompetitionManager.getCompetition(match.getCompetitionId());
        
        // Define stage
        stage = new Stage();
        stage.setTitle(bundle.getString("label.wedstrijdblad") + " " + match.getPlayer1().getName() + " - " + match.getPlayer2().getName());

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
            if(competition instanceof IndividualCompetition) {
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
        }
        
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
        match.setScoreSheetHTML(html);
        MatchManager.getInstance().updateMatch(match);
        webEngine.loadContent(html);
        
        String filename = competition.getName() + " - ("+match.getNumber()+") "+match.getPlayer1().getName()+"-"+match.getPlayer2().getName()+".pdf";
        String location = AppProperties.getInstance().getArchiveLocation()+"/"+archiveDate+"/";
        File fileLocation = new File(location);
        if(!fileLocation.exists()) {
            fileLocation.mkdirs();
        }
        String filePath = location+filename;
        PDFCreator.createPDF(html, filePath);

        BorderPane rootPane = new BorderPane(browser);
        rootPane.setTop(toolbar);
        
        Scene scene = new Scene(rootPane);

        stage.setScene(scene);
        stage.show();
        PauseTransition wait = new PauseTransition(Duration.seconds(60));
        wait.setOnFinished((e) -> {
            stage.hide();
            wait.playFromStart();
        });
        wait.play();
    }

    private String genScroreSheet() throws Exception {
        
        AppProperties prop = AppProperties.getInstance();

        String title = prop.getTitle();
        String subtitle = prop.getSubTitle();
        String club = prop.getClub();
        String logoLocation = AppProperties.getInstance().getLogoLocation();
         
        StringBuilder html = new StringBuilder();

        // html head
        html.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
        html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">").append("\n");
        html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">").append("\n");
        html.append("<head>").append("\n");
        html.append("<meta charset='UTF-8'/>").append("\n");
        html.append("<title>").append(bundle.getString("label.wedstrijdblad.titel")).append("</title>").append("\n");
        html.append("<style type='text/css'>").append("\n");
        html.append(getStyles());
        html.append("</style>").append("\n");
        html.append("</head>").append("\n");
        
        // html body
        // heading
        html.append("<body>").append("\n");
        html.append("<div id='page'>").append("\n");
        html.append("<div id='heading'>").append("\n");
        html.append("<table width='100%'>").append("\n");
        html.append("<tr>").append("\n");
        File logoFile = new File(logoLocation);
        if(logoFile.exists()) {
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
        html.append("<td class='value'>").append(match.getDiscipline()).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.biljart.formaat")).append("</td>").append("\n");
        html.append("<td class='value'>").append(competition.getTableFormat()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.wedstrijdnr")).append("</td>").append("\n");
        html.append("<td class='value'>").append(match.getNumber()).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.biljart.nr")).append("</td>").append("\n");
        html.append("<td class='value'>").append("\n");
        if(competition instanceof IndividualCompetition) {
            html.append(((IndividualCompetition)competition).getTableNumber()).append("\n");
        }else{
            html.append("&nbsp;").append("\n");
        }
        html.append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("</table>").append("\n");
        html.append("</div>").append("\n");
        
        // wedstrijd verslag
        html.append("<div id='body'>").append("\n");
        html.append("<table class='tabel'>").append("\n");
        html.append("<tr>").append("\n");
        if(match.isStarted() || match.isEnded()) {
            html.append("<td colspan='3' width='50%'>").append(match.getPlayer1().getName()).append("</td>").append("\n"); // Speler 1
            html.append("<td colspan='3' width='50%'>").append(match.getPlayer2().getName()).append("</td>").append("\n"); // Speler 2
        } else {
            html.append("<td colspan='3' width='50%'>").append("&nbsp;").append("</td>").append("\n"); // Speler 1
            html.append("<td colspan='3' width='50%'>").append("&nbsp;").append("</td>").append("\n"); // Speler 2
        }
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");

        // Speler 1
        for (int i = 0; i < 3; i++) {
            int startInning = i * 25;
            html.append("<td>").append("\n");
            html.append("<table class='tabel'>").append("\n");
            
            for (int j = startInning; j < startInning+25; j++) {
                int inning = j + 1;
                html.append("<tr>").append("\n");
                html.append("<td class='beurten'>").append(inning).append("</td>").append("\n");
                if ((match.isStarted() || match.isEnded()) && player1Result.getScoreList().size()>j) {
                    Integer score = (Integer)player1Result.getScoreList().get(j);
                    if (score != 0) {
                        html.append("<td class='punten'>").append(player1Result.getScoreList().get(j)).append("</td>").append("\n");
                        html.append("<td class='punten'>").append(player1Result.getTotalList().get(j)).append("</td>").append("\n");
                    }else{
                        html.append("<td class='punten'>-</td>").append("\n");
                        html.append("<td class='punten'>-</td>").append("\n");
                    }
                }else{
                    html.append("<td class='punten'>&nbsp;</td>").append("\n");
                    html.append("<td class='punten'>&nbsp;</td>").append("\n");
                }
                html.append("</tr>").append("\n");
            }

            html.append("</table>").append("\n");
            html.append("</td>").append("\n");
        }

        
        // Speler 2
        for (int i = 0; i < 3; i++) {
            int startInning = i * 25;
            html.append("<td>").append("\n");
            html.append("<table class='tabel'>").append("\n");
            
            for (int j = startInning; j < startInning+25; j++) {
                int inning = j + 1;
                html.append("<tr>").append("\n");
                html.append("<td class='beurten'>").append(inning).append("</td>").append("\n");
                if ((match.isStarted() || match.isEnded()) && player2Result.getScoreList().size()>j) {
                    Integer score = (Integer)player2Result.getScoreList().get(j);
                    if ( score != 0) {
                        html.append("<td class='punten'>").append(player2Result.getScoreList().get(j)).append("</td>").append("\n");
                        html.append("<td class='punten'>").append(player2Result.getTotalList().get(j)).append("</td>").append("\n");
                    }else{
                        html.append("<td class='punten'>-</td>").append("\n");
                        html.append("<td class='punten'>-</td>").append("\n");
                    }
                }else{
                    html.append("<td class='punten'>&nbsp;</td>").append("\n");
                    html.append("<td class='punten'>&nbsp;</td>").append("\n");
                }
                html.append("</tr>").append("\n");
            }

            html.append("</table>").append("\n");
            html.append("</td>").append("\n");
        }

        html.append("</tr>").append("\n");
        html.append("</table>").append("\n");

        // summary
        html.append("</div>").append("\n");
        html.append("<div id='summary'>").append("\n");
        html.append("<table class='tabel' style='margin-bottom:5px'>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.naam")).append("</td>").append("\n");
        html.append("<td class='value'>").append(match.getPlayer1().getName()).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.naam")).append("</td>").append("\n");
        html.append("<td class='value'>").append(match.getPlayer2().getName()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.club")).append("</td>").append("\n");
        html.append("<td class='value'>").append(match.getPlayer1().getClub()).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.club")).append("</td>").append("\n");
        html.append("<td class='value'>").append(match.getPlayer2().getClub()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.licentie")).append("</td>").append("\n");
        html.append("<td class='value'>").append(match.getPlayer1().getLicentie()).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.licentie")).append("</td>").append("\n");
        html.append("<td class='value'>").append(match.getPlayer2().getLicentie()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("<tr>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.tsp")).append("</td>").append("\n");
        html.append("<td class='value'>").append(match.getPlayer1().getTsp()).append(" ")
                .append(match.getPlayer1().getDiscipline()).append("</td>").append("\n");
        html.append("<td class='label'>").append(bundle.getString("label.tsp")).append("</td>").append("\n");
        html.append("<td class='value'>").append(match.getPlayer2().getTsp()).append(" ")
                .append(match.getPlayer2().getDiscipline()).append("</td>").append("\n");
        html.append("</tr>").append("\n");
        html.append("</table>").append("\n");
        if(mode.equals(PermittedValues.Mode.EDIT)) {
            html.append("<table class='tabel' style='margin-bottom:5px'>").append("\n");
            html.append("<tr>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.punten")).append("</td>").append("\n");
            html.append("<td class='value'>").append(player1Result.getPoints()).append("</td>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.punten")).append("</td>").append("\n");
            html.append("<td class='value'>").append(player2Result.getPoints()).append("</td>").append("\n");
            html.append("</tr>").append("\n");
            html.append("<tr>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.beurten")).append("</td>").append("\n");
            html.append("<td class='value'>").append(player1Result.getInnings()).append("</td>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.beurten")).append("</td>").append("\n");
            html.append("<td class='value'>").append(player2Result.getInnings()).append("</td>").append("\n");
            html.append("</tr>").append("\n");
            html.append("<tr>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.gem")).append("</td>").append("\n");
            String dfFormat = "#0.00";
            if(match.getPlayer1().getDiscipline().contains("Drieband")) {
                dfFormat = "#0.000";
            }
            DecimalFormat df = new DecimalFormat(dfFormat);
            df.setRoundingMode(RoundingMode.DOWN);
            html.append("<td class='value'>").append(df.format(player1Result.getAverage())).append("</td>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.gem")).append("</td>").append("\n");
            dfFormat = "#0.00";
            if(match.getPlayer2().getDiscipline().contains("Drieband")) {
                dfFormat = "#0.000";
            }
            df = new DecimalFormat(dfFormat);
            df.setRoundingMode(RoundingMode.DOWN);
            html.append("<td class='value'>").append(df.format(player2Result.getAverage())).append("</td>").append("\n");
            html.append("</tr>").append("\n");
            html.append("<tr>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.hr")).append("</td>").append("\n");
            html.append("<td class='value'>").append(player1Result.getHighestRun()).append("</td>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.hr")).append("</td>").append("\n");
            html.append("<td class='value'>").append(player2Result.getHighestRun()).append("</td>").append("\n");
            html.append("</tr>").append("\n");
            html.append("</table>").append("\n");
        } else {
            html.append("<table class='tabel' style='margin-bottom:5px'>").append("\n");
            html.append("<tr>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.punten")).append("</td>").append("\n");
            html.append("<td class='value'>").append("&nbsp;").append("</td>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.punten")).append("</td>").append("\n");
            html.append("<td class='value'>").append("&nbsp;").append("</td>").append("\n");
            html.append("</tr>").append("\n");
            html.append("<tr>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.beurten")).append("</td>").append("\n");
            html.append("<td class='value'>").append("&nbsp;").append("</td>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.beurten")).append("</td>").append("\n");
            html.append("<td class='value'>").append("&nbsp;").append("</td>").append("\n");
            html.append("</tr>").append("\n");
            html.append("<tr>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.gem")).append("</td>").append("\n");
            html.append("<td class='value'>").append("&nbsp;").append("</td>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.gem")).append("</td>").append("\n");
            html.append("<td class='value'>").append("&nbsp;").append("</td>").append("\n");
            html.append("</tr>").append("\n");
            html.append("<tr>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.hr")).append("</td>").append("\n");
            html.append("<td class='value'>").append("&nbsp;").append("</td>").append("\n");
            html.append("<td class='label'>").append(bundle.getString("label.hr")).append("</td>").append("\n");
            html.append("<td class='value'>").append("&nbsp;").append("</td>").append("\n");
            html.append("</tr>").append("\n");
            html.append("</table>").append("\n");
        }
        
        // Handtekeningen
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
        
        // footing
        html.append("<div id='footing'>").append("\n");
        html.append("http://billiardsoftware.be</div>"+"\n").append("\n");
        html.append("</div>").append("\n");
        html.append("</body>").append("\n");
        html.append("</html>").append("\n");

        return html.toString();
    }

    private void print() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(stage)) {
            PageLayout layout = job.getPrinter().createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
            job.getJobSettings().setPageLayout(layout);
            webEngine.print(job);
            job.endJob();
        }
    }
    
    private void cancel() {
        stage.close();
    }
    
    private void send() throws Exception {
        String to = (null!=competition.getContactDetails()?competition.getContactDetails().getEmail():"");
        String from = AppProperties.getInstance().getEmailSender();
        String host = AppProperties.getInstance().getEmailServer();
        String subject = "Wedstrijdblad: " + competition.getName();
    
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
            attachements.add(getFile());

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
    
    private String getFile() throws Exception {
        Date dNow = new Date();
        SimpleDateFormat ft2 = new SimpleDateFormat("yyyyMMdd");
        String archiveDate = ft2.format(dNow);

        String filename = competition.getName() + " - (" + match.getNumber() + ") "
                + match.getPlayer1().getName() + "-" + match.getPlayer2().getName() + ".pdf";
        String location = AppProperties.getInstance().getArchiveLocation() + "/" + archiveDate + "/";
        String filePath = location + filename;
        File file = new File(filePath);
        if (!file.exists()) {
            PDFCreator.createPDF(match.getScoreSheetHTML(), filePath);
        }
        return filePath;
    }
    private String getStyles() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(SCORESHEET_CSS)));

        StringBuilder stringBuffer = new StringBuilder();
        String line;

        while((line = bufferedReader.readLine())!=null){
            stringBuffer.append(line).append("\n");
        }        
        return stringBuffer.toString();
    }
}
