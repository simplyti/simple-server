package com.networknt.techempower.handler;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.config.Config;
import com.networknt.techempower.Helper;
import com.networknt.techempower.db.mysql.MysqlStartupHookProvider;
import com.networknt.techempower.db.postgres.PostgresStartupHookProvider;
import com.networknt.techempower.model.World;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.sql.DataSource;

import static com.networknt.techempower.Helper.randomWorld;

public class QueriesPostgresqlGetHandler implements HttpHandler {
    private final DataSource ds = PostgresStartupHookProvider.ds;
    private DslJson<Object> dsl = new DslJson<>();
    private JsonWriter writer = dsl.newWriter(1024);

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        int queries = Helper.getQueries(exchange);

        World[] worlds = new World[queries];
        try (Connection connection = ds.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT * FROM World WHERE id = ?")) {
            for (int i = 0; i < worlds.length; i++) {
                statement.setInt(1, randomWorld());
                try (ResultSet resultSet = statement.executeQuery()) {
                    resultSet.next();
                    int id = resultSet.getInt("id");
                    int randomNumber = resultSet.getInt("randomNumber");
                    worlds[i] = new World(id, randomNumber);
                }
            }
        }

        writer.reset();
        writer.serialize(worlds);

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(writer.toByteArray()));
    }
}