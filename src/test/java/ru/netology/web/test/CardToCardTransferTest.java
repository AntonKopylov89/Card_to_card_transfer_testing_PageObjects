package ru.netology.web.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.TransactionPage;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardToCardTransferTest {
    @BeforeEach
    void login() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }

    @AfterEach
    void returnCardBalancesToDefault() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
        var dashboardPage = new DashboardPage();
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        if (firstCardBalance < secondCardBalance) {
            dashboardPage.firstCardButtonClick();
            var transactionPage = new TransactionPage();
            transactionPage.validTransaction(String.valueOf((secondCardBalance - firstCardBalance) / 2),
                    DataHelper.getSecondCard().getNumber());
        } else if (firstCardBalance > secondCardBalance) {
            dashboardPage.secondCardButtonClick();
            var transactionPage = new TransactionPage();
            transactionPage.validTransaction(String.valueOf((firstCardBalance - secondCardBalance) / 2),
                    DataHelper.getFirstCard().getNumber());
        }
    }
    @AfterEach
    void closeWebBrowser() {
        closeWebDriver();
    }

    @Test
    void shouldTransferFromSecondCardToFirst() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstCardButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 3500;
        transactionPage.validTransaction(String.valueOf(amount), DataHelper.getSecondCard().getNumber());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        assertEquals(10000 + amount, firstCardBalance);
        assertEquals(10000 - amount, secondCardBalance);
    }

    @Test
    void shouldTransferFromFirstCardToSecond() {
        var dashboardPage = new DashboardPage();
        dashboardPage.secondCardButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0002"));
        var amount = 5000;
        transactionPage.validTransaction(String.valueOf(amount), DataHelper.getFirstCard().getNumber());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        assertEquals(10000 - amount, firstCardBalance);
        assertEquals(10000 + amount, secondCardBalance);
    }

    @Test
    void shouldTransferFromSecondCardToFirstFractionalAmount() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstCardButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 3500.50;
        transactionPage.validTransaction(String.valueOf(amount), DataHelper.getSecondCard().getNumber());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        assertEquals(10000 + amount, firstCardBalance);
        assertEquals(10000 - amount, secondCardBalance);
    }

    @Test
    void shouldNotTransferFromSecondCardToFirstOverCardBalance() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstCardButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 30000;
        transactionPage.invalidTransaction(String.valueOf(amount), DataHelper.getSecondCard().getNumber());
    }

    @Test
    void shouldNotTransferFromNotExistingCardToFirst() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstCardButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 8000;
        transactionPage.invalidTransaction(String.valueOf(amount), "5559 0000 0000 0003");
    }

    @Test
    void shouldCancelTransfer() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstCardButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 6000;
        transactionPage.cancelTransaction(String.valueOf(amount), DataHelper.getSecondCard().getNumber());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        assertEquals(10000, firstCardBalance);
        assertEquals(10000, secondCardBalance);
    }
}
