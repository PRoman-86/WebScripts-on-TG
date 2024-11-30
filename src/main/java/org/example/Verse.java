package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Verse {

    private ChromeDriver driver;
    private int counter;
    private final By FULL_DUST_BUTTON = By.xpath("//span[contains(text(),'Собрать пыль')]");

    public ChromeOptions getChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary("C:/Program Files/Google/Chrome/Application/chrome.exe")
                .addArguments("user-data-dir=C:/Users/suvor/AppData/Local/Google/Chrome/User Data")
                .addArguments("profile-directory=Profile 1");
        chromeOptions.addArguments("--headless");
        return chromeOptions;
    }

    Verse(int numberOfCycles) {
        System.out.println(getTime() + "| VERSE IS STARTED");
        while (numberOfCycles != this.counter) {
            try {
                script();
            } catch (NoSuchElementException e) {
                driver.quit();
                System.out.println(getTime() + "| fail: NoSuchElementException");
                waitOnSec(10);
            } catch (ElementClickInterceptedException e) {
                driver.quit();
                System.out.println(getTime() + "| fail: ElementClickInterceptedException");
                waitOnSec(10);
            }
        }

        driver.quit();
        System.out.println(getTime() + "| VERSE IS STOPPED");
    }

    public void script() throws NoSuchElementException, ElementClickInterceptedException {
        driver = new ChromeDriver(getChromeOptions());
        driver.get("https://web.telegram.org/a/");
        waitOnSec(7);
        driver.findElement(By.xpath("(//div[@class='ripple-container'])[3]")).click();
        waitOnSec(7);
        driver.findElement(By.cssSelector("button[title='Open bot command keyboard']")).click();
        waitOnSec(10);
        driver.switchTo().frame(driver.findElement(By.className("OmY14FFl"))); //focus on bot frame

        if (elementIsExist(FULL_DUST_BUTTON)) {
            driver.findElement(FULL_DUST_BUTTON).click();
            System.out.println(getTime() + "| dust storage was full, successful collected on 100%");
            waitOnSec(randomRangeOnSec(120, 280));
        }

        String percent = driver.findElement(By.className("ml-16px")).getText(); //get percentage of full storage
        driver.findElement(By.className("progress-bar-container")).click();
        this.counter++;
        System.out.println(getTime() + "| successful collected, cycle " + this.counter + " on " + percent);
        waitOnSec(5);
        driver.quit();
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
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty();
    }

    public String getTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}