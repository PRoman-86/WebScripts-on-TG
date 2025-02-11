package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Verse {

    private ChromeDriver driver;
    private int counter;
    private String quantityDustLine = " quantity dust is not defined";
    private final By FULL_DUST_BUTTON = By.xpath("//span[contains(text(),'Собрать пыль')]");
    private final By UFO_FACE_BUTTON = By.xpath("//div[@id='ui-top-right']//a[@class='ui-link blur']//*[name()='svg']");
    private final By QUANTITY_DUST_LINE = By.xpath("(//label[@class='details link'])[2]");
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public ChromeOptions getChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary("C:/Program Files/Google/Chrome/Application/chrome.exe")
                .addArguments("user-data-dir=C:/Users/suvor/AppData/Local/Google/Chrome/User Data")
                .addArguments("profile-directory=Profile 1");
        chromeOptions.addArguments("--headless");
        return chromeOptions;
    }

    Verse(int numberOfCycles) {
        appendLineToLog(ANSI_YELLOW + getTime() + "| VERSE IS STARTED |" + ANSI_RESET);
        while (numberOfCycles != this.counter) {
            try {
                script();
            } catch (NoSuchElementException e) {
                handlingException("NoSuchElementException");
            } catch (ElementClickInterceptedException e) {
                handlingException("ElementClickInterceptedException");
            } catch (SessionNotCreatedException e) {
                appendLineToLog(ANSI_RED + getTime() + "| OTHER INSTANCE OF BROWSER CHROME IS OPEN! |" + ANSI_RESET);
                waitOnSec(30);
            }
        }

        this.driver.quit();
        appendLineToLog(ANSI_YELLOW + getTime() + "| VERSE IS STOPPED |" + ANSI_RESET);
    }

    public void script() throws NoSuchElementException, ElementClickInterceptedException {
        this.driver = new ChromeDriver(getChromeOptions());
        this.driver.get("https://web.telegram.org/a/");
        waitOnSec(7);
        this.driver.findElement(By.xpath("(//div[@class='ripple-container'])[3]")).click();
        waitOnSec(7);
        this.driver.findElement(By.cssSelector("button[title='Open bot command keyboard']")).click();
        waitOnSec(10);
        this.driver.switchTo().frame(this.driver.findElement(By.className("OmY14FFl"))); //focus on bot frame

        if (elementIsExist(FULL_DUST_BUTTON)) {
            this.driver.findElement(FULL_DUST_BUTTON).click();
            appendLineToLog(ANSI_GREEN + getTime() + "| dust storage was full, successful collected on 100% |" + ANSI_RESET);
            waitOnSec(randomRangeOnSec(120, 280));
        }

        String percent = this.driver.findElement(By.className("ml-16px")).getText(); //get percentage of full storage
        this.driver.findElement(By.className("progress-bar-container")).click();
        this.counter++;
        waitOnSec(3);

        if (elementIsExist(UFO_FACE_BUTTON)) {
            this.driver.findElement(UFO_FACE_BUTTON).click();
            if (elementIsExist(QUANTITY_DUST_LINE)) {
                String getCurrentQuantityDustLine = this.driver.findElement(QUANTITY_DUST_LINE).getText();
                this.quantityDustLine = " quantity dust is " + getCurrentQuantityDustLine.substring(14, getCurrentQuantityDustLine.length() - 16);
            } else {
                this.quantityDustLine = " quantity dust is not defined";
            }
        } else {
            this.quantityDustLine = " quantity dust is not defined";
        }

        waitOnSec(2);
        appendLineToLog(ANSI_GREEN + getTime() + "| successful collected, cycle " + this.counter + " on " + percent + " |" + ANSI_RESET);
        this.driver.quit();
        waitOnSec(randomRangeOnSec(2500, 3400));
    }

    public void waitOnSec(long sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public long randomRangeOnSec(int from, int to) {
        return (long) (from + Math.random() * (to - from));
    }

    public boolean elementIsExist(By locator) {
        List<WebElement> elements = this.driver.findElements(locator);
        return !elements.isEmpty();
    }

    public String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void handlingException(String typeException) {
        this.driver.quit();
        this.counter++;
        appendLineToLog(ANSI_RED + getTime() + "| fail: " + typeException + ", cycle " + this.counter + " |" + ANSI_RESET);
        waitOnSec(30);
    }

    public void appendLineToLog(String logLine) {
        System.out.println(logLine + this.quantityDustLine);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("VerseLogFile.txt", true))) {
                writer.write(logLine.substring(5, logLine.length() - 4) + this.quantityDustLine);
                writer.newLine();
        } catch (IOException e) {
            System.out.println(ANSI_RED + " IO Exception: LogFile Write Error" + ANSI_RESET);
        }
    }
}