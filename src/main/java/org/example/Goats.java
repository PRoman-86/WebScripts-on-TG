package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.LocalTime;
import java.util.List;

public class Goats {

    private ChromeDriver driver;
    private int counter;
    private final By BANNER_CLOSE_BUTTON = By.xpath("//*[name()='path' and contains(@d,'M15.6804 1')]");
    private final By VAST_PLAYER_AD_FRAME = By.className("vast_player__vpaid-frame");

    Goats(int numberOfCycles) {
        System.out.println("RUN GOATS");
        while(this.driver == null) {
            startChrome();
            waitOnSec(5);
            try {
                script();
            } catch (NoSuchElementException e) {
                handlingException("fail - NoSuchElementException");
                continue;
            } catch (ElementClickInterceptedException e) {
                handlingException("fail - ElementClickInterceptedException");
                continue;
            }
            stopChrome();
            this.counter++;
            System.out.println(LocalTime.now() + ", cycle: " + this.counter + "  coins: " + this.counter*200);
            if (this.counter == numberOfCycles) break;
            waitOnSec(30);
        }
        if (this.driver != null) this.driver.quit();
        System.out.println("GOATS IS STOPPED");
    }

    public void script() throws NoSuchElementException, ElementClickInterceptedException {
        getDriver().findElement(By.xpath("(//div[@class='ripple-container'])[3]")).click(); //select bot
        waitOnSec(2);
        getDriver().findElement(By.cssSelector("button[title='Open bot command keyboard']")).click(); //run bot
        waitOnSec(9);
        getDriver().switchTo().frame(driver.findElement(By.className("OmY14FFl"))); //focus on bot frame
        if (elementIsExist(BANNER_CLOSE_BUTTON)) getDriver().findElement(BANNER_CLOSE_BUTTON).click(); //close banner, if he is exist
        waitOnSec(1);
        getDriver().findElement(By.id("tabs-:r1:--tab-2")).click(); //click on "missions" button
        waitOnSec(4);
        getDriver().findElement(By.className("css-1a44hfb")).click(); //click on "do" button
        waitOnSec(10);
        if (elementIsExist(VAST_PLAYER_AD_FRAME)) {
            getDriver().switchTo().frame(getDriver().findElement(VAST_PLAYER_AD_FRAME)); //focus on another AD frame
            getDriver().findElement(By.cssSelector(".close_btn")).click(); //click on close button
        }
        waitOnSec(33);
    }

    public void waitOnSec(long sec) {
        try {
            Thread.sleep(sec*1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void startChrome() {
        this.driver = new ChromeDriver(new ChromeOptions()
                .setBinary("C:/Program Files/Google/Chrome/Application/chrome.exe")
                .addArguments("user-data-dir=C:/Users/suvor/AppData/Local/Google/Chrome/User Data")
                .addArguments("profile-directory=Profile 1"));

        getDriver().get("https://web.telegram.org/a/");
        getDriver().manage().window().fullscreen();
    }
    public void stopChrome() {
        getDriver().quit();
        this.driver = null;
    }
    public WebDriver getDriver() {return this.driver;}

    public void handlingException(String type) {
        stopChrome();
        System.out.println(type);
        waitOnSec(10);
    }
    public boolean elementIsExist(By locator) {
        List<WebElement> elements = getDriver().findElements(locator);
        return !elements.isEmpty();
    }
}