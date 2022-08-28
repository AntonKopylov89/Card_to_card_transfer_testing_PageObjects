package ru.netology.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.val;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private SelenideElement firstCardDepositButton = $$("[data-test-id=action-deposit]").get(0);
    private SelenideElement secondCardDepositButton = $$("[data-test-id=action-deposit]").get(1);
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";

    public DashboardPage(){
        heading.shouldBe(visible);
    }

    public TransactionPage firstCardButtonClick() {
        firstCardDepositButton.click();
        return new TransactionPage();
    }

    public TransactionPage secondCardButtonClick() {
        secondCardDepositButton.click();
        return new TransactionPage();
    }

    public int getCardBalance(String id) {
        // перебрать все карты и найти по атрибуту data-test-id
        val text = cards.findBy(attribute("data-test-id", id)).text();
        return extractBalance(text);
    }

    private int extractBalance(String text) {
        val start = text.indexOf(balanceStart);
        val finish = text.indexOf(balanceFinish);
        val value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }
}
