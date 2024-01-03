package ru.netology.sender;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.Test;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import java.util.HashMap;
import java.util.Map;

public class MessageSenderImplTest {
    @ParameterizedTest
    @CsvSource(value={
            "172.123.12.19, RUSSIA, true, Отправлено сообщение: Добро пожаловать",
            "172.333.11.22, RUSSIA, true, Отправлено сообщение: Добро пожаловать",
            "96.44.183.149, USA, false, Отправлено сообщение: Welcome"
    })

    void checkRussianLocalizationParametrized(String ipAddress, Country country, Boolean result, String trueMessage) {
        String message = "Отправлено сообщение: Добро пожаловать";
        GeoService geoService = Mockito.mock(GeoServiceImpl.class);
        Location location = new Location(null, country, null, 0);
        Mockito.when(geoService.byIp(ipAddress)).thenReturn(location);

        LocalizationService localizationService = Mockito.mock(LocalizationServiceImpl.class);
        Mockito.when(localizationService.locale(location.getCountry())).thenReturn(trueMessage);
        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ipAddress);
        Assertions.assertEquals(result, messageSender.send(headers).equals(message));
    }

    @ParameterizedTest
    @CsvSource(value={
            "172.123.12.19, RUSSIA, false, Отправлено сообщение: Добро пожаловать",
            "96.44.183.149, USA, true, Отправлено сообщение: Welcome",
            "96.11.222.333, USA, true, Отправлено сообщение: Welcome"
    })
    void checkUSALocalizationParametrized(String ipAddress, Country country, Boolean result, String trueMessage) {
        String message = "Отправлено сообщение: Welcome";
        GeoService geoService = Mockito.mock(GeoServiceImpl.class);
        Location location = new Location(null, country, null, 0);
        Mockito.when(geoService.byIp(ipAddress)).thenReturn(location);

        LocalizationService localizationService = Mockito.mock(LocalizationServiceImpl.class);
        Mockito.when(localizationService.locale(location.getCountry())).thenReturn(trueMessage);
        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ipAddress);
        Assertions.assertEquals(result, messageSender.send(headers).equals(message));
    }

    @ParameterizedTest
    @CsvSource(value={
            "172.123.12.19, RUSSIA",
            "172.333.11.22, RUSSIA",
            "96.44.183.149, USA",
            "96.11.222.333, USA"
    })
    void checkIpParametrized(String ipAddress, Country country){
        GeoService geoService = new GeoServiceImpl();
        GeoService geoServiceMoc = Mockito.mock(GeoServiceImpl.class);
        Location location = new Location(null, country, null, 0);
        Mockito.when(geoServiceMoc.byIp(ipAddress)).thenReturn(location);
        Assertions.assertEquals(geoService.byIp(ipAddress).getCountry(), geoServiceMoc.byIp(ipAddress).getCountry());
    }

    @ParameterizedTest
    @CsvSource(value={
            "RUSSIA, RUSSIA, Добро пожаловать, true",
            "USA, USA, Welcome, true",
            "RUSSIA, USA, Добро пожаловать, false",
            "USA, RUSSIA, Welcome, false"
    })
    void checkLocaleParametrized(Country testCountry, Country country, String message, Boolean result) {
        Location location = new Location(null, testCountry, null, 0);
        LocalizationService localizationServiceMoc = Mockito.mock(LocalizationServiceImpl.class);
        Mockito.when(localizationServiceMoc.locale(location.getCountry())).thenReturn(message);

        LocalizationService localizationService = new LocalizationServiceImpl();
        Boolean finalResult = localizationService.locale(country).equals(localizationServiceMoc.locale(location.getCountry()));

        Assertions.assertEquals(result, finalResult);
    }
}