package br.com.eterniaserver.eternialib.uuidfetcher;

import br.com.eterniaserver.eternialib.uuidfetcher.enums.UUIDResponseStatus;
import br.com.eterniaserver.eternialib.uuidfetcher.impl.UUIDFetcherImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TestUUIDFetcher {

    private String playerName;
    private UUID playerUUID;

    private HttpClient httpClient;
    private UUIDFetcher uuidFetcher;

    @BeforeEach
    void setUp() {
        playerName = "yurinogueira";
        playerUUID = UUID.randomUUID();

        httpClient = Mockito.mock(HttpClient.class);

        uuidFetcher = new UUIDFetcherImpl(httpClient);
        uuidFetcher.cacheUUID(playerName, playerUUID);
    }

    @Test
    void testGetCachedUUID() {
        Optional<UUID> optionalUUID = uuidFetcher.getCachedUUID(playerName);

        Assertions.assertTrue(optionalUUID.isPresent());
        Assertions.assertEquals(playerUUID, optionalUUID.get());
    }

    @Test
    void testGetCachedName() {
        Optional<String> optionalName = uuidFetcher.getCachedName(playerUUID);

        Assertions.assertTrue(optionalName.isPresent());
        Assertions.assertEquals(playerName, optionalName.get());
    }

    @Test
    void testFetchUUIDCached() {
        uuidFetcher.fetchUUID(playerName, optionalResponse -> {
            Assertions.assertTrue(optionalResponse.isPresent());
            Assertions.assertEquals(playerUUID, optionalResponse.get().uuid());
        });
    }

    @Test
    void testFetchUUIDNotCached(@Mock HttpResponse<Object> response) throws IOException, InterruptedException {
        UUID uuid = UUID.randomUUID();

        String name = "tatanogueira";
        String plainUUID = uuid.toString().replace("-", "");

        String responseBody = "{\"id\":\"" + plainUUID + "\",\"name\":\"" + name + "\"}";

        Mockito.when(httpClient.send(Mockito.any(), Mockito.any())).thenReturn(response);
        Mockito.when(response.statusCode()).thenReturn(UUIDResponseStatus.OK.code());
        Mockito.when(response.body()).thenReturn(responseBody);

        uuidFetcher.fetchUUID(name, optionalResponse -> {
            Assertions.assertTrue(optionalResponse.isPresent());
            Assertions.assertEquals(uuid, optionalResponse.get().uuid());
        });
    }

    @Test
    void testFetchNameCached() {
        uuidFetcher.fetchName(playerUUID, optionalResponse -> {
            Assertions.assertTrue(optionalResponse.isPresent());
            Assertions.assertEquals(playerName, optionalResponse.get().name());
        });
    }

    @Test
    void testFetchNameNotCached(@Mock HttpResponse<Object> response) throws IOException, InterruptedException {
        UUID uuid = UUID.randomUUID();

        String name = "tatanogueira";
        String plainUUID = uuid.toString().replace("-", "");

        String responseBody = "{\"id\":\"" + plainUUID + "\",\"name\":\"" + name + "\"}";

        Mockito.when(httpClient.send(Mockito.any(), Mockito.any())).thenReturn(response);
        Mockito.when(response.statusCode()).thenReturn(UUIDResponseStatus.OK.code());
        Mockito.when(response.body()).thenReturn(responseBody);

        uuidFetcher.fetchName(uuid, optionalResponse -> {
            Assertions.assertTrue(optionalResponse.isPresent());
            Assertions.assertEquals(name, optionalResponse.get().name());
        });
    }

    @Test
    void testNotExistingUUID(@Mock HttpResponse<Object> response) throws IOException, InterruptedException {
        Mockito.when(httpClient.send(Mockito.any(), Mockito.any())).thenReturn(response);
        Mockito.when(response.statusCode()).thenReturn(UUIDResponseStatus.NOT_FOUND.code());

        uuidFetcher.fetchUUID("yurinogueira3", optionalResponse -> Assertions.assertTrue(optionalResponse.isEmpty()));
    }
}
