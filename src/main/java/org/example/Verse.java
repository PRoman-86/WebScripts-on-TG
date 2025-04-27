package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.sound.sampled.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class Verse {

    private ChromeDriver driver;
    private int counter;
    private int quantityOfCycles;
    private long waitingTimeSec = 0;
    private String quantityDustLine = " quantity dust is not defined";
    private String currentRating = "NA";
    private String pathChromeExe;
    private String pathChromeUserDataDir;
    private String nameChromeProfileDirectory;
    private String pathVerseLogFileTxt;
    private String pathAlarmWav;
    private boolean IsSilentMode = false;
    private static final By COLLECT_DUST_BUTTON = By.className("progress-bar-container");
    private static final By FULL_DUST_BUTTON = By.xpath("//span[contains(text(),'Собрать пыль')]");
    private static final By UFO_FACE_BUTTON = By.xpath("//div[@id='ui-top-right']//a[@class='ui-link blur']//*[name()='svg']");
    private static final By QUANTITY_DUST_LINE = By.xpath("(//label[@class='details link'])[2]");
    private static final By RATING_LINE = By.xpath("(//button[@class='ui-button'])[1]");
    private static final By DUST_PERCENT_VALUE_ON_BUTTON = By.className("ml-16px");
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    private ChromeOptions getChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary(this.pathChromeExe)
                .addArguments("user-data-dir=" + this.pathChromeUserDataDir)
                .addArguments("profile-directory=" + this.nameChromeProfileDirectory);
        if (this.IsSilentMode) chromeOptions.addArguments("--headless");
        if (!this.IsSilentMode) chromeOptions.addArguments("--start-fullscreen");
        return chromeOptions;
    }

    private void fetchConfigLoader() {
        Properties properties = new Properties();
        try (InputStream input = Verse.class.getClassLoader().getResourceAsStream("local.PROPERTIES")) {
            if (input == null) {
                System.out.println(ANSI_RED + "Error: local.PROPERTIES is not found" + ANSI_RESET);
                System.exit(0);
            }
            properties.load(input);
            this.pathChromeExe = properties.getProperty("path_chrome.exe");
            this.pathChromeUserDataDir = properties.getProperty("path_chromeUserDataDir");
            this.nameChromeProfileDirectory = properties.getProperty("name_chromeProfileDirectory");
            this.pathVerseLogFileTxt = properties.getProperty("path_VerseLogFile.txt");
            this.pathAlarmWav = properties.getProperty("path_alarm.wav");
            this.quantityOfCycles = Integer.parseInt(properties.getProperty("int_quantityCycles"));
            this.IsSilentMode = Boolean.parseBoolean(properties.getProperty("boolean_silentMode"));
        } catch (IOException e) {
            System.out.println(ANSI_RED +
                    "Error: InputStream input in fetchConfigLoader() has been a problem with initialization" + ANSI_RESET);
            System.exit(0);
        }
    }

    Verse() {
        fetchConfigLoader();
        appendLineToLog(ANSI_YELLOW + getTime() + "| VERSE IS STARTING... |" + ANSI_RESET);
        while (this.quantityOfCycles != this.counter) {
            try {
                this.driver = new ChromeDriver(getChromeOptions());
                script();
            } catch (NoSuchElementException e) {
                this.driver.quit();
                handlingException("NoSuchElementException");
                continue;
            } catch (SessionNotCreatedException e) {
                handlingException("WebDriverSessionNotCreatedException");
                continue;
            }

            this.driver.quit();
            this.waitingTimeSec = randomRangeOnSec(2500, 3500);
            waitOnSec(this.waitingTimeSec);
        }

        this.driver.quit();
        soundPlayback();
        appendLineToLog(ANSI_YELLOW + getTime() + "| VERSE IS STOPPED |" + ANSI_RESET);
        System.exit(0);
    }

    private void script() throws NoSuchElementException {
        this.driver.get("https://web.telegram.org/a/");
        waitOnSec(randomRangeOnSec(6, 10));
        this.driver.findElement(By.xpath("(//div[@class='ripple-container'])[3]")).click();
        waitOnSec(randomRangeOnSec(6, 10));
        this.driver.findElement(By.cssSelector("button[title='Open bot command keyboard']")).click();
        waitOnSec(randomRangeOnSec(9, 12));
        this.driver.switchTo().frame(this.driver.findElement(By.className("OmY14FFl"))); //focus on bot frame

        if (elementIsExist(FULL_DUST_BUTTON)) {
            this.driver.findElement(FULL_DUST_BUTTON).click();
            waitOnSec(randomRangeOnSec(3, 6));
            fetchRatingQuantityDust();
            this.driver.findElement(By.xpath("//a[@class='ui-link blur close']")).click(); //closing stat. window
            appendLineToLog(ANSI_GREEN + getTime() + "| dust storage was full, successful collected on 100% |" + ANSI_RESET);
            waitOnSec(randomRangeOnSec(120, 280));
        }

        String percent = getTextOfElement(DUST_PERCENT_VALUE_ON_BUTTON);
        this.driver.findElement(COLLECT_DUST_BUTTON).click();
        waitOnSec(randomRangeOnSec(3, 6));
        if (!getTextOfElement(DUST_PERCENT_VALUE_ON_BUTTON).equals("0%")) this.driver.findElement(COLLECT_DUST_BUTTON).click(); //repeat click
        this.counter++;
        waitOnSec(randomRangeOnSec(3, 6));
        fetchRatingQuantityDust();
        appendLineToLog(ANSI_GREEN + getTime() + "| waiting " + convertSecondsToMinutesSeconds(this.waitingTimeSec)
                + "| collected, cycle " + String.format("%03d", this.counter) + " of " + this.quantityOfCycles
                + " on " + percent + "| " + "rating: " + this.currentRating + "| " + "silent: " + this.IsSilentMode + "|" + ANSI_RESET);
    }

    public static void waitOnSec(long sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static long randomRangeOnSec(int from, int to) {
        return (long) (from + Math.random() * (to - from));
    }

    public boolean elementIsExist(By locator) {
        List<WebElement> elements = this.driver.findElements(locator);
        return !elements.isEmpty();
    }

    public static String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String convertSecondsToMinutesSeconds(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public String getTextOfElement(By locator) {
        String currentText;
        try {
            currentText = this.driver.findElement(locator).getText();
        } catch (NoSuchElementException e) {
            return "NA";
        }
        return currentText;
    }

    private void setDefaultQuantityDustLine() { this.quantityDustLine = " quantity dust is not defined"; }

    public void handlingException(String typeException) {
        this.counter++;
        setDefaultQuantityDustLine();
        soundPlayback();
        appendLineToLog(ANSI_RED + getTime() + "| fail: " + typeException + ", cycle " +
                String.format("%03d", this.counter) + " of " + this.quantityOfCycles + " |" + ANSI_RESET);
        waitOnSec(randomRangeOnSec(25, 35));
    }

    private void appendLineToLog(String logLine) {
        System.out.println(logLine + this.quantityDustLine);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.pathVerseLogFileTxt, true))) {
                writer.write(logLine.substring(5, logLine.length() - 4) + this.quantityDustLine);
                writer.newLine();
        } catch (IOException e) {
            soundPlayback();
            System.out.println(ANSI_RED + "Error: FileWriter has been a problem with initialization" + ANSI_RESET);
        }
    }

    private void fetchRatingQuantityDust() {
        if (elementIsExist(UFO_FACE_BUTTON)) {
            this.driver.findElement(UFO_FACE_BUTTON).click();
            if (elementIsExist(RATING_LINE)) {
                this.currentRating = getTextOfElement(RATING_LINE); //get rating
            } else this.currentRating = "NA";

            if (elementIsExist(QUANTITY_DUST_LINE)) {
                String getCurrentQuantityDustLine = getTextOfElement(QUANTITY_DUST_LINE);

                if (!getCurrentQuantityDustLine.equals("NA")) {
                    this.quantityDustLine = " dust: " + getCurrentQuantityDustLine.substring(14, getCurrentQuantityDustLine.length() - 16);
                } else setDefaultQuantityDustLine();
            } else setDefaultQuantityDustLine();
        } else setDefaultQuantityDustLine();
    }

    public void soundPlayback() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(this.pathAlarmWav));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            appendLineToLog(ANSI_RED + "Error: Playing or reading the wav-file has been failed" + ANSI_RESET);
        }
    }
}